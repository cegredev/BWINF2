package hexmax;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

/**
 * Die Main-Klasse für diese Aufgabe. Von hier wird die Ausführung gestartet.
 */
public class HexMax {

	// FIXME: Package-private for tester
	static HexConfig config;

	public static void main(String[] args) throws IOException {
		try (var scanner = new Scanner(System.in)) {
			System.out.print("Geben Sie ein Alphabet an. Wählen Sie dazu aus \"binary\", \"decimal\" und \"hex\", " +
					"geben Sie einen eigenen Pfad ein oder lassen Sie diese Zeile leer (hex): ");

			// Alphabet einlesen
			var alphabetPath = scanner.nextLine();
			if (alphabetPath.isBlank())
				alphabetPath = "hex";

			var alphabetIn = HexMax.class.getResourceAsStream(alphabetPath + "_alphabet.txt");
			if (alphabetIn == null)
				alphabetIn = Files.newInputStream(Path.of(alphabetPath), StandardOpenOption.READ);

			// Aufgabe einlesen
			System.out.print("Geben Sie nun bitte den Pfad zu einer Aufgaben-Datei an: ");

			var exerciseIn = Files.newInputStream(Path.of(scanner.nextLine()));

			// Konfiguration erstellen
			config = HexConfig.readFrom(exerciseIn, alphabetIn);

			// Ausgabe-Destination einlesen
			System.out.print("Wohin möchten Sie das Ergebnis ausgeben lassen? Geben sie einen Pfad für eine Datei an," +
					" " +
					"oder lassen Sie die Eingabe leer für die Konsole: ");

			var outPath = scanner.nextLine();
			var outStream = System.out;
			if (!outPath.isBlank()) {
				outStream = new PrintStream(new FileOutputStream(outPath));
			}

			// Zwischenschritt-Inklusion einlesen
			System.out.print("Sollen die Zwischenschritte ausgegeben werden? (y für ja, alles andere für nein): ");

			boolean printSteps = scanner.nextLine().equals("y");

			// Aufgabe lösen und Lösung ausgeben
			var solver = new HexSolver(config).solve();
			solver.printSolution(outStream, printSteps);

			outStream.close();
		}

		// Dieses Programm bemüht sich absichtlich nicht Exceptions zu fangen oder Input zu validieren
		// und den Nutzer nach neuen Eingaben zu fragen, um den UI-Teil möglichst gering zu halten.
	}

	public static HexConfig getConfig() {
		return config;
	}

}
