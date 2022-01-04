package representation;

public class Operand extends Node<Operator> {

    private final int operand;

    public Operand(int operand, Operator next) {
        super(next);

        this.operand = operand;
    }

    public Operand(int operand) {
        this.operand = operand;
    }

    public int getOperand() {
        return operand;
    }

    @Override
    public String toString() {
        return String.valueOf(getOperand());
    }

}