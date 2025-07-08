package data0708;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MatrixSearchGUI extends JFrame {
    private int[][] matrix;
    private int N;
    private JTextArea matrixArea;
    private JTextField inputN, inputNumber;
    private JLabel resultLabel, timeSeqLabel, timeBinLabel, timeHashLabel;

    private List<Integer> sortedList; // for binary search
    private Map<Integer, Point> hashMap; // for hash search

    public MatrixSearchGUI() {
        setTitle("N*N 矩陣搜尋比較");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("矩陣大小 N:"));
        inputN = new JTextField(5);
        inputPanel.add(inputN);

        JButton generateBtn = new JButton("產生矩陣");
        inputPanel.add(generateBtn);

        inputPanel.add(new JLabel("搜尋數字:"));
        inputNumber = new JTextField(8);
        inputPanel.add(inputNumber);
        JButton searchBtn = new JButton("搜尋");
        inputPanel.add(searchBtn);

        matrixArea = new JTextArea(15, 40);
        matrixArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        matrixArea.setEditable(false);

        resultLabel = new JLabel("搜尋結果：");
        timeSeqLabel = new JLabel("循序搜尋時間：");
        timeBinLabel = new JLabel("二元搜尋時間：");
        timeHashLabel = new JLabel("雜湊搜尋時間：");

        JScrollPane matrixScroll = new JScrollPane(matrixArea);

        JPanel resultPanel = new JPanel(new GridLayout(5, 1, 2, 2));
        resultPanel.add(resultLabel);
        resultPanel.add(timeSeqLabel);
        resultPanel.add(timeBinLabel);
        resultPanel.add(timeHashLabel);

        add(inputPanel, BorderLayout.NORTH);
        add(matrixScroll, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);

        generateBtn.addActionListener(e -> generateMatrix());
        searchBtn.addActionListener(e -> searchNumber());
    }

    private void generateMatrix() {
        String nStr = inputN.getText().trim();
        try {
            N = Integer.parseInt(nStr);
            if (N < 1 || N > 1000) {
                JOptionPane.showMessageDialog(this, "N請輸入1~1000之間的數字", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "請輸入合法的N", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int total = N * N;
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= total; i++) nums.add(i);
        Collections.shuffle(nums);

        matrix = new int[N][N];
        Iterator<Integer> it = nums.iterator();
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                matrix[i][j] = it.next();

        showMatrix();
        // Prepare sorted list and hash map for search
        sortedList = new ArrayList<>();
        hashMap = new HashMap<>();
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                sortedList.add(matrix[i][j]);
                hashMap.put(matrix[i][j], new Point(i, j));
            }
        Collections.sort(sortedList);
        resultLabel.setText("搜尋結果：");
        timeSeqLabel.setText("循序搜尋時間：");
        timeBinLabel.setText("二元搜尋時間：");
        timeHashLabel.setText("雜湊搜尋時間：");
    }

    private void showMatrix() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            for (int v : row) {
                sb.append(String.format("%4d", v));
            }
            sb.append("\n");
        }
        matrixArea.setText(sb.toString());
    }

    private void searchNumber() {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣！", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String inputStr = inputNumber.getText().trim();
        int target;
        try {
            target = Integer.parseInt(inputStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "請輸入合法的數字", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Sequential Search
        long t1 = System.nanoTime();
        Point pSeq = sequentialSearch(target);
        long t2 = System.nanoTime();

        // Binary Search (use sortedList)
        long t3 = System.nanoTime();
        int idx = Collections.binarySearch(sortedList, target);
        long t4 = System.nanoTime();

        // Hash Search
        long t5 = System.nanoTime();
        Point pHash = hashMap.get(target);
        long t6 = System.nanoTime();

        // Display results
        if (pSeq != null) {
            resultLabel.setText(String.format("搜尋結果：數字 %d 位置=(%d, %d)", target, pSeq.x + 1, pSeq.y + 1));
        } else {
            resultLabel.setText("搜尋結果：找不到");
        }
        timeSeqLabel.setText(String.format("循序搜尋時間：%.3f 毫秒", (t2 - t1) / 1_000_000.0));
        timeBinLabel.setText(idx >= 0
                ? String.format("二元搜尋時間：%.3f 毫秒", (t4 - t3) / 1_000_000.0)
                : "二元搜尋時間：找不到");
        timeHashLabel.setText(pHash != null
                ? String.format("雜湊搜尋時間：%.3f 毫秒", (t6 - t5) / 1_000_000.0)
                : "雜湊搜尋時間：找不到");
    }

    private Point sequentialSearch(int target) {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (matrix[i][j] == target)
                    return new Point(i, j);
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MatrixSearchGUI().setVisible(true);
        });
    }
}