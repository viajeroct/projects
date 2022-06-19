package queue;

import java.util.Objects;

public class LinkedQueue extends AbstractQueue {
    // :NOTE: Доступ
    Node head = null, tail = null;

    @Override
    public void enqueue(Object element) {
        Objects.requireNonNull(element);
        // :NOTE: Упростить
        Node current = new Node(element, null);
        if (tail == null) {
            tail = current;
        } else {
            tail.prev = current;
            tail = tail.prev;
        }
        if (head == null) {
            head = tail;
        }
        size++;
    }

    @Override
    public Object dequeue() {
        Objects.requireNonNull(head);
        Object result = head.value;
        head = head.prev;
        size--;
        return result;
    }

    @Override
    public Object element() {
        Objects.requireNonNull(head);
        return head.value;
    }

    @Override
    public void clear() {
        head = tail = null;
        size = 0;
    }

    private static class Node {
        Object value;
        Node prev;

        public Node(Object value, Node prev) {
            this.value = value;
            this.prev = prev;
        }
    }
}
