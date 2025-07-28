package data0728;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;

public class LimitedQueueExample extends JFrame {
    private static final int QUEUE_CAPACITY = 5;
    private Queue<String> queue = new LinkedList<>();
    private JTextArea queueArea;
    private JTextField inputField;
    private JLabel statusLabel;

    public LimitedQueueExample() {
        setTitle("Queue 上限5個元素範例");
        setSize(400, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 輸入區
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("輸入內容:"));
        inputField = new JTextField(10);
        inputPanel.add(inputField);
        JButton enqueueButton = new JButton("加入Queue");
        inputPanel.add(enqueueButton);
        add(inputPanel, BorderLayout.NORTH);

        // 顯示Queue內容
        queueArea = new JTextArea();
        queueArea.setEditable(false);
        add(new JScrollPane(queueArea), BorderLayout.CENTER);

        // 下方功能欄
        JPanel bottomPanel = new JPanel();
        JButton dequeueButton = new JButton("取出Queue");
        bottomPanel.add(dequeueButton);
        statusLabel = new JLabel(" ");
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // 事件處理
        enqueueButton.addActionListener(e -> enqueue());
        dequeueButton.addActionListener(e -> dequeue());

        updateQueueArea();
    }

    private void enqueue() {
        String value = inputField.getText().trim();
        if (queue.size() >= QUEUE_CAPACITY) {
            statusLabel.setText("空間不足，最多只能放5個元素！");
        } else if (!value.isEmpty()) {
            queue.offer(value);
            statusLabel.setText("已加入: " + value);
            inputField.setText("");
            updateQueueArea();
        }
    }

    private void dequeue() {
        if (queue.isEmpty()) {
            statusLabel.setText("Queue 是空的");
        } else {
            String removed = queue.poll();
            statusLabel.setText("已取出: " + removed);
            updateQueueArea();
        }
    }

    private void updateQueueArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("Queue 內容 (上限5):\n");
        for (String item : queue) {
            sb.append(item).append("\n");
        }
        queueArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LimitedQueueExample().setVisible(true));
    }
}