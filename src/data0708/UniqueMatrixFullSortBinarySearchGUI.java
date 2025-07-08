package data0708;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class UniqueMatrixFullSortBinarySearchGUI extends JFrame {
    private JTextField sizeField;
    private JButton generateButton;
    private JPanel matrixPanel;
    private JPanel sortedMatrixPanel;
    private JTextField searchField;
    private JButton linearSearchButton;
    private JButton binarySearchButton;
    private JButton hashSetSearchButton;
    private JButton allSearchButton;
    private JLabel resultLabel;

    private int[][] matrix = null;
    private int[][] sortedMatrix = null;
    private HashSet<Integer> matrixSet = null;
    private int n = 0;

    public UniqueMatrixFullSortBinarySearchGUI() {
        setTitle("廖卿如 D1397221");
        setSize(950, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("矩陣大小 n:"));
        sizeField = new JTextField(5);
        inputPanel.add(sizeField);
        generateButton = new JButton("產生矩陣");
        inputPanel.add(generateButton);

        inputPanel.add(new JLabel("搜尋數字:"));
        searchField = new JTextField(5);
        inputPanel.add(searchField);

        // 搜尋按鈕區
        linearSearchButton = new JButton("循序搜尋");
        binarySearchButton = new JButton("二元搜尋");
        hashSetSearchButton = new JButton("HashSet搜尋");
        allSearchButton = new JButton("全部搜尋");
        JPanel searchButtonPanel = new JPanel();
        searchButtonPanel.add(linearSearchButton);
        searchButtonPanel.add(binarySearchButton);
        searchButtonPanel.add(hashSetSearchButton);
        searchButtonPanel.add(allSearchButton);

        // Panels for matrices
        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(1, 1));
        sortedMatrixPanel = new JPanel();
        sortedMatrixPanel.setLayout(new GridLayout(1, 1));

        // 分隔線
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        separator.setForeground(Color.DARK_GRAY);

        // Result display
        resultLabel = new JLabel(" ", SwingConstants.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 18));

        // Main layout
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

        add(resultLabel, BorderLayout.SOUTH);

        // Actions
        generateButton.addActionListener(e -> generateMatrix());
        linearSearchButton.addActionListener(e -> searchNumber("linear"));
        binarySearchButton.addActionListener(e -> searchNumber("binary"));
        hashSetSearchButton.addActionListener(e -> searchNumber("hashset"));
        allSearchButton.addActionListener(e -> searchAll());

        setVisible(true);
    }

    // 產生矩陣
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
        updateMatrixPanel(null, null);
        updateSortedMatrixPanel(null, null, false);
        resultLabel.setText(" ");
    }

    // 產生整體排序的矩陣
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

    // 顯示原始矩陣
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

    // 顯示排序後矩陣
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

    // 各種搜尋
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
            updateMatrixPanel(null, null); // 清除原始高亮
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

    // 執行所有搜尋
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(UniqueMatrixFullSortBinarySearchGUI::new);
    }
}