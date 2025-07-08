package data0701;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GcdLcmCalculator {
    // 計算最大公因數
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // 計算最小公倍數
    public static int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }

    // 計算全部公因數
    public static List<Integer> allCommonDivisors(int a, int b) {
        List<Integer> divisors = new ArrayList<>();
        int min = Math.min(a, b);
        for (int i = 1; i <= min; i++) {
            if (a % i == 0 && b % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("最大公因數與最小公倍數計算器");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel num1Label = new JLabel("請輸入第一個數字：");
        JTextField num1Field = new JTextField(10);
        JLabel num2Label = new JLabel("請輸入第二個數字：");
        JTextField num2Field = new JTextField(10);

        JButton calculateButton = new JButton("計算");
        JLabel resultLabel = new JLabel("結果：");

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int num1 = Integer.parseInt(num1Field.getText());
                    int num2 = Integer.parseInt(num2Field.getText());

                    if (num1 <= 0 || num2 <= 0) {
                        resultLabel.setText("請輸入正整數。");
                    } else {
                        int gcdResult = gcd(num1, num2);
                        int lcmResult = lcm(num1, num2);
                        List<Integer> divisors = allCommonDivisors(num1, num2);
                        String divisorsStr = divisors.toString().replaceAll("[\\[\\] ]", "");
                        resultLabel.setText("最大公因數: " + gcdResult +
                                ", 最小公倍數: " + lcmResult +
                                ", 全部公因數: " + divisorsStr);
                    }
                } catch (NumberFormatException ex) {
                    resultLabel.setText("無效的輸入，請輸入正整數。");
                }
            }
        });

        frame.add(num1Label);
        frame.add(num1Field);
        frame.add(num2Label);
        frame.add(num2Field);
        frame.add(calculateButton);
        frame.add(resultLabel);

        frame.setVisible(true);
    }
}