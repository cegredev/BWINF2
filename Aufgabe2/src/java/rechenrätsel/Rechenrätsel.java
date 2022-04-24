package rechenrätsel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

/**
 * Die Main-Klasse für diese Aufgabe. Von hier wird die Ausführung gestartet.
 */
public class Rechenrätsel {

	public static void main(String[] args) {
		test();
//		userInput();
	}

	private static void userInput() {
		try (var scanner = new Scanner(System.in)) {
			// Ließt die Anzahl an Operatoren ein
			int numOperators = -1;
			do {
				System.out.print("Bitte geben Sie die Anzahl an Operatoren ein, für die ein Rätsel generiert werden " +
						"soll: ");
				var in = scanner.nextLine();

				try {
					numOperators = Integer.parseInt(in);
				} catch (NumberFormatException e) {
					System.err.println("Bitte geben Sie eine ganze, positive Zahl ein.");
				}
			} while (numOperators <= 0);

			// Generiert eine entsprechende Gleichung
			var generator = new Generator(numOperators).generate();
			var equation = generator.getEquationAsString();

			System.out.println("Es wurde folgende Gleichung generiert: " + equation);

			var outPath = Path.of(numOperators + "Operatoren_" + System.currentTimeMillis() + ".txt").toAbsolutePath();

			System.out.println("Sie wird in der Datei \"" + outPath + "\" gespeichert.");

			// Speichert die Gleichung in einer Datei
			Files.writeString(outPath, equation, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			System.err.println("Beim Speichern der Datei ist ein Fehler aufgetreten.");
			e.printStackTrace();
		}
	}

	private static void test() {
		int failCount = 0;

		for (int i = 0; i < 1; i++) {
			long startTime = System.currentTimeMillis();
			var gen = new Generator(3).generate();
			var equation = gen.getEquationAsString();

			System.out.println("Took " + (System.currentTimeMillis() - startTime) + "ms to generate equation: " + equation);

			var tester = new Tester(Tester.parse(equation), Operator.VALUES);

			if (tester.getSolutions().size() != 1) {
				failCount++;
				tester.printAnalysis();
			}
		}

		System.out.println("Failed " + failCount + " times.");
	}

}
