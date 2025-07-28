package data0728;
class MyThread1 extends Thread {
    public void run() {
        System.out.println("這是執行緒：" + Thread.currentThread().getName());
    }
}

public class ThreadDemo1 {
    public static void main(String[] args) {
        MyThread1 t1 = new MyThread1();
        t1.start();  // 啟動執行緒，會呼叫 run()
    }
}

