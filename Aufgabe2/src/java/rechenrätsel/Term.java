package rechenrätsel;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection of * or / connected operands.
 */
public class Term {

	private static final double DIV_CHANCE = 0.2;

	private final int[] operands;
	private final Operator[] operators;

	private int result;

	public Term(int size) {
		this.operands = new int[size];
		this.operators = new Operator[size - 1];
	}

	private static Operator genOperator() {
		if (Math.random() < 0.1) {
			return Operator.SUBTRACT;
		} else if (Math.random() < 0.3) {
			return Operator.ADD;
		} else if (Math.random() < 0.4) {
			return Operator.DIVIDE;
		} else {
			return Operator.MULTIPLY;
		}
	}

	public Term populate() {
		var random = ThreadLocalRandom.current();
		int acc = random.nextInt(1, 10);

		var usedFactors = Map.of(Operator.MULTIPLY, new HashSet<>(Set.of(acc)), Operator.DIVIDE,
				new HashSet<Integer>());

		operands[0] = acc;
		for (int i = 1; i < operands.length; i++) {
			Operator operator;
			int operand;

			do {
				operator = Math.random() < DIV_CHANCE ? Operator.DIVIDE : Operator.MULTIPLY;
				operand = random.nextInt(2, 10);

				// Bsp.: 3 / 2
				if (!operator.mayOperate(acc, operand))
					continue;

				var set = usedFactors.get(operator);
				var oppositeSet = usedFactors.get(operator.getOpposite());

//				System.out.println("Trying to generate index " + i + " with operator " + operator + " and operand "
//						+ operand + " on set " + set + " and oppositeSet " + oppositeSet);

				final int operandFinal = operand;
				if (set.stream().anyMatch(value -> oppositeSet.contains(value * operandFinal)))
					continue;

				if (!oppositeSet.contains(operand))
					break;
			} while (true);

			var set = usedFactors.get(operator);
			int operandFinal = operand;
			set.addAll(set.stream().map(val -> val * operandFinal).toList());
			set.add(operand);

			acc = operator.calculate(acc, operand);

			operands[i] = operand;
			operators[i - 1] = operator;
		}

		this.result = acc;

		return this;
	}

	public String termAsString() {
		var builder = new StringBuilder();

		builder.append(operands[0]);
		for (int i = 1; i < operands.length; i++)
			builder.append(operators[i - 1]).append(operands[i]);

		return builder.toString();
	}

	public int getResult() {
		return result;
	}

	@Override
	public String toString() {
		return termAsString() + "=" + getResult();
	}

}
