package hexmax.alphabet;

// TODO: Check if interface implementation makes sense
// TODO: Try to refactor to record
public record Symbol(int value, String text, boolean[] bits) {

	public static final int TOTAL_PLACES = 7;

	@Override
	public String toString() {
		return text;
	}

}
