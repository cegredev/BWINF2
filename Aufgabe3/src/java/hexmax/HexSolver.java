package hexmax;

import hexmax.alphabet.Alphabet;

import java.io.PrintStream;
import java.util.*;

/**
 * Löst ein gegebenes Beispiel.
 */
public class HexSolver {

	private final int maxMoves;
	private final Digit[] digits;
	private final Alphabet alphabet;

	private int additions, removals;

	public HexSolver(HexConfig config) {
		this.maxMoves = config.maxMoves();
		this.digits = Arrays.stream(config.digits()).map(Digit::new).toList().toArray(new Digit[0]);
		this.alphabet = config.alphabet();
	}

	/**
	 * Überprüft, ob eine Umwandlung ausgeführt werden kann, und tut dies falls möglich.
	 */
	private boolean tryConversion(Digit digit, SymbolConversion conversion) {
		// Die Differenzen zwischen den Umlegungen, die die Ziffer momentan benötigt und wie viele sie hinter
		// benötigen würde
		int additionDiff = conversion.additions() - digit.getAdditions();
		int removalDiff = conversion.removals() - digit.getRemovals();

		// Falls die Umwandlung möglich ist
		if (additions + additionDiff <= maxMoves && removals + removalDiff <= maxMoves) {
			// Aktualisiert die Zähler für Ergänzungen und Entnahmen (auch negativ)
			additions += additionDiff;
			removals += removalDiff;
			digit.convert(conversion);

			// Umwandlung erfolgreich
			return true;
		}

		return false;
	}

	/**
	 * Erhöht alle Ziffern der Zahl, solange bis alle Umlegungen aufgebraucht oder das Ende der Zahl erreicht sind/ist.
	 *
	 * @return Ob sich die Zahl in ihrem bestmöglichen Zustand befindet.
	 */
	private boolean maximize(int start) {
		// Maximiert jede Zahl vom Start-Index bis zum Ende der Zahl
		for (int i = start; i < digits.length; i++) {
			var digit = digits[i];
			int original = digit.getOriginalValue();

			// Vom größtmöglichen bis zum originalen Wert
			for (int target = digit.getMaximum(); target > original; target--) {
				// Wandle vom originalen zum neuen Wert um
				var conversion = alphabet.convert(original, target);

				// Falls die Umwandlung erfolgreich war, springe zur nächsten Ziffer (oder beende den Vorgang)
				if (tryConversion(digit, conversion))
					break;
			}

			// Wenn alle Umlegungen verwendet wurden, befindet sich die Zahl in ihrer größtmöglichen Form.
			if (additions == maxMoves && removals == maxMoves)
				return true;
		}

		// Wenn das Ende der Zahl erreicht wurde und nicht alle Umlegungen ausgeschöpft wurden, aber die Anzahl an
		// Ergänzungen und Entnahmen gleich ist, befindet sich die Zahl trotzdem in ihrer größtmöglichen Form.
		return additions == removals;
	}

	/**
	 * Versucht die Ergänzungen und Entnahmen dieser Zahl zu balancieren, indem Ziffern von rechts nach links in die
	 * Form versetzt werden, in der sie am meisten der fehlenden Resource (Ergänzung oder Entnahme) verwenden. Dies
	 * passiert so lange, bis ein Gleichgewicht erreicht oder das Ungleichgewicht umgekehrt wurde.
	 */
	private int balance(int stop) {
		// Ob zu Beginn des Balance-Prozesses die Ergänzungen oder Entnahmen überwiegten
		boolean additionsWereGreater = additions > removals;

		// Von rechts nach links
		for (int i = digits.length - 1; i >= stop; i--) {
			var digit = digits[i];
			int original = digit.getOriginalValue();

			// Wenn die Ergänzungen überwiegen, müssen mehr Entnahmen geschaffen werden.
			// Wenn die Entnahmen überwiegen, müssen mehr Ergänzungen geschaffen werden.
			var conversions = additionsWereGreater ?
					alphabet.getAllConversionsByRemovals(original)
					: alphabet.getAllConversionsByAdditions(original);

			// Die Umwandlungen sind aufsteigend sortiert, daher wird am Ende angefangen
			for (int j = conversions.size() - 1; j >= 0; j--) {
				var conversion = conversions.get(j);

				// Falls die Umwandlung erfolgreich war, springe zur nächsten Ziffer (oder beende den Vorgang)
				if (tryConversion(digit, conversion))
					break;
			}

			// Wenn ein Gleichgewicht geschaffen oder das Ungleichgewicht gekippt wurde, ist diese Funktion fertig
			if ((additionsWereGreater && removals >= additions) || (!additionsWereGreater && additions >= removals))
				return i;
		}

		// Wenn die Funktion wieder am Anfang angekommen ist, konnte sie ihr Ziel nicht erreichen. In dem Fall wird
		// der maximale Wert für die entsprechende Stelle (stop) gesenkt. Siehe "solve".
		return stop;
	}

	/**
	 * Löst das gegebene Beispiel.
	 */
	public HexSolver solve() {
		// Der Prozess startet am Anfang der Zahl
		int start = 0;

		// Erhöht die Werte der einzelnen Ziffern so lange, bis eine funktionierende Lösung gefunden wurde
		while (!maximize(start)) {
			// Da während der maximize Funktion nicht auf eine gleiche Verteilung der Ergänzungen und Entnahmen
			// geachtet wird, versucht diese Funktion diese Balance wieder herzustellen.
			// Bis zu dem Index, den balance zurückgibt (exklusiv) befinden sich die Ziffern bereits in ihrer besten
			// Form.
			int newStart = balance(start);

			// Wenn man nach der Balancierung wieder bei der Zahl ankommt, bei der man angefangen hat, kann diese in
			// ihrer höchsten Form so nicht verwendet werden, und muss ihr Maximum gesenkt bekommen
			var digit = digits[start];
			if (newStart == start)
				digit.setMaximum(digit.getMaximum() - 1);

			start = newStart;
		}

		// War die maximize Funktion erfolgreich, befindet sich die Zahl in ihrem höchsten Zustand
		return this;
	}

	/**
	 * Gibt die Lösung aus.
	 */
	public void printSolution(PrintStream out, boolean printSteps) {
		// Wenn die einzelnen Umlegungen ausgegeben werden sollen
		if (printSteps) {
			// missingBits enthält die Indexe für Stellen, die noch belegt werden müssen, spareBits das Gegenteil
			Queue<Integer> missingBits = new ArrayDeque<>(), spareBits = new ArrayDeque<>();

			// Repräsentiert die einzelnen Ziffern in Form ihrer Stellen
			boolean[][] number = new boolean[digits.length][];
			for (int i = 0; i < digits.length; i++)
				number[i] = alphabet.getSymbolByValue(digits[i].getOriginalValue()).bits().clone();

			// Die Indexe der Ziffern von denen gerade Stellen entfernt bzw. ihnen zugefügt werden
			int additionIndex = -1, removalIndex = -1;

			// additions und removals haben denselben Wert und geben beide die Zahl der Umlegungen an
			for (int i = 0; i < additions; i++) {
				printNumber(number, out);

				// Sucht nacht der nächsten Ziffer in der Stellen fehlen
				while (missingBits.isEmpty()) {
					var digit = digits[++additionIndex];
					var conversion = alphabet.convert(digit.getOriginalValue(), digit.getValue());

					// Merkt sich die fehlenden Stellen für die neue zu füllende Ziffer
					missingBits.addAll(conversion.missingBits());
				}

				// Sucht nacht der nächsten Ziffer in der Stellen überschüssig sind
				while (spareBits.isEmpty()) {
					var digit = digits[++removalIndex];
					var conversion = alphabet.convert(digit.getOriginalValue(), digit.getValue());

					// Merkt sich die überschüssigen Stellen für die neue zu leerende Ziffer
					spareBits.addAll(conversion.spareBits());
				}

				// Nimmt und entfernt die erste gespeicherte Stelle
				int additionBit = missingBits.remove(), removalBit = spareBits.remove();

				// Setzt und entfernt jeweils eine Stelle
				number[additionIndex][additionBit] = true;
				number[removalIndex][removalBit] = false;
			}

			printNumber(number, out);
			out.println();
		}

		out.println(formatSolution());
	}

	/**
	 * Gibt die Nummer in Form eines Seven Segment Displays aus.
	 */
	private void printNumber(boolean[][] number, PrintStream out) {
		// Wirklich kein schöner Code, ich weiß. Aber wahrscheinlich noch der leserlichste für dieses Problem.
		for (var digit : number)
			out.print(digit[0] ? " _ " : "   ");
		out.println();

		for (var digit : number) {
			out.print(digit[5] ? "|" : " ");
			out.print(digit[6] ? "_" : " ");
			out.print(digit[1] ? "|" : " ");
		}
		out.println();

		for (var digit : number) {
			out.print(digit[4] ? "|" : " ");
			out.print(digit[3] ? "_" : " ");
			out.print(digit[2] ? "|" : " ");
		}
		out.println();
	}

	/**
	 * Gibt die Lösung mit ihren tatsächlichen Zeichen zurück.
	 */
	private String formatSolution() {
		return String.join("", Arrays.stream(digits).map(Digit::toString).toList());
	}
}
