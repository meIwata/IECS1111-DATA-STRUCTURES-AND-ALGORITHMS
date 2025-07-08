package data0708;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MatrixSearchCompareGUI extends JFrame {
    private JTextField sizeField;
    private JButton generateButton;
    private JPanel matrixPanel;
    private JTextArea resultArea;
    private JButton slowSearchButton;
    private JButton normalSearchButton;
    private JButton fastSearchButton;
    private JButton autoTestButton;

    private int[][] matrix = null;
    private HashSet<Integer> matrixSet = null;
    private int n = 0;
    private int[] sortedArray = null; // 新增成員變數

    public MatrixSearchCompareGUI() {
        setTitle("二維陣列搜尋演算法比較");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("矩陣大小 n:"));
        sizeField = new JTextField(5);
        inputPanel.add(sizeField);
        generateButton = new JButton("產生矩陣");
        inputPanel.add(generateButton);

        slowSearchButton = new JButton("較慢搜尋");
        normalSearchButton = new JButton("普通搜尋");
        fastSearchButton = new JButton("快速搜尋");
        autoTestButton = new JButton("自動測試20次");

        JPanel searchPanel = new JPanel();
        searchPanel.add(slowSearchButton);
        searchPanel.add(normalSearchButton);
        searchPanel.add(fastSearchButton);
        searchPanel.add(autoTestButton);

        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(1, 1));
        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(matrixPanel, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.EAST);

        generateButton.addActionListener(e -> generateMatrix());
        slowSearchButton.addActionListener(e -> searchRandom("slow"));
        normalSearchButton.addActionListener(e -> searchRandom("normal"));
        fastSearchButton.addActionListener(e -> searchRandom("fast"));
        autoTestButton.addActionListener(e -> autoTest());

        setVisible(true);
    }

    private void generateMatrix() {
        String text = sizeField.getText();
        try {
            n = Integer.parseInt(text.trim());
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "請輸入正整數 n。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int total = n * n;
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= total; i++) numbers.add(i);
        Collections.shuffle(numbers);
        matrix = new int[n][n];
        matrixSet = new HashSet<>();
        Iterator<Integer> it = numbers.iterator();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                int val = it.next();
                matrix[i][j] = val;
                matrixSet.add(val);
            }
        // 建立排序好的一維陣列
        sortedArray = new int[n * n];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sortedArray[idx++] = matrix[i][j];
        Arrays.sort(sortedArray);
        updateMatrixPanel();
        resultArea.setText("");
    }

    private void updateMatrixPanel() {
        matrixPanel.removeAll();
        if (matrix == null) {
            matrixPanel.revalidate();
            matrixPanel.repaint();
            return;
        }
        // 只顯示部分矩陣，避免大尺寸時 GUI 當掉
        int maxShow = Math.min(n, 20); // 最多顯示20x20
        matrixPanel.setLayout(new GridLayout(maxShow, maxShow, 3, 3));
        for (int i = 0; i < maxShow; i++)
            for (int j = 0; j < maxShow; j++) {
                JLabel label = new JLabel(String.valueOf(matrix[i][j]), SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setFont(new Font("Arial", Font.BOLD, 16));
                label.setBackground(Color.WHITE);
                matrixPanel.add(label);
            }
        if (n > 20) {
            JLabel info = new JLabel("僅顯示前20x20，完整矩陣未顯示以避免當機", SwingConstants.CENTER);
            info.setFont(new Font("Arial", Font.PLAIN, 14));
            info.setForeground(Color.RED);
            matrixPanel.add(info);
        }
        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    // 較慢搜尋：循序搜尋
    private boolean slowSearch(int target) {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] == target)
                    return true;
        return false;
    }

    // 普通搜尋：二分搜尋（直接用已排序陣列）
    private boolean normalSearch(int target) {
        return Arrays.binarySearch(sortedArray, target) >= 0;
    }

    // 快速搜尋：HashSet搜尋
    private boolean fastSearch(int target) {
        return matrixSet.contains(target);
    }

    // 單次隨機搜尋
    private void searchRandom(String mode) {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int total = n * n;
        int target = new Random().nextInt(total) + 1;
        long start = System.nanoTime();
        boolean found = false;
        if (mode.equals("slow")) found = slowSearch(target);
        else if (mode.equals("normal")) found = normalSearch(target);
        else if (mode.equals("fast")) found = fastSearch(target);
        long duration = System.nanoTime() - start;
        resultArea.append(String.format("搜尋值: %d, %s搜尋, 結果: %s, 耗時: %.4f ms\n",
                target,
                mode.equals("slow") ? "較慢" : mode.equals("normal") ? "普通" : "快速",
                found ? "找到" : "找不到",
                duration / 1e6));
    }

    // 自動測試20次
    private void autoTest() {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int total = n * n;
        Random rand = new Random();
        double slowSum = 0, normalSum = 0, fastSum = 0;
        for (int t = 0; t < 20; t++) {
            int target = rand.nextInt(total) + 1;
            long start = System.nanoTime();
            slowSearch(target);
            slowSum += (System.nanoTime() - start) / 1e6;
            start = System.nanoTime();
            normalSearch(target);
            normalSum += (System.nanoTime() - start) / 1e6;
            start = System.nanoTime();
            fastSearch(target);
            fastSum += (System.nanoTime() - start) / 1e6;
        }
        resultArea.append(String.format("\n20次隨機搜尋平均耗時：\n較慢搜尋：%.4f ms\n普通搜尋：%.4f ms\n快速搜尋：%.4f ms\n",
                slowSum / 20.0, normalSum / 20.0, fastSum / 20.0));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MatrixSearchCompareGUI::new);
    }
}
