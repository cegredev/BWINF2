package representation;

public class Node<T extends Node<?>> {

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