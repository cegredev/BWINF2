package hexmax;

import hexmax.alphabet.Alphabet;
import hexmax.alphabet.Symbol;

import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HexSolver {

	private final int maxMoves;
	private final Alphabet alphabet;
	private final Digit[] digits;

	private int additions, removals;

	public HexSolver(HexConfig config, Alphabet alphabet) {
		this.maxMoves = config.maxMoves();
		this.digits = Arrays.stream(config.digits()).map(Digit::new).toList().toArray(new Digit[0]);
		this.alphabet = alphabet;
	}

	private static Comparator<SymbolConversion> conversionComparator(Function<SymbolConversion, Integer> getter) {
		return Comparator.comparingInt(getter::apply);
	}

	private int totalMoves() {
		return Math.max(additions, removals);
	}

	/**
	 * Optimistically increase every digit to its maximum (cap) value.
	 *
	 * @param start
	 * @return W
	 */
	private boolean maximize(int start) {
		for (int i = start; i < digits.length; i++) {
			var digit = digits[i];
			int original = digit.getOriginalValue();

			// This chooses the best possible conversion for this digit
			for (int target = digit.getMaximum(); target > original; target--) {
				var conversion = alphabet.convert(original, target);

				int additionDiff = conversion.additions() - digit.getAdditions();
				int removalDiff = conversion.removals() - digit.getRemovals();
				// Check if the conversion is possible and if so, choose it
				if (additions + additionDiff <= maxMoves && removals + removalDiff <= maxMoves) {
					additions += additionDiff;
					removals += removalDiff;
					digit.set(target, conversion);
					break;
				}
			}

			// If all moves have been used up, this phase is completed
			// If this never becomes true, the method just runs through all digits and then returns whether additions
			// are equal to removals
			if (additions == maxMoves && removals == maxMoves)
				return true;
		}

		// If we got to the end, but didn't use all of our moves, this means we're still done, as long as we have an
		// equal amount of additions and removals
		return additions == removals;
	}

	private int balance(int stop) {
		boolean additionsWereGreater = additions > removals;

		for (int i = digits.length - 1; i >= stop; i--) {
			var digit = digits[i];
			int original = digit.getOriginalValue();

			var conversions = additionsWereGreater ?
					alphabet.getAllConversionsByRemovals(original)
					: alphabet.getAllConversionsByAdditions(original);

			for (int j = conversions.size() - 1; j >= 0; j--) {
				var conversion = conversions.get(j);

				int additionDiff = conversion.additions() - digit.getAdditions();
				int removalDiff = conversion.removals() - digit.getRemovals();

				// Check if the conversion is possible and if so, choose it
				if (additions + additionDiff <= maxMoves && removals + removalDiff <= maxMoves) {
					additions += additionDiff;
					removals += removalDiff;
					digit.set(conversion.target().value(), conversion);
					break;
				}
			}

			if ((additionsWereGreater && removals >= additions) || (!additionsWereGreater && additions >= removals))
				return i;
		}

		return stop;
	}

	public Digit[] solve() {
		int start = 0;

		int iterations = 0;

		// PHASE 1: Alle Digits zu ihrer größtmöglichen Darstellung bekommen
		while (!maximize(start)) {
			System.out.println("Completed maximizing: additions: " + additions + " removals: " + removals + " state: " + String.join("", Arrays.stream(digits).map(Digit::getTargetSymbol).toList()));

			// PHASE 2
			int newStart = balance(start);
			System.out.println("Completed balancing: additions: " + additions + " removals: " + removals + " state: " + String.join("", Arrays.stream(digits).map(Digit::getTargetSymbol).toList()));

			if (newStart == start)
				digits[start].setMaximum(digits[start].getMaximum() - 1);

			start = newStart;

//			if (iterations++ >= 10) break;
		}

		System.out.println("Finished solving. Final stats: additions: " + additions + " removals: " + removals);

		return digits;
	}
}
