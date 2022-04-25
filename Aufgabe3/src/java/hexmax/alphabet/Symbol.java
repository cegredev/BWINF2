package hexmax.alphabet;

/**
 * Eine zu einem Alphabet gehörende Ziffer.
 *
 * @param bits Die Anordnung der Stellen.
 */
public record Symbol(int value, String text, boolean[] bits) {

	public static final int TOTAL_PLACES = 7;

	public Symbol(int value, String text, boolean[] bits) {
		this.value = value;
		this.text = text;

		if (bits.length != TOTAL_PLACES)
			throw new IllegalArgumentException("Ziffer besteht nicht aus vorgegebener Anzahl an Stellen!");

		this.bits = bits;
	}

	/**
	 * @return Die Anzahl an belegten Stellen in dieser Ziffer.
	 */
	public int countSetBits() {
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
