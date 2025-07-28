package data0728;

class MyThread2 extends Thread {
    public void run() {
        while(true){
            System.out.println("這是執行緒：" + Thread.currentThread().getName());
            try {
                Thread.sleep(500);  // 模擬延遲
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class ThreadDemo2 {
    public static void main(String[] args) {
        MyThread2 t1 = new MyThread2();
        MyThread2 t2 = new MyThread2();
        MyThread2 t3 = new MyThread2();

        t1.start();  // 啟動執行緒
        t2.start();
        t3.start();
    }
}