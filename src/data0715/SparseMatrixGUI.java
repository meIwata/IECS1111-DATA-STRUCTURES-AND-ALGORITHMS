package data0715;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SparseMatrixGUI extends JFrame {
    private JTextField sizeField;
    private JTextField densityField;
    private JButton generateButton;
    private JButton generateButton2;
    private JTable matrixTable;
    private JScrollPane scrollPane;
    private JTextArea sparseTextArea;
    private int[][] lastMatrix;
    private JTable matrixTable2;
    private JTextArea sparseTextArea2;
    private int[][] lastMatrix2;
    private JLabel messageLabel;
    private JButton addButton;
    private JButton subButton;
    private JTable resultTable;
    private JTextArea resultSparseTextArea;
    private JButton transposeButton;
    private JTable transposeTable;
    private JTextArea transposeSparseTextArea;
    private JButton transposeButton1;
    private JTable transposeTable1;
    private JTextArea transposeSparseTextArea1;
    private JLabel timeLabel1;
    private JLabel timeLabel2;

    public SparseMatrixGUI() {
        setTitle("稀疏矩陣產生器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- 上方訊息與輸入區 ---
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 1));
        JPanel row1 = new JPanel();
        row1.add(new JLabel("矩陣大小 n:"));
        sizeField = new JTextField("5", 5);
        row1.add(sizeField);
        row1.add(new JLabel("密集度 (0~1):"));
        densityField = new JTextField("0.2", 5);
        row1.add(densityField);
        inputPanel.add(row1);
        JPanel row2 = new JPanel();
        generateButton = new JButton("產生矩陣1");
        row2.add(generateButton);
        generateButton2 = new JButton("產生矩陣2");
        row2.add(generateButton2);
        addButton = new JButton("矩陣相加");
        row2.add(addButton);
        subButton = new JButton("矩陣相減");
        row2.add(subButton);
        transposeButton = new JButton("第二個稀疏矩陣轉置");
        row2.add(transposeButton);
        transposeButton1 = new JButton("矩陣1原始轉置");
        row2.add(transposeButton1);
        inputPanel.add(row2);
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(messageLabel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- 主體區域 ---
        JPanel gridPanel = new JPanel(new GridLayout(2, 4));
        matrixTable = new JTable();
        gridPanel.add(new JScrollPane(matrixTable));
        sparseTextArea = new JTextArea(8, 30);
        sparseTextArea.setEditable(false);
        gridPanel.add(new JScrollPane(sparseTextArea));
        matrixTable2 = new JTable();
        gridPanel.add(new JScrollPane(matrixTable2));
        sparseTextArea2 = new JTextArea(8, 30);
        sparseTextArea2.setEditable(false);
        gridPanel.add(new JScrollPane(sparseTextArea2));
        resultTable = new JTable();
        gridPanel.add(new JScrollPane(resultTable));
        resultSparseTextArea = new JTextArea(8, 30);
        resultSparseTextArea.setEditable(false);
        gridPanel.add(new JScrollPane(resultSparseTextArea));
        transposeTable = new JTable();
        gridPanel.add(new JScrollPane(transposeTable));
        transposeSparseTextArea = new JTextArea(8, 30);
        transposeSparseTextArea.setEditable(false);
        gridPanel.add(new JScrollPane(transposeSparseTextArea));
        transposeTable1 = new JTable();
        gridPanel.add(new JScrollPane(transposeTable1));
        transposeSparseTextArea1 = new JTextArea(8, 30);
        transposeSparseTextArea1.setEditable(false);
        gridPanel.add(new JScrollPane(transposeSparseTextArea1));
        add(gridPanel, BorderLayout.CENTER);

        // --- 時間顯示區 ---
        timeLabel1 = new JLabel("矩陣1原始轉置執行時間: ");
        timeLabel2 = new JLabel("矩陣2快速轉置執行時間: ");
        JPanel timePanel = new JPanel(new GridLayout(2, 1));
        timePanel.add(timeLabel1);
        timePanel.add(timeLabel2);
        add(timePanel, BorderLayout.SOUTH);

        // --- 事件 ---
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMatrix1();
            }
        });
        generateButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMatrix2();
            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateMatrix(true);
            }
        });
        subButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                operateMatrix(false);
            }
        });
        transposeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transposeSparseMatrix2();
            }
        });
        transposeButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transposeMatrix1();
            }
        });

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateMatrix1() {
        int n;
        double density;
        try {
            n = Integer.parseInt(sizeField.getText());
            density = Double.parseDouble(densityField.getText());
            if (n <= 0 || density < 0 || density > 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入正確的數值 (n>0, 0<=密集度<=1)", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int[][] matrix = createSparseMatrix(n, density);
        lastMatrix = matrix;
        showMatrix(matrixTable, matrix);
        showSparseMatrix(sparseTextArea, matrix);
    }

    private void generateMatrix2() {
        int n;
        double density;
        try {
            n = Integer.parseInt(sizeField.getText());
            density = Double.parseDouble(densityField.getText());
            if (n <= 0 || density < 0 || density > 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入正確的數值 (n>0, 0<=密集度<=1)", "輸入錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int[][] matrix = createSparseMatrix(n, density);
        lastMatrix2 = matrix;
        showMatrix(matrixTable2, matrix);
        showSparseMatrix(sparseTextArea2, matrix);
    }

    private int[][] createSparseMatrix(int n, double density) {
        int[][] matrix = new int[n][n];
        Random rand = new Random();
        int total = n * n;
        int nonZero = (int) Math.round(total * density);
        int count = 0;
        boolean[][] filled = new boolean[n][n];
        while (count < nonZero) {
            int i = rand.nextInt(n);
            int j = rand.nextInt(n);
            if (!filled[i][j]) {
                matrix[i][j] = rand.nextInt(9) + 1; // 1~9的隨機數
                filled[i][j] = true;
                count++;
            }
        }
        return matrix;
    }

    private void showMatrix(JTable table, int[][] matrix) {
        int n = matrix.length;
        String[] columns = new String[n];
        for (int i = 0; i < n; i++) columns[i] = String.valueOf(i);
        String[][] data = new String[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                data[i][j] = String.valueOf(matrix[i][j]);
        DefaultTableModel model = new DefaultTableModel(data, columns);
        table.setModel(model);
    }

    private void showSparseMatrix(JTextArea area, int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        sb.append("稀疏矩陣(row, col, value):\n");
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] != 0) {
                    sb.append("(").append(i).append(", ").append(j).append(", ").append(matrix[i][j]).append(")\n");
                }
            }
        }
        area.setText(sb.toString());
    }

    private void operateMatrix(boolean isAdd) {
        messageLabel.setText("");
        if (lastMatrix == null || lastMatrix2 == null) {
            messageLabel.setText("請先產生兩個矩陣");
            return;
        }
        int n1 = lastMatrix.length;
        int n2 = lastMatrix2.length;
        if (n1 != n2) {
            messageLabel.setText("大小不一致, 無法相加或相減!");
            return;
        }
        int[][] result = new int[n1][n1];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n1; j++) {
                result[i][j] = isAdd ? lastMatrix[i][j] + lastMatrix2[i][j] : lastMatrix[i][j] - lastMatrix2[i][j];
            }
        }
        showMatrix(resultTable, result);
        showSparseMatrix(resultSparseTextArea, result);
    }

    // --- 快速轉置方法 ---
    private void transposeMatrix1() {
        messageLabel.setText("");
        if (lastMatrix == null) {
            messageLabel.setText("請先產生第一個矩陣");
            return;
        }
        int n = lastMatrix.length;
        int[][] trans = new int[n][n];
        long start = System.nanoTime();
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                trans[j][i] = lastMatrix[i][j];
        long end = System.nanoTime();
        showMatrix(transposeTable1, trans);
        showSparseMatrix(transposeSparseTextArea1, trans);
        timeLabel1.setText("矩陣1原始轉置執行時間: " + (end - start) / 1_000_000.0 + " ms");
    }

    private void transposeSparseMatrix2() {
        messageLabel.setText("");
        if (lastMatrix2 == null) {
            messageLabel.setText("請先產生第二個矩陣");
            return;
        }
        int n = lastMatrix2.length;
        long start = System.nanoTime();
        int nonZero = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (lastMatrix2[i][j] != 0) nonZero++;
        int[][] triples = new int[nonZero][3];
        int idx = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (lastMatrix2[i][j] != 0) {
                    triples[idx][0] = i;
                    triples[idx][1] = j;
                    triples[idx][2] = lastMatrix2[i][j];
                    idx++;
                }
        int[] colCount = new int[n];
        for (int i = 0; i < nonZero; i++) colCount[triples[i][1]]++;
        int[] pos = new int[n];
        pos[0] = 0;
        for (int i = 1; i < n; i++) pos[i] = pos[i-1] + colCount[i-1];
        int[][] transTriples = new int[nonZero][3];
        for (int i = 0; i < nonZero; i++) {
            int col = triples[i][1];
            int p = pos[col]++;
            transTriples[p][0] = triples[i][1];
            transTriples[p][1] = triples[i][0];
            transTriples[p][2] = triples[i][2];
        }
        int[][] transMatrix = new int[n][n];
        for (int i = 0; i < nonZero; i++) {
            int r = transTriples[i][0];
            int c = transTriples[i][1];
            int v = transTriples[i][2];
            transMatrix[r][c] = v;
        }
        long end = System.nanoTime();
        showMatrix(transposeTable, transMatrix);
        showSparseMatrix(transposeSparseTextArea, transMatrix);
        timeLabel2.setText("矩陣2快速轉置執行時間: " + (end - start) / 1_000_000.0 + " ms");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SparseMatrixGUI::new);
    }
}
