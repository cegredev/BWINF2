package hexmax.alphabet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Alphabet {

	private final Digit[] symbols; // Um Ziffern schnell nach ihrem Wert finden zu können
	private final Map<String, Digit> symbolLookup; // Um Ziffern schnell nach ihrem Symbol finden zu können
	private final Conversion[][] conversions;

	public Alphabet(Digit[] symbols) {
		this.symbols = symbols;

		symbolLookup = new HashMap<>(symbols.length);
		for (var symbol : symbols)
			symbolLookup.put(symbol.getSymbol(), symbol);

		conversions = new Conversion[symbols.length][symbols.length];

		// TODO: This very likely works, but... maybe check all combinations? Thank youuu
		for (int i = 0; i < symbols.length; i++) {
			boolean[] from = symbols[i].getBits();

			for (int j = 0; j < symbols.length; j++) {
				boolean[] to = symbols[j].getBits();

				int moves = 0, changes = 0;
				for (int bitIndex = 0; bitIndex < from.length; bitIndex++) {
					int difference = binary(to[bitIndex]) - binary(from[bitIndex]);

					if (difference != 0) {
						moves++;
						changes += difference;
					}
				}

				int switches = (moves - Math.abs(changes)) / 2;
				conversions[i][j] = new Conversion(switches, changes);
			}
		}
	}

	public static Alphabet readFrom(InputStream input) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
			final char SET_BIT = '1';
			var digits = new ArrayList<Digit>();

			String line;
			for (int value = 0; (line = reader.readLine()) != null; value++) {
				var split = line.split(":");
				if (split.length != 2)
					throw new IllegalArgumentException("Wrongly formatted line!");

				String representation = split[1].trim();
				if (representation.length() != Digit.TOTAL_PLACES)
					throw new IllegalArgumentException("Wrong representation!");

				var bits = new boolean[representation.length()];
				for (int i = 0; i < bits.length; i++)
					bits[i] = representation.charAt(i) == SET_BIT;

				var digit = new ReadDigit(value, bits, split[0]);
				digits.add(digit);
			}

			input.close();
			return new Alphabet(digits.toArray(new Digit[0]));
		}
	}

	public static Alphabet fromFile(Path path) throws IOException {
		return readFrom(Files.newInputStream(path));
	}

	public Digit[] stringToNum(CharSequence sequence) {
		var digits = new Digit[sequence.length()];

		for (int i = 0; i < digits.length; i++)
			digits[i] = symbolLookup.get(String.valueOf(sequence.charAt(i)));

		return digits;
	}

	private static int binary(boolean bool) {
		return bool ? 1 : 0;
	}

	public Iterable<Digit> highestValueToLowest() {
		return () -> new Iterator<>() {

			private int counter = symbols.length - 1;

			@Override
			public boolean hasNext() {
				return counter >= 0;
			}

			@Override
			public Digit next() {
				return symbols[counter--];
			}
		};
	}

	public Conversion change(int from, int to) {
		return conversions[from][to];
	}

	public static class Conversion {

		private final int switches;
		private final int changes;
		private final int totalMoves;

		private Conversion(int switches, int changes) {
			this.switches = switches;
			this.changes = changes;

			this.totalMoves = switches + Math.abs(changes);
		}

		public int getSwitches() {
			return switches;
		}

		public int getChanges() {
			return changes;
		}

		public int getTotalMoves() {
			return totalMoves;
		}

		@Override
		public String toString() {
			return "Con[switches=" + switches + ",changes=" + changes + "]";
		}

	}

}
