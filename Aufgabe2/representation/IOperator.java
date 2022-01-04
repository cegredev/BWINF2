package representation;

public interface IOperator {
    int operate(int a, int b);

    char getSymbol();

    default boolean hasPriority() {
        return false;
    }

    default boolean canOperate(int a, int b) {
        return true;
    }
}
