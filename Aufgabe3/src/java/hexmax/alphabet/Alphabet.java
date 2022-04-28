package hexmax.alphabet;

import hexmax.SymbolConversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

/**
 * Hier werden die Zeichen des verwendeten Alphabets gespeichert. Ein Alphabet könnte die
 * Hexadezimal oder auch Binär-Darstellung sein.
 */
public class Alphabet {

	private final Symbol[] symbols; // Um Ziffern schnell nach ihrem Wert finden zu können
	private final SymbolConversion[][] conversions; // Umwandlung zwischen Werten
	// Um Ziffern schnell nach ihrem Symbol finden zu können
	private final Map<String, Symbol> symbolLookup;
	// Umwandlungen zwischen Werten, sortiert nach Ergänzungen
	private final List<List<SymbolConversion>> conversionsByAdditions;
	// Umwandlungen zwischen Werten, sortiert nach Entnahmen
	private final List<List<SymbolConversion>> conversionsByRemovals;

	public Alphabet(Symbol[] symbols) {
		this.symbols = symbols;

		// Speichert die Symbole nach ihrem Zeichen
		symbolLookup = new HashMap<>(symbols.length);
		for (var symbol : symbols)
			symbolLookup.put(symbol.text(), symbol);

		// Speichert die Umwandlungen nach ihren Werten
		conversions = new SymbolConversion[symbols.length][symbols.length];

		for (int i = 0; i < symbols.length; i++) {
			boolean[] from = symbols[i].bits();

			for (int j = 0; j < symbols.length; j++) {
				var toSymbol = symbols[j];
				boolean[] to = toSymbol.bits();

				// Speichert für die Umwandlung welche Stellen fehlen und welche zu viel sind
				List<Integer> missingBits = new ArrayList<>(Symbol.TOTAL_PLACES),
						spareBits = new ArrayList<>(Symbol.TOTAL_PLACES);

				int additions = 0, removals = 0;
				for (int bitIndex = 0; bitIndex < from.length; bitIndex++) {
					// Wenn zwei Stellen unterschiedlich sind...
					if (from[bitIndex] != to[bitIndex]) {
						// ...und die Stelle ursprünglich besetzt ist, entferne diese Stelle.
						if (from[bitIndex]) {
							removals++;
							spareBits.add(bitIndex);
						} else {
							// Ansonsten füge eine hinzu.
							additions++;
							missingBits.add(bitIndex);
						}
					}
				}

				// Die Umwandlung von Wert i nach Wert j ist...
				conversions[i][j] = new SymbolConversion(additions, removals, toSymbol,
						missingBits, spareBits);
			}
		}

		// Generiert sortierte Listen für die Umwandlungen benötigt für die balance Methode
		conversionsByAdditions = conversionsSorted(SymbolConversion::additions,
				SymbolConversion::removals);
		conversionsByRemovals = conversionsSorted(SymbolConversion::removals,
				SymbolConversion::additions);
	}

	/**
	 * Liest ein Alphabet von einem InputStream ein.
	 */
	public static Alphabet readFrom(InputStream input) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(input,
				StandardCharsets.UTF_8))) {
			final char SET_BIT = '1'; // Steht für eine aktive Stelle im 7-Segment-Display
			var symbols = new ArrayList<Symbol>();

			String line;
			for (int value = 0; (line = reader.readLine()) != null; value++) {
				var split = line.split(":");
				if (split.length != 2)
					throw new IllegalArgumentException("Wrongly formatted line!");

				String representation = split[1].trim();
				if (representation.length() != Symbol.TOTAL_PLACES)
					throw new IllegalArgumentException("Wrong representation!");

				var bits = new boolean[representation.length()];
				for (int i = 0; i < bits.length; i++)
					bits[i] = representation.charAt(i) == SET_BIT;

				symbols.add(new Symbol(value, split[0], bits));
			}

			return new Alphabet(symbols.toArray(new Symbol[0]));
		}
	}

	/**
	 * Sortiert die Umwandlungen auf eine Weise, das für entweder fehlende Ergänzungen oder
	 * Entnahmen, die optimale Umwandlung oben ist.
	 */
	private List<List<SymbolConversion>> conversionsSorted(Function<SymbolConversion, Integer> primary,
														   Function<SymbolConversion, Integer> secondary) {
		// Hier werden die sortieren Listen drin gespeichert
		var totalList = new ArrayList<List<SymbolConversion>>(symbols.length);

		for (var arr : conversions) {
			var list =
					new ArrayList<>(Arrays.stream(arr).sorted(Comparator.comparingInt(primary::apply)).toList());

			for (int i = list.size() - 1; i > 0; i--) {
				var conversion = list.get(i);
				var other = list.get(i - 1);

				// Wenn entweder die Ergänzungen oder Entnahmen (primary) gleich sind...
				if (primary.apply(conversion).equals(primary.apply(other))) {
					// ...entferne das Element mit dem größten Gegenteil (secondary ->
					// Entnahmen/Ergänzungen).
					// Dadurch bekommt man nicht nur die maximale Anzahl von der Resource, die
					// man braucht, sondern
					// auch die geringste von der, die man nicht braucht.
					if (secondary.apply(conversion) > secondary.apply(other)) {
						list.remove(i);
					} else {
						list.remove(i - 1);
					}
				}
			}

			totalList.add(list);
		}

		return totalList;
	}

	/**
	 * Konvertiert eine Liste an Charakteren zu einem Array von passenden Symbolen aus diesem
	 * Alphabet.
	 */
	public Symbol[] stringToNum(CharSequence sequence) {
		var digits = new Symbol[sequence.length()];

		for (int i = 0; i < digits.length; i++) {
			var digit = digits[i] = symbolLookup.get(String.valueOf(sequence.charAt(i)));

			if (digit == null)
				throw new IllegalArgumentException("Die Aufgaben-Datei passt nicht zum " +
						"gewählten Alphabet.");
		}

		return digits;
	}

	public SymbolConversion convert(int from, int to) {
		return conversions[from][to];
	}

	public Symbol getSymbolByValue(int value) {
		return symbols[value];
	}

	public List<SymbolConversion> getAllConversionsByAdditions(int from) {
		return conversionsByAdditions.get(from);
	}

	public List<SymbolConversion> getAllConversionsByRemovals(int from) {
		return conversionsByRemovals.get(from);
	}

	public int getMaxValue() {
		return symbols.length - 1;
	}

	public Symbol[] getSymbols() {
		return symbols;
	}

}
