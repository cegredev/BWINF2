package hexmax.alphabet;

import java.util.Arrays;

// TODO: Check if interface implementation makes sense
// TODO: Try to refactor to record
public record Symbol(int value, String text, boolean[] bits) {

	public static final int TOTAL_PLACES = 7;

	public int getSetBits() {
		int count = 0;

		for (boolean bit : bits)
			if (bit) count++;

		return count;
	}

	@Override
	public String toString() {
		return text;
	}

}
