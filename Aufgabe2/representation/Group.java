package representation;

public class Group {

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