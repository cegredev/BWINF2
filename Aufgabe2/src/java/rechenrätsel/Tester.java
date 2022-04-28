package rechenrätsel;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Tester {

	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	private static final double NOTIFY_PERCENTAGE = 0.05;
	private final long neededCycles, notifyThreshold;
	private final String[] formatColors;
	private final List<Operator[]> solutions = new ArrayList<>();
	private final Equation equation;
	private final Operator[] globalOperators;
	private long cycles = 0;

	Tester(Equation equation, Operator[] operators) {
		this.neededCycles = (long) Math.pow(4, equation.getOperands().length - 1);
		this.notifyThreshold = (int) Math.ceil(neededCycles * NOTIFY_PERCENTAGE);
		this.formatColors = new String[equation.getOperands().length - 1];
		this.equation = equation;
		this.globalOperators = operators;

		for (int i = 0; i < formatColors.length; i++)
			formatColors[i] = ANSI_GREEN;

		solve(equation);
	}

	private Tester(Equation equation) {
		this(equation, Operator.values());
	}

	private static void runSingleAnalysis(String equationStr, Operator[] operators) {
		if (equationStr == null) {
			var scanner = new Scanner(System.in);

			System.out.print("Please enter an equation: ");

			equationStr = scanner.nextLine();
			scanner.close();
		}

		var equation = parse(equationStr);
		var tester = new Tester(equation, operators);
		tester.printAnalysis();
		System.out.println(ANSI_RESET);
	}

	public static void main(String[] args) throws Exception {
		runSingleAnalysis(null, Operator.values());
//		runSingleAnalysis("5 6 5=6", Operator.values());
	}

	private static String color(int amount) {
		String color;

		if (amount == 1) {
			color = ANSI_GREEN;
		} else if (amount == 2) {
			color = ANSI_YELLOW;
		} else {
			color = ANSI_RED;
		}

		return color + amount + ANSI_RESET;
	}

	private static int evaluate(Equation equation, Operator[] operators) {
		if (equation.getOperands().length - 1 != operators.length)
			throw new IllegalArgumentException(
					"Mismatched lengths " + equation.getOperands().length + " and " + operators.length);

		int[] operands = equation.getOperands();
		int result = operands[0];

		for (int i = 0; i < operators.length; i++) {
			var operator = operators[i];
			var operand = operands[i + 1];

			if (!operator.hasPriority()) {
				int j;
				for (j = i + 1; j < operators.length; j++) {
					Operator nextOperator = operators[j];
					int nextOperand = operands[j + 1];
					if (!nextOperator.hasPriority())
						break;

					if (nextOperator.canOperate(operand, nextOperand)) {
						operand = nextOperator.calculate(operand, nextOperand);
//						System.out.println("Operand: " + operand);
					} else {
//						System.out.println("Couldn't operate on next");
						return -1;
					}
				}

				i = j - 1;
			}

			if (operator.canOperate(result, operand)) {
				result = operator.calculate(result, operand);
			} else { // Invalid
//				System.out.println("Couldn't operate on self");
//				System.out.println("result: " + result + " Operand:" + operand);
				return -1;
			}
		}

		return result;
	}

	private static String format(Equation equation, Operator[] operators, String[] opColors) {
		int[] operands = equation.getOperands();

		var builder = new StringBuilder(operands.length * 2);

		for (int i = 0; i < operands.length; i++) {
			builder.append(operands[i]);

			if (i < operators.length)
				builder.append(opColors[i] + operators[i] + ANSI_RESET);
		}

		builder.append('=');
		builder.append(equation.getResult());

		return builder.toString();
	}

	static Equation parse(String equation) {
		var list = new ArrayList<Integer>(equation.length() / 2);

		equation = equation.trim();
		String[] sides = equation.split("=");

		for (char c : sides[0].toCharArray()) {
			try {
				list.add(Integer.parseInt(String.valueOf(c)));
			} catch (NumberFormatException e) {
				continue;
			}
		}

		int[] operands = list.stream().mapToInt(Integer::intValue).toArray();

		return new Equation(operands, Integer.parseInt(sides[1].trim()));
	}

	public void printAnalysis() {
		int amount = solutions.size();
		System.out.println("There were " + color(amount) + " solutions:");

		if (amount > 0) {
			Operator[] original = solutions.get(0);
			Map<Operator, Integer> originalCount = new HashMap<>(4), count = new HashMap<>(4);
			String[] sameAmountColors = new String[original.length], diffAmountColors =
					new String[original.length];

			System.out.println("(0) " + format(equation, original));

			for (int i = 0; i < sameAmountColors.length; i++) {
				sameAmountColors[i] = ANSI_YELLOW;
				diffAmountColors[i] = ANSI_RED;
			}

			for (var op : original)
				originalCount.put(op, originalCount.getOrDefault(op, 0) + 1);

			for (int i = 1; i < amount; i++) {
				Operator[] next = solutions.get(i);

				for (var op : next)
					count.put(op, count.getOrDefault(op, 0) + 1);

				System.out.println("(" + i + ") "
						+ format(equation, next, originalCount.equals(count) ?
						sameAmountColors : diffAmountColors)
						+ ANSI_RESET);

				count.clear();
			}
		}
	}

	private void solve(Equation equation, Operator[] operators, int index) {
		if (index == operators.length) {
			int result = evaluate(equation, operators);
//			System.out.println(format(equation, operators));
//			System.out.println("Result: " + result);
			// cycles++;
			// if (cycles % notifyThreshold == 0)
			// System.out.println((int) (cycles / (double) neededCycles * 100) + "% done.");

			if (result == equation.getResult()) {
				solutions.add(operators.clone());
//				System.out.println("Solved with " + format(equation, operators));
			}
		} else {
			for (var operator : globalOperators) {
				operators[index] = operator;
				solve(equation, operators, index + 1);
			}
		}
	}

	private void solve(Equation equation) {
		solve(equation, new Operator[equation.getOperands().length - 1], 0);
	}

	private String format(Equation equation, Operator[] operators) {
		return format(equation, operators, formatColors);
	}

	public List<Operator[]> getSolutions() {
		return solutions;
	}

	private static class Equation {

		private final int result;
		private final int[] operands;

		public Equation(int[] operands, int result) {
			if (operands.length <= 0)
				throw new IllegalArgumentException("No operands");

			this.operands = operands;
			this.result = result;
		}

		public int[] getOperands() {
			return operands;
		}

		public int getResult() {
			return result;
		}

		@Override
		public String toString() {
			return Arrays.toString(operands) + " = " + result;
		}

	}

}