import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import representation.EOperator;
import representation.Group;
import representation.Operand;
import representation.Operator;

public class Generator {

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

    private static class DivByRestriction implements IRestriction {

        private final Collection<Integer> allowed = new ArrayList<>();

        public DivByRestriction(int operand) {
            for (int num = 2; num <= 9; num++)
                if (EOperator.DIV.canOperate(operand, num))
                    allowed.add(num);
        }

        @Override
        public Collection<Integer> restrictDiv(Collection<Integer> nums) {
            nums.retainAll(allowed);
            // System.out.println("Restricting: " + nums);
            return nums;
        }

    }

    private static class CancelOutRestriction implements IRestriction {

        private final Collection<Integer> allowed = new ArrayList<>();

        public CancelOutRestriction(Set<Integer> others, Set<Integer> sameKind) {
            Outer: for (int num = 2; num <= 9; num++) {
                if (others.contains(num))
                    continue;

                for (int similar : sameKind)
                    if (others.contains(similar * num))
                        continue Outer;

                allowed.add(num);
            }
        }

        @Override
        public Collection<Integer> restrict(EOperator purpose, Collection<Integer> nums) {
            nums.retainAll(allowed);
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

        public void restrict(IRestriction restriction) {
            restrictions.add(restriction);
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

    public Group generateGroup(int initial, int length) {
        if (length <= 1)
            throw new IllegalArgumentException("Groups have to consist of two or more operands. Got: " + length);

        var operandNode = new Operand(initial);
        final var start = operandNode;

        Set<Integer> mulNums = new HashSet<>(), divNums = new HashSet<>();

        int result = operandNode.getOperand();
        var selection = new Selection();
        for (int i = 1; i < length; i++) {
            selection.prepare();

            int operand = -1;
            double roll = random.nextDouble();
            Operator operatorNode = null;

            if (roll <= divChance) {
                selection.restrict(new DivByRestriction(result));
                selection.restrict(new CancelOutRestriction(mulNums, divNums));

                var maybeOperand = selection.get(EOperator.DIV);
                if (maybeOperand.isPresent()) {
                    operand = maybeOperand.get();
                    operatorNode = new Operator(EOperator.DIV);
                    result /= operand;

                    fillSet(divNums, operand);
                }
            }

            if (operatorNode == null) {
                selection.restrict(new CancelOutRestriction(divNums, mulNums));

                var maybeOperand = selection.get(EOperator.MUL);
                if (maybeOperand.isEmpty())
                    throw new IllegalArgumentException("Unable to generate operand");

                operand = maybeOperand.get();
                operatorNode = new Operator(EOperator.MUL);
                result *= operand;

                fillSet(mulNums, operand);
            }

            operandNode.setNext(operatorNode);
            operandNode = new Operand(operand);
            operatorNode.setNext(operandNode);
        }

        // System.out.println(mulNums + " " + divNums);

        return new Group(start, result);
    }

    private static void fillSet(Set<Integer> set, int num) {
        var newNums = new ArrayList<Integer>(set.size() * 2);

        for (int a : set)
            newNums.add(a * num);

        set.add(num);
        set.addAll(newNums);
    }

    public static void main(String[] args) {
        var solution = new Generator();

        for (int i = 1; i <= 9; i++)
            System.out.println(solution.generateGroup(i, 8));
    }

}
