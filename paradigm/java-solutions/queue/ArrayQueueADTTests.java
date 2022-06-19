package queue;

public class ArrayQueueADTTests {
    public static void main(String[] args) {
        ArrayQueueADT queue = new ArrayQueueADT();
        int total = 12, mod = 3;
        System.out.println("Queue is empty: " + ArrayQueueADT.isEmpty(queue));
        for (int i = 0; i < total; i++) {
            ArrayQueueADT.enqueue(queue, "Element(value=" + i + ")");
            ArrayQueueADT.push(queue, "Element(value=" + i * i + ")");
            if (i % mod == 0) {
                ArrayQueueADT.dequeue(queue);
                ArrayQueueADT.remove(queue);
            }
        }
        System.out.println(ArrayQueueADT.indexOf(queue, "Element(value=4)"));
        System.out.println(ArrayQueueADT.lastIndexOf(queue, "Element(value=4)"));
        while (ArrayQueueADT.size(queue) > 1) {
            System.out.println(ArrayQueueADT.element(queue));
            System.out.println(ArrayQueueADT.peek(queue));
            ArrayQueueADT.dequeue(queue);
            ArrayQueueADT.remove(queue);
        }
        ArrayQueueADT.clear(queue);
        System.out.println("Queue was cleared: " + ArrayQueueADT.isEmpty(queue));
    }
}
