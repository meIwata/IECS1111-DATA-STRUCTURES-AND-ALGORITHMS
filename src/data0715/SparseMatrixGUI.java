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
    private JButton showSparseButton;
    private JTable matrixTable;
    private JScrollPane scrollPane;
    private JTextArea sparseTextArea;
    private int[][] lastMatrix;

    public SparseMatrixGUI() {
        setTitle("稀疏矩陣產生器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("矩陣大小 n:"));
        sizeField = new JTextField("5", 5);
        inputPanel.add(sizeField);
        inputPanel.add(new JLabel("密集度 (0~1):"));
        densityField = new JTextField("0.2", 5);
        inputPanel.add(densityField);
        generateButton = new JButton("生成矩陣");
        inputPanel.add(generateButton);
        showSparseButton = new JButton("顯示稀疏表示");
        inputPanel.add(showSparseButton);
        add(inputPanel, BorderLayout.NORTH);

        matrixTable = new JTable();
        scrollPane = new JScrollPane(matrixTable);
        add(scrollPane, BorderLayout.CENTER);

        sparseTextArea = new JTextArea(8, 40);
        sparseTextArea.setEditable(false);
        add(new JScrollPane(sparseTextArea), BorderLayout.SOUTH);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMatrix();
            }
        });
        showSparseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSparseMatrix();
            }
        });

        setSize(600, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generateMatrix() {
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
        showMatrix(matrix);
        sparseTextArea.setText("");
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

    private void showMatrix(int[][] matrix) {
        int n = matrix.length;
        String[] columns = new String[n];
        for (int i = 0; i < n; i++) columns[i] = String.valueOf(i);
        String[][] data = new String[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                data[i][j] = String.valueOf(matrix[i][j]);
        DefaultTableModel model = new DefaultTableModel(data, columns);
        matrixTable.setModel(model);
    }

    private void showSparseMatrix() {
        if (lastMatrix == null) {
            sparseTextArea.setText("請先生成矩陣");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("稀疏矩陣(row, col, value):\n");
        int n = lastMatrix.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (lastMatrix[i][j] != 0) {
                    sb.append("(").append(i).append(", ").append(j).append(", ").append(lastMatrix[i][j]).append(")\n");
                }
            }
        }
        sparseTextArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SparseMatrixGUI::new);
    }
}
