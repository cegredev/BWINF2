package representation;

public class OperandNode extends Node<OperatorNode> {

    private final int operand;

    public OperandNode(int operand, OperatorNode next) {
        super(next);

        this.operand = operand;
    }

    public OperandNode(int operand) {
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