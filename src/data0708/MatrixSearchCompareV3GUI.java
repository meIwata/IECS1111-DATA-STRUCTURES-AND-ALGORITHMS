package data0708;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class MatrixSearchCompareV3GUI extends JFrame {
    private JTextField sizeField;
    private JButton generateButton;
    private JPanel matrixPanel;
    private JPanel sortedMatrixPanel;
    private JTextField searchField;
    private JButton linearSearchButton;
    private JButton binarySearchButton;
    private JButton hashSetSearchButton;
    private JButton allSearchButton;
    private JButton autoTestButton;
    private JLabel resultLabel;
    private JLabel autoTestLabel;

    private int[][] matrix = null;
    private int[][] sortedMatrix = null;
    private HashSet<Integer> matrixSet = null;
    private int n = 0;

    public MatrixSearchCompareV3GUI() {
        setTitle("廖卿如 D1397221");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("矩陣大小 n:"));
        sizeField = new JTextField(5);
        inputPanel.add(sizeField);
        generateButton = new JButton("產生矩陣");
        inputPanel.add(generateButton);
        inputPanel.add(new JLabel("搜尋數字:"));
        searchField = new JTextField(5);
        inputPanel.add(searchField);

        linearSearchButton = new JButton("循序搜尋");
        binarySearchButton = new JButton("二元搜尋");
        hashSetSearchButton = new JButton("HashSet搜尋");
        allSearchButton = new JButton("全部搜尋");
        autoTestButton = new JButton("自動測試20次");
        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.add(linearSearchButton);
        searchButtonPanel.add(binarySearchButton);
        searchButtonPanel.add(hashSetSearchButton);
        searchButtonPanel.add(allSearchButton);
        searchButtonPanel.add(autoTestButton);

        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(1, 1));
        sortedMatrixPanel = new JPanel();
        sortedMatrixPanel.setLayout(new GridLayout(1, 1));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        separator.setForeground(Color.DARK_GRAY);

        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));
        autoTestLabel = new JLabel(" ", SwingConstants.CENTER);
        autoTestLabel.setFont(new Font("Arial", Font.BOLD, 16));

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(searchButtonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(matrixPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        centerPanel.add(separator);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        centerPanel.add(sortedMatrixPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(resultLabel);
        bottomPanel.add(autoTestLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateMatrix());
        linearSearchButton.addActionListener(e -> searchNumber("linear"));
        binarySearchButton.addActionListener(e -> searchNumber("binary"));
        hashSetSearchButton.addActionListener(e -> searchNumber("hashset"));
        allSearchButton.addActionListener(e -> searchAll());
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
        sortedMatrix = null;
        // 若 n >= 1000，不顯示圖形，僅顯示提示
        if (n >= 1000) {
            matrixPanel.removeAll();
            matrixPanel.setLayout(new BorderLayout());
            matrixPanel.add(new JLabel("n >= 1000，為避免當機不顯示矩陣圖形！", SwingConstants.CENTER), BorderLayout.CENTER);
            matrixPanel.revalidate();
            matrixPanel.repaint();
            sortedMatrixPanel.removeAll();
            sortedMatrixPanel.revalidate();
            sortedMatrixPanel.repaint();
        } else {
            updateMatrixPanel(null, null);
            updateSortedMatrixPanel(null, null, false);
        }
        resultLabel.setText(" ");
        autoTestLabel.setText(" ");
    }

    private void createFullSortedMatrix() {
        if (matrix == null) return;
        int[] arr = new int[n * n];
        int idx = 0;
        for (int[] row : matrix)
            for (int num : row)
                arr[idx++] = num;
        Arrays.sort(arr);
        sortedMatrix = new int[n][n];
        idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                sortedMatrix[i][j] = arr[idx++];
    }

    private void updateMatrixPanel(Integer foundRow, Integer foundCol) {
        matrixPanel.removeAll();
        if (matrix == null) {
            matrixPanel.revalidate();
            matrixPanel.repaint();
            return;
        }
        matrixPanel.setLayout(new GridLayout(n, n, 5, 5));
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                JLabel label = new JLabel(String.valueOf(matrix[i][j]), SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setFont(new Font("Arial", Font.BOLD, 18));
                if (foundRow != null && foundCol != null && i == foundRow && j == foundCol)
                    label.setBackground(Color.YELLOW);
                else
                    label.setBackground(Color.WHITE);
                matrixPanel.add(label);
            }
        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    private void updateSortedMatrixPanel(Integer foundRow, Integer foundCol, boolean show) {
        sortedMatrixPanel.removeAll();
        if (!show || matrix == null) {
            sortedMatrixPanel.revalidate();
            sortedMatrixPanel.repaint();
            return;
        }
        if (sortedMatrix == null) createFullSortedMatrix();
        sortedMatrixPanel.setLayout(new GridLayout(n, n, 5, 5));
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                JLabel label = new JLabel(String.valueOf(sortedMatrix[i][j]), SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setFont(new Font("Arial", Font.BOLD, 18));
                if (foundRow != null && foundCol != null && i == foundRow && j == foundCol)
                    label.setBackground(Color.GREEN);
                else
                    label.setBackground(Color.WHITE);
                sortedMatrixPanel.add(label);
            }
        sortedMatrixPanel.revalidate();
        sortedMatrixPanel.repaint();
    }

    private void searchNumber(String mode) {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String searchText = searchField.getText();
        int target;
        try {
            target = Integer.parseInt(searchText.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "請輸入要搜尋的整數。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        long startTime = System.nanoTime();
        boolean found = false;
        int foundRow = -1, foundCol = -1;
        if ("linear".equals(mode)) {
            outer:
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (matrix[i][j] == target) {
                        found = true;
                        foundRow = i;
                        foundCol = j;
                        break outer;
                    }
            updateMatrixPanel(found ? foundRow : null, found ? foundCol : null);
            updateSortedMatrixPanel(null, null, false);
            long duration = System.nanoTime() - startTime;
            resultLabel.setText(found
                    ? "【循序搜尋】找到！原始矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms"
                    : "【循序搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms");
        } else if ("binary".equals(mode)) {
            if (sortedMatrix == null) createFullSortedMatrix();
            int total = n * n;
            int[] arr = new int[total];
            int idx = 0;
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    arr[idx++] = sortedMatrix[i][j];
            int pos = Arrays.binarySearch(arr, target);
            if (pos >= 0) {
                found = true;
                foundRow = pos / n;
                foundCol = pos % n;
            }
            updateMatrixPanel(null, null);
            updateSortedMatrixPanel(found ? foundRow : null, found ? foundCol : null, true);
            long duration = System.nanoTime() - startTime;
            resultLabel.setText(found
                    ? "【二元搜尋】找到！排序後新矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms"
                    : "【二元搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms");
        } else if ("hashset".equals(mode)) {
            if (matrixSet.contains(target)) {
                found = true;
                outer:
                for (int i = 0; i < n; i++)
                    for (int j = 0; j < n; j++)
                        if (matrix[i][j] == target) {
                            foundRow = i;
                            foundCol = j;
                            break outer;
                        }
            }
            updateMatrixPanel(found ? foundRow : null, found ? foundCol : null);
            updateSortedMatrixPanel(null, null, false);
            long duration = System.nanoTime() - startTime;
            resultLabel.setText(found
                    ? "【HashSet搜尋】找到！原始矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms"
                    : "【HashSet搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms");
        }
    }

    private void searchAll() {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String searchText = searchField.getText();
        int target;
        try {
            target = Integer.parseInt(searchText.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "請輸入要搜尋的整數。", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        // Linear search
        long startTime = System.nanoTime();
        boolean found = false;
        int foundRow = -1, foundCol = -1;
        outer:
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (matrix[i][j] == target) {
                    found = true;
                    foundRow = i;
                    foundCol = j;
                    break outer;
                }
        long duration = System.nanoTime() - startTime;
        sb.append(found
                ? "【循序搜尋】找到！原始矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms<br>"
                : "【循序搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms<br>");
        // Binary search
        startTime = System.nanoTime();
        if (sortedMatrix == null) createFullSortedMatrix();
        int total = n * n;
        int[] arr = new int[total];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                arr[idx++] = sortedMatrix[i][j];
        int pos = Arrays.binarySearch(arr, target);
        found = false;
        foundRow = foundCol = -1;
        if (pos >= 0) {
            found = true;
            foundRow = pos / n;
            foundCol = pos % n;
        }
        duration = System.nanoTime() - startTime;
        sb.append(found
                ? "【二元搜尋】找到！排序後新矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms<br>"
                : "【二元搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms<br>");
        // HashSet search
        startTime = System.nanoTime();
        found = false;
        foundRow = foundCol = -1;
        if (matrixSet.contains(target)) {
            found = true;
            outer:
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (matrix[i][j] == target) {
                        foundRow = i;
                        foundCol = j;
                        break outer;
                    }
        }
        duration = System.nanoTime() - startTime;
        sb.append(found
                ? "【HashSet搜尋】找到！原始矩陣位置: (" + (foundRow + 1) + ", " + (foundCol + 1) + ")，耗時: " + (duration / 1e6) + " ms"
                : "【HashSet搜尋】找不到該數字，耗時: " + (duration / 1e6) + " ms");
        updateMatrixPanel(null, null);
        updateSortedMatrixPanel(null, null, false);
        resultLabel.setText("<html>" + sb.toString() + "</html>");
    }

    // 自動測試20次平均搜尋時間（只計算查詢本身）
    private void autoTest() {
        if (matrix == null) {
            JOptionPane.showMessageDialog(this, "請先產生矩陣。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int total = n * n;
        Random rand = new Random();
        double sumLinear = 0, sumBinary = 0, sumHash = 0;
        for (int t = 0; t < 20; t++) {
            int target = rand.nextInt(total) + 1;
            // Linear
            long start = System.nanoTime();
            boolean found = false;
            outer:
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (matrix[i][j] == target) {
                        found = true;
                        break outer;
                    }
            long duration = System.nanoTime() - start;
            sumLinear += duration / 1e6;
            // Binary
            if (sortedMatrix == null) createFullSortedMatrix();
            int[] arr = new int[total];
            int idx = 0;
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    arr[idx++] = sortedMatrix[i][j];
            start = System.nanoTime();
            int pos = Arrays.binarySearch(arr, target);
            duration = System.nanoTime() - start;
            sumBinary += duration / 1e6;
            // HashSet
            start = System.nanoTime();
            boolean hashFound = matrixSet.contains(target);
            duration = System.nanoTime() - start;
            sumHash += duration / 1e6;
        }
        autoTestLabel.setText(String.format("20次隨機搜尋平均時間：循序搜尋 %.4f ms，二元搜尋 %.4f ms，HashSet搜尋 %.4f ms", sumLinear/20, sumBinary/20, sumHash/20));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MatrixSearchCompareV3GUI::new);
    }
}
