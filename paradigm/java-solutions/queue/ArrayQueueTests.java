package queue;

public class ArrayQueueTests {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        int total = 12, mod = 3;
        System.out.println("Queue is empty: " + queue.isEmpty());
        for (int i = 0; i < total; i++) {
            queue.enqueue("Element(value=" + i + ")");
            queue.push("Element(value=" + i * i + ")");
            if (i % mod == 0) {
                queue.dequeue();
                queue.remove();
            }
        }
        System.out.println(queue.indexOf("Element(value=4)"));
        System.out.println(queue.lastIndexOf("Element(value=4)"));
        while (queue.size() > 1) {
            System.out.println(queue.element());
            System.out.println(queue.peek());
            queue.dequeue();
            queue.remove();
        }
        queue.clear();
        System.out.println("Queue was cleared: " + queue.isEmpty());
    }
}
