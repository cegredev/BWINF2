package hexmax;

import hexmax.alphabet.Alphabet;

import java.nio.file.Path;
import java.util.Arrays;

public class HexMax {

	public static Alphabet alphabet;

	public static void main(String[] args) throws Exception {
		alphabet = Alphabet.readFrom(HexMax.class.getResourceAsStream("hex_alphabet.txt"));
		var hexConfig = HexConfig.fromFile(Path.of("samples/hexmax5.txt"), alphabet);

		System.out.print("Alphabet: ");
		for (var digit : alphabet.highestValueToLowest())
			System.out.print(digit + " ");
		System.out.println();

		var solver = new HexSolver(hexConfig, alphabet);
		var result = solver.solve();

		System.out.println("Result: " + String.join("", Arrays.stream(result).map(Digit::getTargetSymbol).toList()));
	}

}
