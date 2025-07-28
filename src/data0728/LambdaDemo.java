package data0728;

public class LambdaDemo {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            System.out.println("執行緒: " + Thread.currentThread().getName() + " 正在執行");
        });
        t.start();
    }
}
