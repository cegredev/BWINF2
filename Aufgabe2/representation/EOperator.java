package representation;

public enum EOperator implements IOperator {

    ADD {
        @Override
        public int operate(int a, int b) {
            return a + b;
        }

        @Override
        public char getSymbol() {
            return '+';
        }
    },
    SUB {
        @Override
        public int operate(int a, int b) {
            return a - b;
        }

        @Override
        public char getSymbol() {
            return '-';
        }
    },
    MUL {
        @Override
        public int operate(int a, int b) {
            return a * b;
        }

        @Override
        public char getSymbol() {
            return '*';
        }

        @Override
        public boolean hasPriority() {
            return true;
        }
    },
    DIV {
        @Override
        public int operate(int a, int b) {
            return a / b;
        }

        @Override
        public char getSymbol() {
            return '/';
        }

        @Override
        public boolean canOperate(int a, int b) {
            return b != 0 && a % b == 0;
        }

        @Override
        public boolean hasPriority() {
            return true;
        }
    };

    public static final EOperator[] VALUES = values();

    @Override
    public String toString() {
        return String.valueOf(getSymbol());
    }

}