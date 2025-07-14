package data0714;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;

public class FactorialCompareGUI extends JFrame {
    private JTextField inputField;
    private JTextArea resultArea;

    public FactorialCompareGUI() {
        setTitle("N! 遞迴與迴圈比較");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("請輸入N: "));
        inputField = new JTextField(10);
        inputPanel.add(inputField);
        JButton calcButton = new JButton("計算");
        inputPanel.add(calcButton);
        JButton clearButton = new JButton("清除"); // 新增清除按鈕
        inputPanel.add(clearButton);
        add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // 讓結果區域可隨視窗變動
        resultArea.setPreferredSize(null);
        scrollPane.setPreferredSize(null);

        calcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateAndShow();
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
            }
        });
    }

    private void calculateAndShow() {
        String input = inputField.getText();
        try {
            int n = Integer.parseInt(input);
            if (n < 0) {
                appendResult("N 必須為非負整數");
                return;
            }
            // 迴圈法執行10次取平均
            long loopTotalTime = 0;
            long loopResult = 0;
            for (int i = 0; i < 10; i++) {
                long startLoop = System.nanoTime();
                loopResult = factorialLoop(n);
                long endLoop = System.nanoTime();
                loopTotalTime += (endLoop - startLoop);
            }
            long loopAvgTime = loopTotalTime / 10;

            // 遞迴法(BigInteger)執行10次取平均
            long recTotalTime = 0;
            BigInteger recResult = BigInteger.ZERO;
            boolean recOverflow = false;
            for (int i = 0; i < 10; i++) {
                long startRec = System.nanoTime();
                try {
                    recResult = factorialRecursiveBig(n);
                } catch (StackOverflowError err) {
                    recOverflow = true;
                    break;
                }
                long endRec = System.nanoTime();
                recTotalTime += (endRec - startRec);
            }
            long recAvgTime = recOverflow ? -1 : recTotalTime / 10;

            StringBuilder sb = new StringBuilder();
            sb.append("N = ").append(n).append("\n");
            if (loopResult < 0) {
                sb.append("[迴圈] 結果: 數字過大計算有誤\n");
            } else {
                sb.append("[迴圈] 結果: ").append(loopResult).append("\n");
            }
            sb.append("[迴圈] 平均執行時間(10次): ").append(loopAvgTime).append(" ns\n");
            if (recOverflow) {
                sb.append("[遞迴(BigInteger)] 結果: StackOverflow\n");
            } else {
                sb.append("[遞迴(BigInteger)] 結果: ").append(recResult.toString()).append("\n");
            }
            if (recAvgTime < 0) {
                sb.append("[遞迴(BigInteger)] 平均執行時間(10次): 無法計算\n");
            } else {
                sb.append("[遞迴(BigInteger)] 平均執行時間(10次): ").append(recAvgTime).append(" ns\n");
            }
            appendResult(sb.toString());
        } catch (NumberFormatException ex) {
            appendResult("請輸入正確的整數");
        }
    }

    // 將新結果加到舊結果下方
    private void appendResult(String text) {
        if (!resultArea.getText().isEmpty()) {
            resultArea.append("\n-----------------------------\n");
        }
        resultArea.append(text);
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    private long factorialLoop(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
            if (result < 0) return -1; // overflow
        }
        return result;
    }

    private BigInteger factorialRecursiveBig(int n) {
        if (n == 0 || n == 1) return BigInteger.ONE;
        return BigInteger.valueOf(n).multiply(factorialRecursiveBig(n - 1));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FactorialCompareGUI().setVisible(true);
        });
    }
}
