package representation;

public class Operator extends Node<Operand> {

    private EOperator operator;

    public Operator(EOperator operator, Operand next) {
        super(next);

        this.operator = operator;
    }

    public Operator(EOperator operator) {
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