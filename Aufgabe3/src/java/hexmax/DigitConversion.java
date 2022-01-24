package hexmax;

public record DigitConversion(int additions, int removals) {

	public int totalMoves() {
		return Math.max(additions, removals);
	}

}
