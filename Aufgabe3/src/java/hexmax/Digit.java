package hexmax;

import hexmax.alphabet.Symbol;

/**
 * Repräsentiert eine Ziffer.
 */
public class Digit {

	private final int originalValue;

	private int value, additions, removals, maximum;

	public Digit(int value) {
		this.originalValue = value;
		this.value = value;
		this.maximum = HexMax.getConfig().alphabet().getMaxValue();
	}

	public Digit(Symbol symbol) {
		this(symbol.value());
	}

	/**
	 * Wandelt diese Ziffer in eine andere um.
	 */
	public void convert(SymbolConversion conversion) {
		this.value = conversion.target().value();
		this.additions = conversion.additions();
		this.removals = conversion.removals();
	}

	/**
	 * @return Der Wert, der für diese Ziffer angestrebt wird.
	 */
	public int getValue() {
		return value;
	}

	public int getOriginalValue() {
		return originalValue;
	}

	/**
	 * @return Die Anzahl an Ergänzungen an Stellen für diese Ziffer.
	 */
	public int getAdditions() {
		return additions;
	}

	/**
	 * @return Die Anzahl an Entnahmen von Stellen für diese Ziffer.
	 */
	public int getRemovals() {
		return removals;
	}

	/**
	 * @return Der maximale Wert, den diese Ziffer haben kann.
	 */
	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	@Override
	public String toString() {
		return HexMax.getConfig().alphabet().getSymbols()[getValue()].toString();
	}

}
