package hexmax.alphabet;

// TODO: Check if interface implementation makes sense
// TODO: Try to refactor to record
public class ReadDigit implements Digit {

	private final int value;
	private final boolean[] bits;
	private final String symbol;

	public ReadDigit(int value, boolean[] bits, String symbol) {
		this.value = value;
		this.bits = bits;
		this.symbol = symbol;
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public boolean[] getBits() {
		return bits;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String toString() {
		return getSymbol();
	}

}
