package data0728;

import java.util.LinkedList;
import java.util.Queue;

public class QueueDemo {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<String>();
        queue.offer("Karen"); // offer 是放入
        queue.offer("Brain");
        queue.offer("Emma");
        queue.offer("Paul");
        System.out.println(queue.isEmpty());
        System.out.println(queue.size());
        System.out.println(queue.contains("Paul"));

        System.out.println(queue.peek()); // peek 是查看第一個元素，但不取出

        queue.poll(); // poll 是取出
        queue.poll();
        queue.poll();
        System.out.println(queue);

    }

}
