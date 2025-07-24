package data0724;

import java.util.Stack;

public class StackDemo {
    public static void main(String[] args) {
        Stack<String> path = new Stack<>();
        path.push("公園");
        path.push("早餐店");
        path.push("便利商店");

        while(!path.isEmpty()) {
            System.out.println("返航: " + path.pop());
        }
        System.out.println("返航結束");
    }
}
