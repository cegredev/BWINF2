package rechenrätsel;

/**
 * Repräsentiert einen Operator wie Addition oder Multiplikation.
 */
public enum Operator {

	ADD {
		@Override
		public int calculate(int a, int b) {
			return a + b;
		}
	},
	SUBTRACT {
		@Override
		public int calculate(int a, int b) {
			return a - b;
		}
	},
	MULTIPLY {
		@Override
		public int calculate(int a, int b) {
			return a * b;
		}

		@Override
		public boolean hasPriority() {
			return false;
		}
	},
	DIVIDE {
		@Override
		public int calculate(int a, int b) {
			return a / b;
		}

		@Override
		public boolean canOperate(int a, int b) {
			return b != 0 && a % b == 0;
		}

		@Override
		public boolean hasPriority() {
			return false;
		}
	};

	public static final Operator[] VALUES = values();

	/**
	 * Wendet den Operator auf die Variablen a und b an.
	 */
	public abstract int calculate(int a, int b);

	/**
	 * Gibt zurück, ob ein Operator nicht priorisiert wird.
	 */
	public boolean hasPriority() {
		return true;
	}

	/**
	 * Gibt zurück, ob ein Operator auf die gegebenen Variablen angewandt werden kann. Ein Negativbeispiel ist 3 / 2.
	 */
	public boolean canOperate(int a, int b) {
		return true;
	}

}
