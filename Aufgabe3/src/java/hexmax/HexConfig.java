package hexmax;

import hexmax.alphabet.Alphabet;
import hexmax.alphabet.Symbol;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public record HexConfig(int maxMoves, Symbol[] digits) {

	public static HexConfig fromFile(Path path, Alphabet alphabet) throws IOException {
		try (var reader = Files.newBufferedReader(path)) {
			Symbol[] digits = alphabet.stringToNum(reader.readLine());

			int maxMoves = Integer.parseInt(reader.readLine());

			int setBits = Arrays.stream(digits).mapToInt(Symbol::getSetBits).sum();
			int unsetBits = Symbol.TOTAL_PLACES * digits.length - setBits;

			// Es können nie mehr Bits bewegt werden, als es zur Verfügung stehen, aber können
			// auch nie mehr Bits bewegt werden, als freie Plätze zur Verfügung stehen
			maxMoves = Math.min(maxMoves, Math.min(setBits, unsetBits));

			return new HexConfig(maxMoves, digits);
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(digits) + " " + maxMoves;
	}

}
