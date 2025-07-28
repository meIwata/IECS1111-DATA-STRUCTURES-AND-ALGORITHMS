package data0728;
class MyRunnable implements Runnable{
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

public class RunnableDemo {
    // 使用 Runnable 介面來實現執行緒
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyRunnable());
        t1.start();
    }
}
