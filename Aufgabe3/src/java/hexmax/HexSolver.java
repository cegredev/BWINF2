package hexmax;

import hexmax.alphabet.Alphabet;

import java.util.Arrays;

public class HexSolver {

	private final int maxMoves;
	private final Alphabet alphabet;
	private final WorkingDigit[] digits;

	private int moves, changes;
	private int maxMinBorder;

	public HexSolver(HexConfig config, Alphabet alphabet) {
		this.maxMoves = config.maxMoves();
		this.digits = Arrays.stream(config.digits()).map(WorkingDigit::new).toList().toArray(new WorkingDigit[0]);
		this.alphabet = alphabet;
	}

	private void maximize() {
		for (int i = 0; i < digits.length; i++) {
			var digit = digits[i];

			for (var higher : alphabet.highestValueToLowest()) {
				if (digit.getValue() >= higher.getValue())
					break;

				var conversion = alphabet.change(digit.getValue(), higher.getValue());

				if (moves + conversion.getTotalMoves() <= maxMoves) {
					moves += conversion.getTotalMoves();
					changes += conversion.getChanges();
					digit.set(higher.getValue(), conversion);
					break;
				}
			}

			// If all maxMoves have been used up, this phase is completed
			if (moves == maxMoves) {
				maxMinBorder = i;
				break;
			}
		}
	}

	public WorkingDigit[] solve() {
		maximize();

		return digits;
	}
}
