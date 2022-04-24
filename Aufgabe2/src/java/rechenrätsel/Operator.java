package rechenrätsel;

public enum Operator {

	ADD {
		@Override
		public int calculate(int a, int b) {
			return a + b;
		}

		@Override
		public Operator getOpposite() {
			return SUBTRACT;
		}

		@Override
		public char getSymbol() {
			return '+';
		}

		@Override
		public boolean mayOperate(int a, int b) {
			// 2 + 2 == 2 * 2, not having it
			return a != 2 && b != 2;
		}
	},
	SUBTRACT {
		@Override
		public int calculate(int a, int b) {
			return a - b;
		}

		@Override
		public Operator getOpposite() {
			return ADD;
		}

		@Override
		public char getSymbol() {
			return '-';
		}
	},
	MULTIPLY {
		@Override
		public int calculate(int a, int b) {
			return a * b;
		}

		@Override
		public Operator getOpposite() {
			return DIVIDE;
		}

		@Override
		public char getSymbol() {
			return '*';
		}

		@Override
		public boolean hasPriority() {
			return true;
		}

		@Override
		public boolean mayOperate(int a, int b) {
			// Can't multiply with 1 because that would create a scenario in which adding could create a bigger number than multiplying
			return true;
		}
	},
	DIVIDE {
		@Override
		public int calculate(int a, int b) {
			return a / b;
		}

		@Override
		public Operator getOpposite() {
			return MULTIPLY;
		}

		@Override
		public char getSymbol() {
			return '/';
		}

		@Override
		public boolean canOperate(int a, int b) {
			return mayOperate(a, b);
		}

		@Override
		public boolean mayOperate(int a, int b) {
			return b != 0 && a % b == 0;
		}

		@Override
		public boolean hasPriority() {
			return true;
		}
	};

	public abstract int calculate(int a, int b);

	public abstract Operator getOpposite();

	public abstract char getSymbol();

	public boolean hasPriority() {
		return false;
	}

	// For tester
	public boolean canOperate(int a, int b) {
		return true;
	}

	public boolean mayOperate(int a, int b) {
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(getSymbol());
	}

}
