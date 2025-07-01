import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageViewer extends JFrame {
    private JLabel imageLabel;
    private List<File> imageFiles;
    private int currentIndex = -1;

    public ImageViewer() {
        setTitle("Java 看圖軟體");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 圖片顯示區
        imageLabel = new JLabel("顯示圖片區域", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(600, 450));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(imageLabel, BorderLayout.CENTER);

        // 按鈕區
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton prevBtn = new JButton("上一張");
        JButton selectBtn = new JButton("選取圖片所在目錄");
        JButton nextBtn = new JButton("下一張");

        buttonPanel.add(prevBtn);
        buttonPanel.add(selectBtn);
        buttonPanel.add(nextBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // 事件處理
        selectBtn.addActionListener(e -> chooseDirectory());
        prevBtn.addActionListener(e -> showImage(currentIndex - 1));
        nextBtn.addActionListener(e -> showImage(currentIndex + 1));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void chooseDirectory() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            imageFiles = Arrays.stream(dir.listFiles((d, name) -> {
                String n = name.toLowerCase();
                return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".gif");
            })).collect(Collectors.toList());
            if (!imageFiles.isEmpty()) {
                showImage(0);
            }
        }
    }

    private void showImage(int idx) {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        if (idx < 0 || idx >= imageFiles.size()) return;
        currentIndex = idx;
        try {
            BufferedImage original = ImageIO.read(imageFiles.get(currentIndex));
            int w = original.getWidth() / 2;
            int h = original.getHeight() / 2;
            Image scaled = original.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaled));
            imageLabel.setText("");
        } catch (Exception ex) {
            imageLabel.setText("無法載入圖片");
            imageLabel.setIcon(null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageViewer::new);
    }
}