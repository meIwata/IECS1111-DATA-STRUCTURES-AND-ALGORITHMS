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
        generateButton = new JButton("產生矩陣1");
        inputPanel.add(generateButton);
        generateButton2 = new JButton("產生矩陣2");
        inputPanel.add(generateButton2);
        add(inputPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2));
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
        add(gridPanel, BorderLayout.CENTER);

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SparseMatrixGUI::new);
    }
}
