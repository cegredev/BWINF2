package hexmax;

import hexmax.alphabet.Alphabet;

import java.nio.file.Path;
import java.util.Arrays;

public class HexMax {

	public static void main(String[] args) throws Exception {
		var alphabet = Alphabet.readFrom(HexMax.class.getResourceAsStream("hexalphabet.txt"));
		var hexConfig = HexConfig.fromFile(Path.of("samples/hexmax0.txt"), alphabet);

		for (var digit : alphabet.highestValueToLowest())
			System.out.print(digit + " ");
		System.out.println();

		var solver = new HexSolver(hexConfig, alphabet);
		var result = solver.solve();

		System.out.println(String.join("", Arrays.stream(result).map(WorkingDigit::getTargetSymbol).toList()));
	}

}
