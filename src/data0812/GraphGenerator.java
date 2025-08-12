package data0812;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GraphGenerator extends JFrame {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 500;
    private static final int NODE_RADIUS = 15;

    private JPanel graphPanel;
    private JTable adjacencyTable;
    private JTextArea resultArea;
    private JSpinner nodeSpinner, edgeSpinner;
    private JTextField sourceField, targetField;

    private List<Point> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();
    private int[][] adjacencyMatrix;
    private int nodeCount = 0;
    private int selectedSource = -1;
    private List<Integer> shortestPath = new ArrayList<>();

    private class Edge {
        int from, to;
        double weight;

        Edge(int from, int to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    public GraphGenerator() {
        setTitle("圖形自動產生程式");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        layoutComponents();
        addEventListeners();

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        // 控制面板
        nodeSpinner = new JSpinner(new SpinnerNumberModel(5, 3, 20, 1));
        edgeSpinner = new JSpinner(new SpinnerNumberModel(7, 0, 50, 1));
        sourceField = new JTextField(5);
        targetField = new JTextField(5);

        // 圖形顯示面板
        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGraph(g);
            }
        };
        graphPanel.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setBorder(BorderFactory.createTitledBorder("圖形顯示"));

        // 相鄰矩陣表格
        adjacencyTable = new JTable();
        adjacencyTable.setPreferredScrollableViewportSize(new Dimension(300, 200));

        // 結果顯示區域
        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private void layoutComponents() {
        // 頂部控制面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("節點數:"));
        controlPanel.add(nodeSpinner);
        controlPanel.add(new JLabel("邊數:"));
        controlPanel.add(edgeSpinner);
        controlPanel.add(new JButton("產生圖形") {{ addActionListener(e -> generateGraph()); }});

        // 路徑查找面板
        JPanel pathPanel = new JPanel(new FlowLayout());
        pathPanel.add(new JLabel("起點:"));
        pathPanel.add(sourceField);
        pathPanel.add(new JLabel("終點:"));
        pathPanel.add(targetField);
        pathPanel.add(new JButton("找最短路徑") {{ addActionListener(e -> findShortestPath()); }});
        pathPanel.add(new JButton("計算所有距離") {{ addActionListener(e -> calculateAllDistances()); }});

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(controlPanel);
        topPanel.add(pathPanel);

        // 中間面板
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(graphPanel, BorderLayout.CENTER);

        // 右側面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("相鄰矩陣", JLabel.CENTER), BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(adjacencyTable), BorderLayout.CENTER);

        // 底部結果面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("結果顯示", JLabel.CENTER), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        graphPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedNode = getNodeAtPoint(e.getPoint());
                if (clickedNode != -1) {
                    selectedSource = clickedNode;
                    sourceField.setText(String.valueOf(clickedNode));
                    resultArea.setText("選擇了節點: " + clickedNode + "\n");
                    graphPanel.repaint();
                }
            }
        });
    }

    private void generateGraph() {
        nodeCount = (Integer) nodeSpinner.getValue();
        int edgeCount = (Integer) edgeSpinner.getValue();

        nodes.clear();
        edges.clear();
        selectedSource = -1;
        shortestPath.clear();

        // 隨機產生節點位置
        Random random = new Random();
        for (int i = 0; i < nodeCount; i++) {
            int x, y;
            boolean validPosition;
            do {
                x = NODE_RADIUS + random.nextInt(PANEL_WIDTH - 2 * NODE_RADIUS);
                y = NODE_RADIUS + random.nextInt(PANEL_HEIGHT - 2 * NODE_RADIUS);
                Point newPoint = new Point(x, y);

                validPosition = true;
                for (Point existingPoint : nodes) {
                    if (newPoint.distance(existingPoint) < NODE_RADIUS * 3) {
                        validPosition = false;
                        break;
                    }
                }
            } while (!validPosition);

            nodes.add(new Point(x, y));
        }

        // 初始化相鄰矩陣
        adjacencyMatrix = new int[nodeCount][nodeCount];

        // 隨機產生邊
        Set<String> edgeSet = new HashSet<>();
        for (int i = 0; i < edgeCount && edgeSet.size() < edgeCount; i++) {
            int from = random.nextInt(nodeCount);
            int to = random.nextInt(nodeCount);

            if (from != to) {
                String edgeKey = Math.min(from, to) + "-" + Math.max(from, to);
                if (!edgeSet.contains(edgeKey)) {
                    edgeSet.add(edgeKey);

                    Point p1 = nodes.get(from);
                    Point p2 = nodes.get(to);
                    double weight = p1.distance(p2);

                    edges.add(new Edge(from, to, weight));
                    adjacencyMatrix[from][to] = (int) Math.round(weight);
                    adjacencyMatrix[to][from] = (int) Math.round(weight);
                }
            }
        }

        updateAdjacencyTable();
        graphPanel.repaint();
        resultArea.setText("已產生圖形: " + nodeCount + " 個節點, " + edges.size() + " 條邊\n");
    }

    private void updateAdjacencyTable() {
        String[] columnNames = new String[nodeCount + 1];
        columnNames[0] = "";
        for (int i = 0; i < nodeCount; i++) {
            columnNames[i + 1] = String.valueOf(i);
        }

        Object[][] data = new Object[nodeCount][nodeCount + 1];
        for (int i = 0; i < nodeCount; i++) {
            data[i][0] = String.valueOf(i);
            for (int j = 0; j < nodeCount; j++) {
                data[i][j + 1] = adjacencyMatrix[i][j];
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        adjacencyTable.setModel(model);
        adjacencyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    }

    private void drawGraph(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 繪製邊
        g2d.setStroke(new BasicStroke(2));
        for (Edge edge : edges) {
            Point p1 = nodes.get(edge.from);
            Point p2 = nodes.get(edge.to);

            // 檢查是否在最短路徑中
            boolean inPath = false;
            for (int i = 0; i < shortestPath.size() - 1; i++) {
                if ((shortestPath.get(i) == edge.from && shortestPath.get(i + 1) == edge.to) ||
                        (shortestPath.get(i) == edge.to && shortestPath.get(i + 1) == edge.from)) {
                    inPath = true;
                    break;
                }
            }

            g2d.setColor(inPath ? Color.RED : Color.BLACK);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

            // 繪製權重
            int midX = (p1.x + p2.x) / 2;
            int midY = (p1.y + p2.y) / 2;
            g2d.setColor(Color.BLUE);
            g2d.drawString(String.valueOf((int) Math.round(edge.weight)), midX, midY);
        }

        // 繪製節點
        for (int i = 0; i < nodes.size(); i++) {
            Point p = nodes.get(i);

            if (i == selectedSource) {
                g2d.setColor(Color.GREEN);
            } else if (shortestPath.contains(i)) {
                g2d.setColor(Color.ORANGE);
            } else {
                g2d.setColor(Color.LIGHT_GRAY);
            }

            g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // 繪製節點編號
            FontMetrics fm = g2d.getFontMetrics();
            String label = String.valueOf(i);
            int textX = p.x - fm.stringWidth(label) / 2;
            int textY = p.y + fm.getAscent() / 2;
            g2d.drawString(label, textX, textY);
        }
    }

    private int getNodeAtPoint(Point clickPoint) {
        for (int i = 0; i < nodes.size(); i++) {
            Point nodePoint = nodes.get(i);
            if (clickPoint.distance(nodePoint) <= NODE_RADIUS) {
                return i;
            }
        }
        return -1;
    }

    private void findShortestPath() {
        try {
            int source = Integer.parseInt(sourceField.getText().trim());
            int target = Integer.parseInt(targetField.getText().trim());

            if (source < 0 || source >= nodeCount || target < 0 || target >= nodeCount) {
                resultArea.setText("錯誤: 節點編號必須在 0 到 " + (nodeCount - 1) + " 之間\n");
                return;
            }

            // 使用 Dijkstra 算法
            int[] distances = new int[nodeCount];
            int[] previous = new int[nodeCount];
            boolean[] visited = new boolean[nodeCount];

            Arrays.fill(distances, Integer.MAX_VALUE);
            Arrays.fill(previous, -1);
            distances[source] = 0;

            PriorityQueue<Integer> queue = new PriorityQueue<>((a, b) -> distances[a] - distances[b]);
            queue.offer(source);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                if (visited[current]) continue;
                visited[current] = true;

                for (int neighbor = 0; neighbor < nodeCount; neighbor++) {
                    if (adjacencyMatrix[current][neighbor] > 0 && !visited[neighbor]) {
                        int newDistance = distances[current] + adjacencyMatrix[current][neighbor];
                        if (newDistance < distances[neighbor]) {
                            distances[neighbor] = newDistance;
                            previous[neighbor] = current;
                            queue.offer(neighbor);
                        }
                    }
                }
            }

            // 重建路徑
            shortestPath.clear();
            if (distances[target] != Integer.MAX_VALUE) {
                List<Integer> path = new ArrayList<>();
                int current = target;
                while (current != -1) {
                    path.add(current);
                    current = previous[current];
                }
                Collections.reverse(path);
                shortestPath = path;

                StringBuilder sb = new StringBuilder();
                sb.append("從節點 ").append(source).append(" 到節點 ").append(target).append(" 的最短路徑:\n");
                sb.append("路徑: ");
                for (int i = 0; i < path.size(); i++) {
                    sb.append(path.get(i));
                    if (i < path.size() - 1) sb.append(" -> ");
                }
                sb.append("\n距離: ").append(distances[target]).append("\n");
                resultArea.setText(sb.toString());
            } else {
                resultArea.setText("從節點 " + source + " 到節點 " + target + " 沒有路徑\n");
                shortestPath.clear();
            }

            graphPanel.repaint();

        } catch (NumberFormatException e) {
            resultArea.setText("錯誤: 請輸入有效的節點編號\n");
        }
    }

    private void calculateAllDistances() {
        if (nodeCount == 0) {
            resultArea.setText("請先產生圖形\n");
            return;
        }

        // 使用 Floyd-Warshall 算法計算所有點對之間的最短距離
        int[][] distances = new int[nodeCount][nodeCount];

        // 初始化
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else if (adjacencyMatrix[i][j] > 0) {
                    distances[i][j] = adjacencyMatrix[i][j];
                } else {
                    distances[i][j] = Integer.MAX_VALUE / 2; // 避免溢出
                }
            }
        }

        // Floyd-Warshall
        for (int k = 0; k < nodeCount; k++) {
            for (int i = 0; i < nodeCount; i++) {
                for (int j = 0; j < nodeCount; j++) {
                    if (distances[i][k] + distances[k][j] < distances[i][j]) {
                        distances[i][j] = distances[i][k] + distances[k][j];
                    }
                }
            }
        }

        // 顯示結果
        StringBuilder sb = new StringBuilder();
        sb.append("所有節點對之間的最短距離:\n");
        sb.append("       ");
        for (int j = 0; j < nodeCount; j++) {
            sb.append(String.format("%6d", j));
        }
        sb.append("\n");

        for (int i = 0; i < nodeCount; i++) {
            sb.append(String.format("%6d ", i));
            for (int j = 0; j < nodeCount; j++) {
                if (distances[i][j] >= Integer.MAX_VALUE / 2) {
                    sb.append("   ∞  ");
                } else {
                    sb.append(String.format("%6d", distances[i][j]));
                }
            }
            sb.append("\n");
        }

        resultArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    // 可選：顯示錯誤提示或忽略
                }
            }
            new GraphGenerator();
        });
    }
}