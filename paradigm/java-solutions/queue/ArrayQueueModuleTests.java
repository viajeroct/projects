package queue;

public class ArrayQueueModuleTests {
    public static void main(String[] args) {
        int total = 12, mod = 3;
        System.out.println("Queue is empty: " + ArrayQueueModule.isEmpty());
        for (int i = 0; i < total; i++) {
            ArrayQueueModule.enqueue("Element(value=" + i + ")");
            ArrayQueueModule.push("Element(value=" + i * i + ")");
            if (i % mod == 0) {
                ArrayQueueModule.dequeue();
                ArrayQueueModule.remove();
            }
        }
        System.out.println(ArrayQueueModule.indexOf("Element(value=4)"));
        System.out.println(ArrayQueueModule.lastIndexOf("Element(value=4)"));
        while (ArrayQueueModule.size() > 1) {
            System.out.println(ArrayQueueModule.element());
            System.out.println(ArrayQueueModule.peek());
            ArrayQueueModule.dequeue();
            ArrayQueueModule.remove();
        }
        ArrayQueueModule.clear();
        System.out.println("Queue was cleared: " + ArrayQueueModule.isEmpty());
    }
}
