package data0805;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphGeneratorGUI extends JFrame {
    private JTextField verticesField;
    private JTextField edgesField;
    private JButton startButton;
    private JButton mstButton;
    private GraphPanel graphPanel;
    private JTextArea edgeInfoArea;

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<Edge> mstEdges;
    private final Random random;
    private boolean showMST = false;

    public GraphGeneratorGUI() {
        setTitle("Graph Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        random = new Random();
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        mstEdges = new ArrayList<>();

        initializeComponents();
        layoutComponents();
        addEventListeners();

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        // 頂部控制面板
        verticesField = new JTextField("5", 10);
        edgesField = new JTextField("8", 10);
        startButton = new JButton("Start");
        mstButton = new JButton("Find MST");
        mstButton.setEnabled(false);

        // 圖形繪製面板
        graphPanel = new GraphPanel();
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setBorder(BorderFactory.createLoweredBevelBorder());

        // 邊信息顯示區域
        edgeInfoArea = new JTextArea(5, 0);
        edgeInfoArea.setEditable(false);
        edgeInfoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
    }

    private void layoutComponents() {
        // 頂部面板
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Vertices:"));
        topPanel.add(verticesField);
        topPanel.add(new JLabel("Edges:"));
        topPanel.add(edgesField);
        topPanel.add(startButton);
        topPanel.add(mstButton);

        // 底部滾動面板
        JScrollPane scrollPane = new JScrollPane(edgeInfoArea);
        scrollPane.setPreferredSize(new Dimension(0, 120));

        add(topPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void addEventListeners() {
        startButton.addActionListener(e -> generateGraph());
        mstButton.addActionListener(e -> findMST());
    }

    private void generateGraph() {
        try {
            int numVertices = Integer.parseInt(verticesField.getText());
            int numEdges = Integer.parseInt(edgesField.getText());

            if (numVertices < 1 || numEdges < 0) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數值！");
                return;
            }

            int maxEdges = numVertices * (numVertices - 1) / 2;
            if (numEdges > maxEdges) {
                JOptionPane.showMessageDialog(this,
                    "邊的數量不能超過 " + maxEdges + " (對於 " + numVertices + " 個頂點)");
                return;
            }

            createVertices(numVertices);
            createEdges(numEdges);
            showMST = false;
            mstEdges.clear();
            mstButton.setEnabled(vertices.size() > 1 && !edges.isEmpty());
            updateEdgeInfo();
            graphPanel.repaint();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的數字！");
        }
    }

    private void findMST() {
        if (vertices.isEmpty() || edges.isEmpty()) {
            return;
        }

        mstEdges.clear();

        // 使用 Kruskal 算法找到最小生成樹
        List<Edge> sortedEdges = new ArrayList<>(edges);
        sortedEdges.sort(Comparator.comparingInt(e -> e.cost));

        UnionFind uf = new UnionFind(vertices.size());

        for (Edge edge : sortedEdges) {
            int v1Index = vertices.indexOf(edge.vertex1);
            int v2Index = vertices.indexOf(edge.vertex2);

            if (uf.union(v1Index, v2Index)) {
                mstEdges.add(edge);
                if (mstEdges.size() == vertices.size() - 1) {
                    break;
                }
            }
        }

        showMST = true;
        updateEdgeInfo();
        graphPanel.repaint();
    }

    private void createVertices(int numVertices) {
        vertices.clear();
        int panelWidth = graphPanel.getWidth() - 100;
        int panelHeight = graphPanel.getHeight() - 100;

        if (panelWidth <= 0) panelWidth = 600;
        if (panelHeight <= 0) panelHeight = 400;

        for (int i = 0; i < numVertices; i++) {
            double angle = 2 * Math.PI * i / numVertices;
            int centerX = panelWidth / 2 + 50;
            int centerY = panelHeight / 2 + 50;
            int radius = Math.min(panelWidth, panelHeight) / 3;

            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            vertices.add(new Vertex("V" + i, x, y));
        }
    }

    private void createEdges(int numEdges) {
        edges.clear();
        Set<String> edgeSet = new HashSet<>();

        while (edges.size() < numEdges && edges.size() < vertices.size() * (vertices.size() - 1) / 2) {
            int v1 = random.nextInt(vertices.size());
            int v2 = random.nextInt(vertices.size());

            if (v1 != v2) {
                String edgeKey = Math.min(v1, v2) + "-" + Math.max(v1, v2);
                if (!edgeSet.contains(edgeKey)) {
                    edgeSet.add(edgeKey);
                    int cost = random.nextInt(99) + 1;
                    edges.add(new Edge(vertices.get(v1), vertices.get(v2), cost));
                }
            }
        }
    }

    private void updateEdgeInfo() {
        StringBuilder sb = new StringBuilder();

        if (showMST && !mstEdges.isEmpty()) {
            sb.append("=== MINIMUM SPANNING TREE ===\n");
            int totalCost = 0;
            for (Edge edge : mstEdges) {
                sb.append("MST Edge: ").append(edge.vertex1.name)
                  .append(" - ").append(edge.vertex2.name)
                  .append(" with cost: ").append(edge.cost).append("\n");
                totalCost += edge.cost;
            }
            sb.append("Total MST Cost: ").append(totalCost).append("\n\n");
            sb.append("=== ALL EDGES ===\n");
        }

        for (Edge edge : edges) {
            sb.append("Edge between ").append(edge.vertex1.name)
              .append(" and ").append(edge.vertex2.name)
              .append(" with cost: ").append(edge.cost).append("\n");
        }
        edgeInfoArea.setText(sb.toString());
    }

    // 內部類：頂點
    private static class Vertex {
        String name;
        int x, y;

        Vertex(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }
    }

    // 內部類：邊
    private static class Edge {
        Vertex vertex1, vertex2;
        int cost;

        Edge(Vertex vertex1, Vertex vertex2, int cost) {
            this.vertex1 = vertex1;
            this.vertex2 = vertex2;
            this.cost = cost;
        }
    }

    // Union-Find 數據結構用於 Kruskal 算法
    private static class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int size) {
            parent = new int[size];
            rank = new int[size];
            for (int i = 0; i < size; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false;
            }

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            return true;
        }
    }

    // 內部類：圖形繪製面板
    private class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 繪製所有邊（如果不顯示MST）或非MST邊（如果顯示MST）
            if (!showMST) {
                // 繪製所有邊
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1.5f));
                for (Edge edge : edges) {
                    drawEdge(g2d, edge, Color.BLACK, Color.RED);
                }
            } else {
                // 先繪製非MST邊（灰色）
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.setStroke(new BasicStroke(1.0f));
                for (Edge edge : edges) {
                    if (!mstEdges.contains(edge)) {
                        drawEdge(g2d, edge, Color.LIGHT_GRAY, Color.GRAY);
                    }
                }

                // 再繪製MST邊（紅色，較粗）
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(3.0f));
                for (Edge edge : mstEdges) {
                    drawEdge(g2d, edge, Color.RED, Color.BLUE);
                }
            }

            // 繪製頂點
            for (Vertex vertex : vertices) {
                // 繪製頂點圓圈
                g2d.setColor(Color.BLACK);
                g2d.fillOval(vertex.x - 8, vertex.y - 8, 16, 16);

                // 繪製頂點標籤
                g2d.setColor(Color.BLUE);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                g2d.drawString(vertex.name, vertex.x + 12, vertex.y + 5);
            }
        }

        private void drawEdge(Graphics2D g2d, Edge edge, Color lineColor, Color textColor) {
            // 繪製邊線
            g2d.setColor(lineColor);
            g2d.drawLine(edge.vertex1.x, edge.vertex1.y, edge.vertex2.x, edge.vertex2.y);

            // 繪製成本標籤
            int midX = (edge.vertex1.x + edge.vertex2.x) / 2;
            int midY = (edge.vertex1.y + edge.vertex2.y) / 2;

            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            String costStr = String.valueOf(edge.cost);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(costStr);
            int textHeight = fm.getHeight();

            // 繪製白色背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(midX - textWidth/2 - 2, midY - textHeight/2 - 2,
                       textWidth + 4, textHeight);

            // 繪製成本文字
            g2d.setColor(textColor);
            g2d.drawString(costStr, midX - textWidth/2, midY + textHeight/4);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GraphGeneratorGUI().setVisible(true);
        });
    }
}
