package queue;

import java.util.Objects;

/*
Model:
Let immutable(b):
    b'.size == b.size && for all i in 0..b.length-1 b'[i] = b[i]
Invariant:
    n >= 0; b[0], ..., b[n - 1] && (for all i in 0..n - 1: b[i] != null)
 */
public class ArrayQueueADT {
    private int head = 0, size = 0;
    private Object[] elements = new Object[2];

    private static int movePosition(final ArrayQueueADT queue, final int x) {
        final int res = (queue.head + x) % queue.elements.length;
        return res >= 0 ? res : (res + queue.elements.length) % queue.elements.length;
    }

    private static void ensureCapacity(final ArrayQueueADT queue, final int capacity) {
        Objects.requireNonNull(queue);
        if (capacity > queue.elements.length) {
            final Object[] tmp = new Object[queue.elements.length * 2];
            System.arraycopy(queue.elements, queue.head, tmp, 0, queue.elements.length - queue.head);
            System.arraycopy(queue.elements, 0, tmp, queue.elements.length - queue.head, queue.head);
            queue.elements = tmp;
            queue.head = 0;
        }
    }

    /*
    Pred: element != null && queue != null
    Post: n' = n + 1 && for all i in 1..n b'[i] = b[i - 1] && b'[0] = element
     */
    public static void push(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(queue);
        ensureCapacity(queue, queue.size + 1);
        final int cur = movePosition(queue, -1);
        queue.elements[cur] = element;
        queue.head = cur;
        queue.size++;
    }

    /*
    Pred: n >= 1 && queue != null
    Post: R = b[n - 1] && immutable(b)
     */
    public static Object peek(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size >= 1;
        return queue.elements[movePosition(queue, queue.size - 1)];
    }

    /*
    Pred: n >= 1 && queue != null
    Post: n' = n - 1 && for all i in 0..n - 2 b'[i] = b[i] && R = b[n - 1]
     */
    public static Object remove(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size >= 1;
        final int cur = movePosition(queue, queue.size - 1);
        final Object result = queue.elements[cur];
        queue.elements[cur] = null;
        queue.size--;
        return result;
    }

    /*
    Pred: element != null
    Post: immutable(b) &&
          (
          ( R == -1 && for all i in 0..n-1 b[i] != element                           ) ||
          ( R in [0..n-1] && b[R] = element && for all j in [0..R-1] b[j] != element )
          )
     */
    public static int indexOf(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(queue);
        for (int i = 0; i < queue.size; i++) {
            if (queue.elements[movePosition(queue, i)].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    /*
    Pred: element != null
    Post: immutable(b) &&
          (
          ( R == -1 && for all i in 0..n-1 b[i] != element                               ) ||
          ( R in [0..n-1] && b[R] = element && for all j in [R + 1..n-1] b[j] != element )
          )
     */
    public static int lastIndexOf(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(queue);
        for (int i = queue.size - 1; i >= 0; i--) {
            if (queue.elements[movePosition(queue, i)].equals(element)) {
                return i;
            }
        }
        return -1;
    }

    /*
    Pred: element != null && queue != null
    Post: n' = n + 1 && for all i in 0..n - 1 b'[i] = b[i] && b'[n] = element
     */
    public static void enqueue(final ArrayQueueADT queue, final Object element) {
        Objects.requireNonNull(queue);
        ensureCapacity(queue, queue.size + 1);
        queue.elements[movePosition(queue, queue.size)] = element;
        queue.size++;
    }

    /*
    Pred: n >= 1 && queue != null
    Post: R = b[0] && immutable(b)
     */
    public static Object element(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size >= 1;
        return queue.elements[queue.head];
    }

    /*
    Pred: n >= 1 && queue != null
    Post: n' = n - 1 && for all i in 0 ... n - 2 b[i]' = b[i + 1] && R = b[0]
     */
    public static Object dequeue(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        assert queue.size >= 1;
        final Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = movePosition(queue, 1);
        queue.size--;
        return result;
    }

    /*
    Pred: queue != null
    Post: R = n && immutable(b)
     */
    public static int size(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size;
    }

    /*
    Pred: queue != null
    Post: R = n == 0 && immutable(b)
     */
    public static boolean isEmpty(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        return queue.size == 0;
    }

    /*
    Pred: queue != null
    Post: n = 0
     */
    public static void clear(final ArrayQueueADT queue) {
        Objects.requireNonNull(queue);
        queue.elements = new Object[2];
        queue.head = queue.size = 0;
    }
}
