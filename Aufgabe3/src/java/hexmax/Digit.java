package hexmax;

import hexmax.alphabet.Symbol;

public class Digit {

	private final int originalValue;

	private int value, additions, removals, maximum;

	public Digit(int value) {
		this.originalValue = value;
		this.value = value;
		this.maximum = HexMax.alphabet.getMaxValue();
	}

	public Digit(Symbol symbol) {
		this(symbol.value());
	}

	private static char numToSymbol(int num) {
		if (num < 10) {
			return (char) ('0' + num);
		} else {
			return (char) ('A' - 10 + num);
		}
	}

	public void set(int value, SymbolConversion conversion) {
		this.value = value;
		this.additions = conversion.additions();
		this.removals = conversion.removals();
	}

	public String getTargetSymbol() {
		return "" + numToSymbol(value);
	}

	public int getValue() {
		return value;
	}

	public int getOriginalValue() {
		return originalValue;
	}

	public int getAdditions() {
		return additions;
	}

	public int getRemovals() {
		return removals;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	@Override
	public String toString() {
		return "D[v=" + numToSymbol(originalValue) + ",t=" + getTargetSymbol() + "]";
	}

}
