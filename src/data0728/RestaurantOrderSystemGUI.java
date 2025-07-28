package data0728;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 餐廳訂單處理系統
 * 實現顧客產生 → 訂單加入佇列 → 工作人員處理 → 餐點完成的流程
 */
public class RestaurantOrderSystemGUI extends JFrame {

    // 餐點類型枚舉
    enum FoodType {
        BURGER("漢堡", 2000),    // 2秒
        FRIES("薯條", 1000),     // 1秒
        DRINK("飲料", 500);      // 0.5秒

        private final String name;
        private final int cookingTime; // 毫秒

        FoodType(String name, int cookingTime) {
            this.name = name;
            this.cookingTime = cookingTime;
        }

        public String getName() { return name; }
        public int getCookingTime() { return cookingTime; }
    }

    // 訂單類別
    static class Order {
        private static final AtomicInteger orderCounter = new AtomicInteger(1);
        private final int orderId;
        private final FoodType foodType;
        private final int tableNumber;
        private final String orderTime;
        private String status;
        private String workerName;

        public Order(FoodType foodType, int tableNumber) {
            this.orderId = orderCounter.getAndIncrement();
            this.foodType = foodType;
            this.tableNumber = tableNumber;
            this.orderTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            this.status = "等待中";
            this.workerName = "";
        }

        // 重置訂單計數器的靜態方法
        public static void resetOrderCounter() {
            orderCounter.set(1);
        }

        // Getters and setters
        public int getOrderId() { return orderId; }
        public FoodType getFoodType() { return foodType; }
        public int getTableNumber() { return tableNumber; }
        public String getOrderTime() { return orderTime; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getWorkerName() { return workerName; }
        public void setWorkerName(String workerName) { this.workerName = workerName; }
    }

    // GUI組件
    private JSpinner speedSpinner;
    private JButton startButton, stopButton;
    private JTable queueTable, processingTable, completedTable;
    private DefaultTableModel queueModel, processingModel, completedModel;
    private JLabel customerCountLabel, totalOrdersLabel;

    // 系統組件
    private final BlockingQueue<Order> orderQueue = new LinkedBlockingQueue<>();
    private boolean systemRunning = false;
    private final AtomicInteger customerCount = new AtomicInteger(0);
    private final AtomicInteger totalOrders = new AtomicInteger(0);
    private final Random random = new Random();

    // 線程
    private Thread customerGeneratorThread;
    private Thread[] workerThreads;

    public RestaurantOrderSystemGUI() {
        initializeGUI();
        setupWorkers();
    }

    private void initializeGUI() {
        setTitle("餐廳訂單處理系統");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 控制面板
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        // 主要內容面板
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // 狀態面板
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        panel.add(new JLabel("顧客產生速度 (秒):"));
        speedSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 5.0, 0.1));
        panel.add(speedSpinner);

        startButton = new JButton("開始營業");
        startButton.addActionListener(e -> startSystem());
        panel.add(startButton);

        stopButton = new JButton("結束營業");
        stopButton.addActionListener(e -> stopSystem());
        stopButton.setEnabled(false);
        panel.add(stopButton);

        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 5, 5));

        // 訂單佇列
        queueModel = new DefaultTableModel(new String[]{"訂單編號", "餐點", "桌號", "訂單時間", "狀態"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        queueTable = new JTable(queueModel);
        JScrollPane queueScroll = new JScrollPane(queueTable);
        queueScroll.setBorder(BorderFactory.createTitledBorder("訂單佇列"));
        panel.add(queueScroll);

        // 處理中訂單
        processingModel = new DefaultTableModel(new String[]{"訂單編號", "餐點", "桌號", "工作人員", "狀態"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        processingTable = new JTable(processingModel);
        JScrollPane processingScroll = new JScrollPane(processingTable);
        processingScroll.setBorder(BorderFactory.createTitledBorder("處理中"));
        panel.add(processingScroll);

        // 完成訂單
        completedModel = new DefaultTableModel(new String[]{"訂單編號", "餐點", "桌號", "完成時間"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        completedTable = new JTable(completedModel);
        JScrollPane completedScroll = new JScrollPane(completedTable);
        completedScroll.setBorder(BorderFactory.createTitledBorder("已完成"));
        panel.add(completedScroll);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        customerCountLabel = new JLabel("顧客總數: 0");
        totalOrdersLabel = new JLabel("訂單總數: 0");

        panel.add(customerCountLabel);
        panel.add(new JLabel(" | "));
        panel.add(totalOrdersLabel);

        return panel;
    }

    private void setupWorkers() {
        // 創建2-3個工作人員線程
        workerThreads = new Thread[3];
        for (int i = 0; i < workerThreads.length; i++) {
            final String workerName = "工作人員-" + (i + 1);
            workerThreads[i] = new Thread(() -> workerTask(workerName));
        }
    }

    private void startSystem() {
        systemRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        // 重新設置工作人員線程（解決重啟問題）
        setupWorkers();

        // 啟動顧客產生線程
        customerGeneratorThread = new Thread(this::customerGenerator);
        customerGeneratorThread.start();

        // 啟動工作人員線程
        for (Thread worker : workerThreads) {
            worker.start();
        }
    }

    private void stopSystem() {
        systemRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        if (customerGeneratorThread != null) {
            customerGeneratorThread.interrupt();
        }

        // 中斷所有工作人員線程
        for (Thread worker : workerThreads) {
            if (worker != null) {
                worker.interrupt();
            }
        }

        // 清空佇列和表格
        orderQueue.clear();
        SwingUtilities.invokeLater(() -> {
            queueModel.setRowCount(0);
            processingModel.setRowCount(0);
            completedModel.setRowCount(0);
            customerCountLabel.setText("顧客總數: 0");
            totalOrdersLabel.setText("訂單總數: 0");
        });

        customerCount.set(0);
        totalOrders.set(0);
        Order.resetOrderCounter(); // 重置訂單編號計數器
    }

    private void customerGenerator() {
        while (systemRunning && !Thread.currentThread().isInterrupted()) {
            try {
                // 產生新顧客和訂單
                generateCustomerOrder();

                // 隨機等待0.5-2秒（根據速度調整）
                double speed = (Double) speedSpinner.getValue();
                int waitTime = (int) (speed * 1000);
                int randomWait = random.nextInt(waitTime) + (waitTime / 2); // 0.5x到1.5x的範圍
                Thread.sleep(randomWait);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void generateCustomerOrder() {
        // 隨機選擇餐點類型
        FoodType[] foods = FoodType.values();
        FoodType selectedFood = foods[random.nextInt(foods.length)];

        // 隨機選擇桌號 (1-20)
        int tableNumber = random.nextInt(20) + 1;

        // 創建訂單
        Order order = new Order(selectedFood, tableNumber);
        orderQueue.offer(order);

        // 更新統計
        customerCount.incrementAndGet();
        totalOrders.incrementAndGet();

        // 更新GUI
        SwingUtilities.invokeLater(() -> {
            queueModel.addRow(new Object[]{
                order.getOrderId(),
                order.getFoodType().getName(),
                order.getTableNumber(),
                order.getOrderTime(),
                order.getStatus()
            });

            customerCountLabel.setText("顧客總數: " + customerCount.get());
            totalOrdersLabel.setText("訂單總數: " + totalOrders.get());
        });
    }

    private void workerTask(String workerName) {
        while (systemRunning || !orderQueue.isEmpty()) {
            try {
                Order order = orderQueue.take(); // 阻塞式取出訂單
                processOrder(order, workerName);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processOrder(Order order, String workerName) {
        System.out.println("工作人員 " + workerName + " 開始處理訂單 " + order.getOrderId());

        try {
            // 第一步：立即從佇列移除並加入處理中
            boolean queueRemoved = false;
            try {
                SwingUtilities.invokeAndWait(() -> {
                    // 先確保訂單確實存在於佇列中
                    if (isOrderInQueue(order.getOrderId())) {
                        removeOrderFromQueue(order.getOrderId());
                        System.out.println("訂單 " + order.getOrderId() + " 已從佇列移除");

                        // 立即加入處理中
                        order.setStatus("製作中");
                        order.setWorkerName(workerName);
                        processingModel.addRow(new Object[]{
                            order.getOrderId(),
                            order.getFoodType().getName(),
                            order.getTableNumber(),
                            workerName,
                            "製作中"
                        });
                        System.out.println("訂單 " + order.getOrderId() + " 已加入處理中");
                    } else {
                        System.out.println("警告：訂單 " + order.getOrderId() + " 不在佇列中，可能已被處理");
                    }
                });
                queueRemoved = true;
            } catch (Exception e) {
                System.err.println("移動到處理中時發生錯誤: " + e.getMessage());
                e.printStackTrace();
                return; // 如果第一步失敗，不繼續處理
            }

            // 第二步：模擬製作時間
            Thread.sleep(order.getFoodType().getCookingTime());

            // 第三步：完成製作並移動到已完成
            if (queueRemoved) {
                try {
                    SwingUtilities.invokeAndWait(() -> {
                        removeOrderFromProcessing(order.getOrderId());

                        order.setStatus("已完成");
                        String completedTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
                        completedModel.addRow(new Object[]{
                            order.getOrderId(),
                            order.getFoodType().getName(),
                            order.getTableNumber(),
                            completedTime
                        });
                        System.out.println("訂單 " + order.getOrderId() + " 已完成並移到已完成區域");
                    });
                } catch (Exception e) {
                    System.err.println("移動到已完成時發生錯誤: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("工作人員 " + workerName + " 被中斷");
        }
    }

    // 檢查訂單是否存在於佇列中
    private boolean isOrderInQueue(int orderId) {
        for (int i = 0; i < queueModel.getRowCount(); i++) {
            try {
                Integer currentOrderId = (Integer) queueModel.getValueAt(i, 0);
                if (currentOrderId != null && currentOrderId.equals(orderId)) {
                    return true;
                }
            } catch (Exception e) {
                System.err.println("檢查佇列時發生錯誤: " + e.getMessage());
            }
        }
        return false;
    }

    // 優化的輔助方法：從佇列表格中移除指定訂單
    private void removeOrderFromQueue(int orderId) {
        boolean found = false;
        for (int i = queueModel.getRowCount() - 1; i >= 0; i--) {
            try {
                Integer currentOrderId = (Integer) queueModel.getValueAt(i, 0);
                if (currentOrderId != null && currentOrderId.equals(orderId)) {
                    queueModel.removeRow(i);
                    found = true;
                    System.out.println("成功從佇列移除訂單 " + orderId + " (第 " + i + " 行)");
                    break;
                }
            } catch (Exception e) {
                System.err.println("移除佇列訂單時發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (!found) {
            System.out.println("警告：在佇列中找不到訂單 " + orderId);
        }
    }

    // 優化的輔助方法：從處理中表格移除指定訂單
    private void removeOrderFromProcessing(int orderId) {
        boolean found = false;
        for (int i = processingModel.getRowCount() - 1; i >= 0; i--) {
            try {
                Integer currentOrderId = (Integer) processingModel.getValueAt(i, 0);
                if (currentOrderId != null && currentOrderId.equals(orderId)) {
                    processingModel.removeRow(i);
                    found = true;
                    System.out.println("成功從處理中移除訂單 " + orderId + " (第 " + i + " 行)");
                    break;
                }
            } catch (Exception e) {
                System.err.println("移除處理中訂單時發生錯誤: " + e.getMessage());
                e.printStackTrace();
            }
        }
        if (!found) {
            System.out.println("警告：在處理中找不到訂單 " + orderId);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 使用預設的Swing外觀，不進行特殊設置以避免兼容性問題
            new RestaurantOrderSystemGUI().setVisible(true);
        });
    }
}
