package data0729;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class ProducerConsumerMinHeap extends JFrame {

    private final DefaultListModel<String> bufferListModel = new DefaultListModel<>();
    private final JTextField bufferSizeField = new JTextField("10", 5); // buffer size input, compact
    private final JButton startButton = new JButton("開始生產");
    private final JList<String> bufferList = new JList<>(bufferListModel);
    private volatile boolean running = false;
    private volatile int bufferSize = 10;

    private final PriorityQueue<Item> buffer = new PriorityQueue<>();
    private final HeapAnimationPanel animationPanel = new HeapAnimationPanel();

    public ProducerConsumerMinHeap() {
        super("機台流程軟體 (Min Heap)");

        // Top panel for buffer size and start button (side by side)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Buffer Size:"));
        topPanel.add(bufferSizeField);
        topPanel.add(startButton);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);

        bufferList.setFont(new Font("monospaced", Font.PLAIN, 14));
        add(new JScrollPane(bufferList), BorderLayout.CENTER);
        add(animationPanel, BorderLayout.SOUTH);

        setSize(600, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Start button listener
        startButton.addActionListener(e -> {
            try {
                bufferSize = Math.max(1, Integer.parseInt(bufferSizeField.getText().trim()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Buffer size必須為正整數", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }
            startButton.setEnabled(false);
            bufferSizeField.setEnabled(false);
            running = true;
            startThreads();
        });
    }

    private void startThreads() {
        // Producer thread
        Thread producer = new Thread(() -> {
            Random rand = new Random();
            while (running) {
                synchronized (buffer) {
                    if (buffer.size() < bufferSize) {
                        int itemId = rand.nextInt(900) + 100; // 100~999
                        Item item = new Item(LocalDateTime.now(), itemId);
                        buffer.add(item);
                        updateBufferList();
                    }
                }
                try {
                    Thread.sleep(1500); // 1.5秒 (原本0.2秒)
                } catch (InterruptedException ignored) {}
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            while (running) {
                Item item = null;
                synchronized (buffer) {
                    if (!buffer.isEmpty()) {
                        item = buffer.poll(); // 取出最小編號的物品
                        updateBufferList();
                    }
                }
                if (item != null) {
                    // 處理物品, 處理速度約1.2倍生產速度
                    try {
                        Thread.sleep(1800); // 1.8秒 (原本0.24秒)
                    } catch (InterruptedException ignored) {}
                    System.out.println("消費者處理: " + item);
                } else {
                    // buffer空時短暫等待
                    try {
                        Thread.sleep(200); // 0.2秒 (原本0.05秒)
                    } catch (InterruptedException ignored) {}
                }
            }
        });

        producer.start();
        consumer.start();
    }

    private void updateBufferList() {
        SwingUtilities.invokeLater(() -> {
            bufferListModel.clear();
            List<Item> itemList = new ArrayList<>();
            for (Item item : buffer) {
                bufferListModel.addElement(item.toString());
                itemList.add(item);
            }
            animationPanel.update(itemList);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProducerConsumerMinHeap().setVisible(true));
    }

    // Item class with Comparable for min heap
    private static class Item implements Comparable<Item> {
        private final LocalDateTime created;
        private final int id;

        public Item(LocalDateTime created, int id) {
            this.created = created;
            this.id = id;
        }

        @Override
        public int compareTo(Item other) {
            return Integer.compare(this.id, other.id);
        }

        @Override
        public String toString() {
            return String.format("編號:%3d 時間:%s",
                    id,
                    created.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"))
            );
        }
    }

    // Animation panel for visualizing the heap
    private class HeapAnimationPanel extends JPanel {
        private final List<Item> items = new ArrayList<>();

        public HeapAnimationPanel() {
            setPreferredSize(new Dimension(350, 200));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder("Min Heap 動畫視覺化"));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (items.isEmpty()) {
                g2d.setColor(Color.GRAY);
                g2d.drawString("堆為空", getWidth() / 2 - 20, getHeight() / 2);
                return;
            }

            drawHeap(g2d);
        }

        private void drawHeap(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight() - 30; // 留出標題空間

            // 計算層數
            int levels = (int) Math.ceil(Math.log(items.size() + 1) / Math.log(2));
            int levelHeight = height / Math.max(levels, 1);

            // 繪製每個節點
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);

                // 計算節點位置
                int level = (int) (Math.log(i + 1) / Math.log(2));
                int positionInLevel = i - (int) Math.pow(2, level) + 1;
                int maxNodesInLevel = (int) Math.pow(2, level);

                int x = (positionInLevel + 1) * width / (maxNodesInLevel + 1);
                int y = 40 + level * levelHeight;

                // 繪製連接線到父節點
                if (i > 0) {
                    int parentIndex = (i - 1) / 2;
                    int parentLevel = (int) (Math.log(parentIndex + 1) / Math.log(2));
                    int parentPosInLevel = parentIndex - (int) Math.pow(2, parentLevel) + 1;
                    int parentMaxNodes = (int) Math.pow(2, parentLevel);

                    int parentX = (parentPosInLevel + 1) * width / (parentMaxNodes + 1);
                    int parentY = 40 + parentLevel * levelHeight;

                    g2d.setColor(Color.GRAY);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(x, y, parentX, parentY);
                }

                // 繪製節點
                g2d.setColor(i == 0 ? Color.RED : Color.BLUE); // 根節點用紅色
                g2d.fillOval(x - 20, y - 20, 40, 40);

                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - 20, y - 20, 40, 40);

                // 繪製節點值
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String text = String.valueOf(item.id);
                int textX = x - fm.stringWidth(text) / 2;
                int textY = y + fm.getAscent() / 2 - 2;
                g2d.drawString(text, textX, textY);
            }

            // 顯示堆的統計信息
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            g2d.drawString("節點數: " + items.size(), 10, height + 25);
            if (!items.isEmpty()) {
                g2d.drawString("最小值: " + items.get(0).id, 100, height + 25);
            }
        }

        public void update(List<Item> newItems) {
            items.clear();
            items.addAll(newItems);
            repaint();
        }
    }
}