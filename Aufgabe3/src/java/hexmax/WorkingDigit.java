package hexmax;

import hexmax.alphabet.Alphabet;
import hexmax.alphabet.Digit;

public class WorkingDigit {

	private final int originalValue;

	private int value;
	private Alphabet.Conversion conversion;

	public WorkingDigit(int value) {
		this.originalValue = value;
		this.value = value;
	}

	public WorkingDigit(Digit digit) {
		this(digit.getValue());
	}

	public void set(int value, Alphabet.Conversion conversion) {
		this.value = value;
		this.conversion = conversion;
	}

	private static char numToSymbol(int num) {
		if (num < 10) {
			return (char) ('0' + num);
		} else {
			return (char) ('A' - 10 + num);
		}
	}

	public String getTargetSymbol() {
		return "" + numToSymbol(value);
	}

	public int getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "D[v=" + numToSymbol(originalValue) + ",t=" + getTargetSymbol() + "]";
	}

	public Alphabet.Conversion getConversion() {
		return conversion;
	}
}
