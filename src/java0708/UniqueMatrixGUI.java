package java0708;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class UniqueMatrixGUI extends JFrame {
    private JTextField sizeField;
    private JButton generateButton;
    private JPanel matrixPanel;

    public UniqueMatrixGUI() {
        setTitle("矩陣顯示");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel for input
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("請輸入矩陣大小 n:"));
        sizeField = new JTextField(5);
        inputPanel.add(sizeField);
        generateButton = new JButton("產生矩陣");
        inputPanel.add(generateButton);

        // Panel to display matrix
        matrixPanel = new JPanel();
        matrixPanel.setLayout(new GridLayout(1, 1)); // Placeholder

        // Layout
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(matrixPanel), BorderLayout.CENTER);

        // Button Action
        generateButton.addActionListener(e -> generateMatrix());

        setVisible(true);
    }

    private void generateMatrix() {
        String text = sizeField.getText();
        int n;
        try {
            n = Integer.parseInt(text);
            if (n <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a positive integer for n.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int total = n * n;
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= total; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        // Remove old matrix
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridLayout(n, n, 5, 5));

        Iterator<Integer> it = numbers.iterator();
        for (int i = 0; i < n * n; i++) {
            int value = it.next();
            JLabel label = new JLabel(String.valueOf(value), SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            label.setFont(new Font("Arial", Font.BOLD, 18));
            matrixPanel.add(label);
        }

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    public static void main(String[] args) {
        // Set system look and feel for better GUI appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(UniqueMatrixGUI::new);
    }
}