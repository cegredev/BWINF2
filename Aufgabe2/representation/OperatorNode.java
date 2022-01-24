package representation;

public class OperatorNode extends Node<OperandNode> {

    private EOperator operator;

    public OperatorNode(EOperator operator, OperandNode next) {
        super(next);

        this.operator = operator;
    }

    public OperatorNode(EOperator operator) {
        this.operator = operator;
    }

    public EOperator getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return getOperator().toString();
    }

}