package data0805;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

class GraphNode {
    String name;
    int x, y;
    public GraphNode(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
}

class GraphEdge {
    GraphNode from, to;
    public GraphEdge(GraphNode from, GraphNode to) {
        this.from = from;
        this.to = to;
    }
}

public class GraphApplicationGUI extends JFrame {
    private final java.util.List<GraphNode> nodes = new ArrayList<>();
    private final java.util.List<GraphEdge> edges = new ArrayList<>();
    private final JPanel graphPanel;
    private final JTextField nodeNameField, fromField, toField;
    private final JTextField nodeCountField, edgeCountField;
    private final JTextField startNodeField;
    private final java.util.List<GraphNode> traversalResult = new ArrayList<>();
    private String traversalType = ""; // "DFS" or "BFS"
    private final Random rand = new Random();

    public GraphApplicationGUI() {
        setTitle("Graph Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw edges
                for (GraphEdge edge : edges) {
                    g.setColor(Color.BLACK);
                    g.drawLine(edge.from.x, edge.from.y, edge.to.x, edge.to.y);
                }
                // Draw nodes
                for (GraphNode node : nodes) {
                    if (!traversalResult.isEmpty() && traversalResult.get(0) == node) {
                        g.setColor(Color.RED); // 起點紅色
                    } else if (traversalResult.contains(node)) {
                        g.setColor(Color.GREEN); // 訪問過的綠色
                    } else {
                        g.setColor(Color.RED); // 未訪問紅色
                    }
                    g.fillOval(node.x - 25, node.y - 25, 50, 50);
                    g.setColor(Color.BLACK);
                    g.drawString(node.name, node.x - 13, node.y + 5);
                }
                // 顯示遍歷結果
                if (!traversalResult.isEmpty()) {
                    g.setColor(Color.BLACK);
                    StringBuilder sb = new StringBuilder();
                    sb.append(traversalType).append(": ");
                    for (GraphNode n : traversalResult) sb.append(n.name).append(" ");
                    g.drawString(sb.toString(), 20, 20);
                }
            }
        };
        graphPanel.setBackground(Color.WHITE);
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        nodeNameField = new JTextField(5);
        JButton addNodeBtn = new JButton("Add Node");
        JButton removeNodeBtn = new JButton("Remove Node");
        fromField = new JTextField(3);
        toField = new JTextField(3);
        JButton addEdgeBtn = new JButton("Add Edge");
        nodeCountField = new JTextField("6", 3);
        edgeCountField = new JTextField("4", 3);
        JButton autoGenBtn = new JButton("Auto Generate Graph");
        startNodeField = new JTextField(5);
        JButton dfsBtn = new JButton("DFS");
        JButton bfsBtn = new JButton("BFS");

        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.gridy = 0;
        gbc.gridx = 0;
        controlPanel.add(new JLabel("Node Name:"), gbc);
        gbc.gridx++;
        controlPanel.add(nodeNameField, gbc);
        gbc.gridx++;
        controlPanel.add(addNodeBtn, gbc);
        gbc.gridx++;
        controlPanel.add(removeNodeBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        controlPanel.add(new JLabel("From:"), gbc);
        gbc.gridx++;
        controlPanel.add(fromField, gbc);
        gbc.gridx++;
        controlPanel.add(new JLabel("To:"), gbc);
        gbc.gridx++;
        controlPanel.add(toField, gbc);
        gbc.gridx++;
        controlPanel.add(addEdgeBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        controlPanel.add(new JLabel("Number of Nodes:"), gbc);
        gbc.gridx++;
        controlPanel.add(nodeCountField, gbc);
        gbc.gridx++;
        controlPanel.add(new JLabel("Number of Edges:"), gbc);
        gbc.gridx++;
        controlPanel.add(edgeCountField, gbc);
        gbc.gridx++;
        controlPanel.add(autoGenBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        controlPanel.add(new JLabel("Start Node:"), gbc);
        gbc.gridx++;
        controlPanel.add(startNodeField, gbc);
        gbc.gridx++;
        controlPanel.add(dfsBtn, gbc);
        gbc.gridx++;
        controlPanel.add(bfsBtn, gbc);
        add(controlPanel, BorderLayout.SOUTH);

        addNodeBtn.addActionListener(e -> {
            String name = nodeNameField.getText().trim();
            if (!name.isEmpty() && getNodeByName(name) == null) {
                int x = 50 + rand.nextInt(graphPanel.getWidth() - 100);
                int y = 50 + rand.nextInt(graphPanel.getHeight() - 100);
                nodes.add(new GraphNode(name, x, y));
                graphPanel.repaint();
            }
        });

        removeNodeBtn.addActionListener(e -> {
            String name = nodeNameField.getText().trim();
            GraphNode node = getNodeByName(name);
            if (node != null) {
                nodes.remove(node);
                edges.removeIf(edge -> edge.from == node || edge.to == node);
                graphPanel.repaint();
            }
        });

        addEdgeBtn.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            GraphNode fromNode = getNodeByName(from);
            GraphNode toNode = getNodeByName(to);
            if (fromNode != null && toNode != null && fromNode != toNode) {
                edges.add(new GraphEdge(fromNode, toNode));
                graphPanel.repaint();
            }
        });

        autoGenBtn.addActionListener(e -> {
            int n, m;
            try {
                n = Integer.parseInt(nodeCountField.getText().trim());
                m = Integer.parseInt(edgeCountField.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid node/edge count");
                return;
            }
            if (n < 1 || m < 0) {
                JOptionPane.showMessageDialog(this, "Node/edge count must be positive");
                return;
            }
            nodes.clear();
            edges.clear();
            int w = graphPanel.getWidth();
            int h = graphPanel.getHeight();
            for (int i = 0; i < n; i++) {
                String name = "N" + i;
                int x = 50 + rand.nextInt(Math.max(1, w - 100));
                int y = 50 + rand.nextInt(Math.max(1, h - 100));
                nodes.add(new GraphNode(name, x, y));
            }
            Set<String> edgeSet = new HashSet<>();
            for (int i = 0; i < m; i++) {
                int a = rand.nextInt(n);
                int b = rand.nextInt(n);
                if (a == b) { i--; continue; }
                String key = a < b ? a + "," + b : b + "," + a;
                if (edgeSet.contains(key)) { i--; continue; }
                edgeSet.add(key);
                edges.add(new GraphEdge(nodes.get(a), nodes.get(b)));
            }
            graphPanel.repaint();
        });
        dfsBtn.addActionListener(e -> {
            String start = startNodeField.getText().trim();
            GraphNode startNode = getNodeByName(start);
            traversalResult.clear();
            traversalType = "DFS";
            if (startNode != null) {
                Set<GraphNode> visited = new LinkedHashSet<>();
                dfs(startNode, visited);
                traversalResult.addAll(visited);
            }
            graphPanel.repaint();
        });
        bfsBtn.addActionListener(e -> {
            String start = startNodeField.getText().trim();
            GraphNode startNode = getNodeByName(start);
            traversalResult.clear();
            traversalType = "BFS";
            if (startNode != null) {
                Set<GraphNode> visited = new LinkedHashSet<>();
                Queue<GraphNode> queue = new LinkedList<>();
                queue.add(startNode);
                visited.add(startNode);
                while (!queue.isEmpty()) {
                    GraphNode curr = queue.poll();
                    for (GraphNode neighbor : getNeighbors(curr)) {
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                traversalResult.addAll(visited);
            }
            graphPanel.repaint();
        });
    }

    private GraphNode getNodeByName(String name) {
        for (GraphNode node : nodes) {
            if (node.name.equals(name)) return node;
        }
        return null;
    }
    private void dfs(GraphNode node, Set<GraphNode> visited) {
        visited.add(node);
        for (GraphNode neighbor : getNeighbors(node)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited);
            }
        }
    }
    private List<GraphNode> getNeighbors(GraphNode node) {
        List<GraphNode> neighbors = new ArrayList<>();
        for (GraphEdge edge : edges) {
            if (edge.from == node && !neighbors.contains(edge.to)) neighbors.add(edge.to);
            if (edge.to == node && !neighbors.contains(edge.from)) neighbors.add(edge.from);
        }
        return neighbors;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GraphApplicationGUI().setVisible(true));
    }
}
