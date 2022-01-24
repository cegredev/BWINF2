package hexmax;

import hexmax.alphabet.Alphabet;

import java.util.Arrays;

public class HexSolver {

	private final int maxMoves;
	private final Alphabet alphabet;
	private final Digit[] digits;

	private int additions, removals;
	private int maxMinBorder;

	public HexSolver(HexConfig config, Alphabet alphabet) {
		this.maxMoves = config.maxMoves();
		this.digits = Arrays.stream(config.digits()).map(Digit::new).toList().toArray(new Digit[0]);
		this.alphabet = alphabet;
	}

	private int totalMoves() {
		return Math.max(additions, removals);
	}

	private void maximize() {
		for (int i = 0; i < digits.length; i++) {
			var digit = digits[i];

			for (var target : alphabet.highestValueToLowest()) {
				if (target.value() <= digit.getValue())
					break;

				DigitConversion conversion = alphabet.convert(digit.getValue(), target.value());
				if (totalMoves() + conversion.totalMoves() <= maxMoves) {
					additions += conversion.additions();
					removals += conversion.removals();
					digit.set(target.value(), conversion);
					break;
				}
			}

			// If all moves have been used up, this phase is completed
			if (totalMoves() == maxMoves) {
				maxMinBorder = i;
				break;
			}
		}

		System.out.println("Border: " + maxMinBorder);
	}

	public Digit[] solve() {
		maximize();

		while (additions != removals) {
			break;
		}

		System.out.println("Finished solving. Final stats: additions: " + additions + " removals: " + removals);

		return digits;
	}
}
