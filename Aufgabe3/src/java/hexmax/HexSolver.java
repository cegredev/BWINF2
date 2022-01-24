package hexmax;

import hexmax.alphabet.Alphabet;

import java.util.Arrays;

public class HexSolver {

	private final int maxMoves;
	private final Alphabet alphabet;
	private final Digit[] digits;

	private int moves, changes;
	private int maxMinBorder;

	public HexSolver(HexConfig config, Alphabet alphabet) {
		this.maxMoves = config.maxMoves();
		this.digits = Arrays.stream(config.digits()).map(Digit::new).toList().toArray(new Digit[0]);
		this.alphabet = alphabet;
	}

	private void maximize() {
		for (int i = 0; i < digits.length; i++) {
			var digit = digits[i];

			for (var higher : alphabet.highestValueToLowest()) {
				if (digit.getValue() >= higher.value())
					break;

				var conversion = alphabet.convert(digit.getValue(), higher.value());

				if (moves + conversion.getTotalMoves() <= maxMoves) {
					moves += conversion.getTotalMoves();
					changes += conversion.getChanges();
					digit.set(higher.value(), conversion);
					break;
				}
			}

			// If all moves have been used up, this phase is completed
			if (moves == maxMoves) {
				maxMinBorder = i;
				break;
			}
		}

		System.out.println("Border: " + maxMinBorder);
	}

	public Digit[] solve() {
		maximize();

		while (changes != 0) {
			break;
		}

		return digits;
	}
}
