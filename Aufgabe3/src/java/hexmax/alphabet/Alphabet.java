package hexmax.alphabet;

import hexmax.DigitConversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Alphabet {

	private final Symbol[] symbols; // Um Ziffern schnell nach ihrem Wert finden zu können
	private final Map<String, Symbol> symbolLookup; // Um Ziffern schnell nach ihrem Symbol finden zu können
	private final DigitConversion[][] conversions; // Conversions zwischen Werten

	public Alphabet(Symbol[] symbols) {
		this.symbols = symbols;

		symbolLookup = new HashMap<>(symbols.length);
		for (var symbol : symbols)
			symbolLookup.put(symbol.text(), symbol);

		conversions = new DigitConversion[symbols.length][symbols.length];

		// TODO: This very likely works, but... maybe check all combinations? Thank youuu
		for (int i = 0; i < symbols.length; i++) {
			boolean[] from = symbols[i].bits();

			for (int j = 0; j < symbols.length; j++) {
				boolean[] to = symbols[j].bits();

				int additions = 0, removals = 0;
				for (int bitIndex = 0; bitIndex < from.length; bitIndex++) {
					if (from[bitIndex] != to[bitIndex])
						if (from[bitIndex])
							removals++;
						else
							additions++;
				}

				conversions[i][j] = new DigitConversion(additions, removals);
			}
		}
	}

	public static Alphabet readFrom(InputStream input) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
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

	public static Alphabet fromFile(Path path) throws IOException {
		return readFrom(Files.newInputStream(path));
	}

	public Symbol[] stringToNum(CharSequence sequence) {
		var digits = new Symbol[sequence.length()];

		for (int i = 0; i < digits.length; i++)
			digits[i] = symbolLookup.get(String.valueOf(sequence.charAt(i)));

		return digits;
	}

	public Iterable<Symbol> highestValueToLowest() {
		return () -> new Iterator<>() {

			private int counter = symbols.length - 1;

			@Override
			public boolean hasNext() {
				return counter >= 0;
			}

			@Override
			public Symbol next() {
				return symbols[counter--];
			}
		};
	}

	public DigitConversion convert(int from, int to) {
		return conversions[from][to];
	}

}
