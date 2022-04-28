package hexmax;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Tester {

	private static void testPossible(String fileName, String result) throws IOException {
		var config = HexMax.config = HexConfig.readFrom(Files.newInputStream(Path.of(fileName)),
				HexMax.class.getResourceAsStream("hex_alphabet.txt"));

		var solver = new HexSolver(config).solve();

		var testSymbols = config.alphabet().stringToNum(result);

		var testDigits = new Digit[testSymbols.length];
		for (int i = 0; i < testDigits.length; i++) {
			var digit = testDigits[i] = new Digit(solver.digits[i].getOriginalValue());
			testDigits[i].convert(config.alphabet().convert(digit.getOriginalValue(), testSymbols[i].value()));
		}
		solver.setDigits(testDigits);
//		solver.printSolution(System.out, true);

		int additions = 0, removals = 0;
		for (var d : testDigits) {
			additions += d.getAdditions();
			removals += d.getRemovals();
		}

		System.out.println("Used " + additions + " additions and " + removals + " removals");
	}

	public static void main(String[] args) throws IOException {
		testPossible("samples/hexmax3.txt", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFAA98BB8B9DFAFEAE888DD888AD8BA8EA8889");
	}

}
