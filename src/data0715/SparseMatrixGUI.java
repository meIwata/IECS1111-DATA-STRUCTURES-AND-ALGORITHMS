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
    private JTable matrixTable;
    private JScrollPane scrollPane;

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
        add(inputPanel, BorderLayout.NORTH);

        matrixTable = new JTable();
        scrollPane = new JScrollPane(matrixTable);
        add(scrollPane, BorderLayout.CENTER);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateMatrix();
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
        showMatrix(matrix);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SparseMatrixGUI::new);
    }
}

