package hexmax;

import hexmax.alphabet.Alphabet;
import hexmax.alphabet.Symbol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Die Konfiguration für das zu lösende Beispiel.
 */
public record HexConfig(int maxMoves, Symbol[] digits, Alphabet alphabet) {

	public static HexConfig readFrom(InputStream exerciseIn, InputStream alphabetIn) throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(exerciseIn, StandardCharsets.UTF_8))) {
			var alphabet = Alphabet.readFrom(alphabetIn);

			Symbol[] digits = alphabet.stringToNum(reader.readLine());

			int maxMoves = Integer.parseInt(reader.readLine());

			int setBits = Arrays.stream(digits).mapToInt(Symbol::countSetBits).sum();
			int unsetBits = Symbol.TOTAL_PLACES * digits.length - setBits;

			// Es können nie mehr Stellen bewegt werden, als es zur Verfügung stehen, aber es können
			// auch nie mehr Stellen bewegt werden, als freie Plätze zur Verfügung stehen
			maxMoves = Math.min(maxMoves, Math.min(setBits, unsetBits));

			return new HexConfig(maxMoves, digits, alphabet);
		}
	}

	@Override
	public String toString() {
		return Arrays.toString(digits) + " " + maxMoves;
	}

}
