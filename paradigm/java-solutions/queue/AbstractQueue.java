package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void retainIf(final Predicate<Object> predicate) {
        for (int i = size; i > 0; i--) {
            final Object el = dequeue();
            if (predicate.test(el)) {
                enqueue(el);
            }
        }
    }

    @Override
    public void removeIf(final Predicate<Object> predicate) {
        retainIf(predicate.negate());
    }

    @Override
    public void dropWhile(final Predicate<Object> predicate) {
        whileImpl(predicate, false);
    }

    @Override
    public void takeWhile(final Predicate<Object> predicate) {
        whileImpl(predicate, true);
    }

    private void whileImpl(final Predicate<Object> predicate, final boolean flag) {
        int len = size;
        while (len > 0 && predicate.test(element())) {
            final Object cur = dequeue();
            if (flag) {
                enqueue(cur);
            }
            len--;
        }
        while (len-- > 0 && flag) {
            dequeue();
        }
    }
}
