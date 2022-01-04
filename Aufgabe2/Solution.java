import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Solution {

    private static abstract class Node<T extends Node<?>> {

        private T next;

        protected Node(T next) {
            this.next = next;
        }

        protected Node() {
            this(null);
        }

        public T getNext() {
            return next;
        }

        public void setNext(T next) {
            this.next = next;
        }

    }

    private static class Operand extends Node<Operator> {

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

    private static interface IOperator {
        int operate(int a, int b);

        char getSymbol();

        default boolean hasPriority() {
            return false;
        }

        default boolean canOperate(int a, int b) {
            return true;
        }
    }

    private static enum EOperator implements IOperator {

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

    private static class Operator extends Node<Operand> {

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

    private static class Group {

        private final int result;
        private final Operand start;

        public Group(Operand start, int result) {
            this.start = start;
            this.result = result;
        }

        @Override
        public String toString() {
            var builder = new StringBuilder();

            Node<?> node = start;
            do {
                builder.append(node.toString());
            } while ((node = node.getNext()) != null);

            builder.append("=").append(getResult());

            return builder.toString();
        }

        public int getResult() {
            return result;
        }

    }

    private static interface IRestriction {

        public default Collection<Integer> restrict(EOperator purpose, Collection<Integer> nums) {
            switch (purpose) {
                case MUL:
                    return restrictMul(nums);
                case DIV:
                    return restrictDiv(nums);
                default:
                    return nums;
            }
        }

        default Collection<Integer> restrictMul(Collection<Integer> nums) {
            return nums;
        }

        default Collection<Integer> restrictDiv(Collection<Integer> nums) {
            return nums;
        }

    }

    private static enum Restriction implements IRestriction {

        SINGLE_PAIR {

            @Override
            public Collection<Integer> restrictMul(Collection<Integer> nums) {
                nums.remove(2);
                return nums;
            }

        };

    }

    private static class DivRestriction implements IRestriction {

        private final Collection<Integer> allowed = new ArrayList<>();

        public DivRestriction(int operand) {
            for (int num = 2; num <= 9; num++)
                if (EOperator.DIV.canOperate(operand, num))
                    allowed.add(num);
        }

        @Override
        public Collection<Integer> restrictDiv(Collection<Integer> nums) {
            nums.retainAll(allowed);
            System.out.println("Restricting: " + nums);
            return nums;
        }

    }

    private static class Selection {

        private final List<Integer> nums = new ArrayList<>(List.of(2, 3, 4, 5, 6, 7, 8, 9));
        private final List<IRestriction> restrictions = new ArrayList<>();

        public void prepare() {
            Collections.shuffle(nums);
            restrictions.clear();
        }

        public Optional<Integer> get(EOperator purpose) {
            var clone = new ArrayList<>(nums);

            for (var restriction : restrictions)
                restriction.restrict(purpose, clone);

            return clone.isEmpty() ? Optional.empty() : Optional.of(clone.get(0));
        }

    }

    private double divChance = 0.2;
    private Random random = new Random();

    private Group generateGroup(int initial, int length) {
        if (length <= 1)
            throw new IllegalArgumentException("Groups have to consist of two or more operands. Got: " + length);

        var operandNode = new Operand(initial);
        final var start = operandNode;

        int result = operandNode.operand;
        var selection = new Selection();
        for (int i = 1; i < length; i++) {
            selection.prepare();

            int operand = -1;
            double roll = random.nextDouble();
            Operator operatorNode = null;

            if (roll <= divChance) {
                selection.restrictions.add(new DivRestriction(result));

                var maybeOperand = selection.get(EOperator.DIV);
                if (maybeOperand.isPresent()) {
                    operand = maybeOperand.get();
                    operatorNode = new Operator(EOperator.DIV);
                    result /= operand;
                }
            }

            if (operatorNode == null) {
                var maybeOperand = selection.get(EOperator.MUL);
                if (maybeOperand.isEmpty())
                    throw new IllegalArgumentException("Unable to generate operand");

                operand = maybeOperand.get();
                operatorNode = new Operator(EOperator.MUL);
                result *= operand;
            }

            operandNode.setNext(operatorNode);
            operandNode = new Operand(operand);
            operatorNode.setNext(operandNode);
        }

        return new Group(start, result);
    }

    public static void main(String[] args) {
        var solution = new Solution();

        for (int i = 1; i <= 9; i++)
            System.out.println(solution.generateGroup(i, 10));
    }

}
