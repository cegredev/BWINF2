package rechenrätsel;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generiert eine den Regeln entsprechende Gleichung.
 */
public class Generator {

	/**
	 * Eine Sammlung an möglichen Ergebnissen.
	 */
	private final Set<Integer> validResults = new HashSet<>();
	/**
	 * Eine Sammlung aller generierter Ergebnisse.
	 */
	private final Set<Integer> generatedResults = new HashSet<>();

	private final int[] operands;
	private final Operator[] operators;

	private int result;

	public Generator(int numOperators) {
		this.operands = new int[numOperators + 1];
		this.operators = new Operator[numOperators];
	}

	/**
	 * Gibt das Ergebnis der momentanen Gleichung oder -1, falls die Gleichung nicht den Regeln entsprechen sollte, zurück.
	 */
	private int evaluate() {
		int result = operands[0];

		for (int i = 0; i < operators.length; i++) {
			var operator = operators[i];
			int operand = operands[i + 1];

			// Wenn der momentane Operator keine Priorität hat, muss erst überprüft werden,
			// ob die Operatoren rechts davon dies haben
			if (!operator.hasPriority()) {
				int j;
				for (j = i + 1; j < operators.length; j++) {
					var nextOperator = operators[j];
					int nextOperand = operands[j + 1];
					// Sobald einer der folgenden Operatoren ebenfalls keine Priorität haben sollte,
					// wird die Schleife abgebrochen
					if (!nextOperator.hasPriority())
						break;

					// Wenn der Operator angewandt werden kann, wird er angewendet,
					// ansonsten wird die Gleichung als ungültig markiert
					if (nextOperator.canOperate(operand, nextOperand))
						operand = nextOperator.calculate(operand, nextOperand);
					else
						return -1;
				}

				// Springt zu dem Operator, der die obige Schleife abbrechen ließ
				i = j - 1;
			}

			// Wenn der Operator angewandt werden kann, wird er angewendet,
			// ansonsten wird die Gleichung als ungültig markiert.
			// Diese Wiederholung des Codes ist nicht schön, aber leider unvermeidlich.
			if (operator.canOperate(result, operand))
				result = operator.calculate(result, operand);
			else
				return -1;
		}

		return result;
	}

	/**
	 * Diese Funktion ruft sich rekursiv selbst auf, um alle möglichen Kombinationen zu überprüfen.
	 */
	private void tryCombination(int operatorIndex) {
		// Befindet sich dieser Ast der Funktion am Ende der Gleichung, so wird ihr Wert berechnet
		if (operatorIndex == operators.length) {
			Integer result = evaluate();

			// Wenn das Ergebnis ≤ 0 ist, entspricht es nicht den Regeln und muss somit übersprungen werden
			if (result <= 0) return;

			// Falls das Ergebnis bis jetzt noch nicht generiert wurde, füge es zur Sammlung möglicher Ergebnisse hinzu
			if (generatedResults.add(result)) {
				validResults.add(result);
			} else {
				// Wurde das Ergebnis jedoch bereits generiert, wird sichergestellt,
				// dass es sich nicht in der Sammlung befindet
				validResults.remove(result);
			}
		} else {
			//
			for (int i = 0; i < Operator.VALUES.length; i++) {
				operators[operatorIndex] = Operator.VALUES[i];
				tryCombination(operatorIndex + 1);
			}
		}
	}

	/**
	 * Generiert eine Gleichung, die allen Regeln entspricht, und speichert sie in dieser Instanz.
	 */
	public Generator generate() {
		final var random = ThreadLocalRandom.current();

		do {
			// Generiert die Operanden
			for (int i = 0; i < operands.length; i++)
				operands[i] = random.nextInt(2, 10);

			// Generiert eine Gleichung mit nur einer Lösung
			tryCombination(0);

			// Gibt es eine Lösung, wird eine zufällige ausgewählt
			if (validResults.size() > 0) {
				// This will give us a result that was generally-speaking generated early on,
				// meaning most of the time, it will be small, which is more desirable
				this.result = validResults.iterator().next();
			}

			// Die Schleife wiederholt sich so lange, bis es eine Lösung gibt
		} while (validResults.size() == 0);

		return this;
	}

	/**
	 * Formatiert die Gleichung als String.
	 */
	public String getEquationAsString() {
		return String.join("◦", Arrays.stream(operands).boxed().map(String::valueOf).toList()) + "=" + result;
	}

}
