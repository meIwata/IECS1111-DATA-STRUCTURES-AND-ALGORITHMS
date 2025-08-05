package data0805;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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
    private java.util.List<GraphNode> nodes = new ArrayList<>();
    private java.util.List<GraphEdge> edges = new ArrayList<>();
    private JPanel graphPanel;
    private JTextField nodeNameField, fromField, toField;
    private Random rand = new Random();

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
                    g.setColor(Color.RED);
                    g.fillOval(node.x - 25, node.y - 25, 50, 50);
                    g.setColor(Color.BLACK);
                    g.drawString(node.name, node.x - 7, node.y + 5);
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

        controlPanel.add(new JLabel("Node Name:"));
        controlPanel.add(nodeNameField);
        controlPanel.add(addNodeBtn);
        controlPanel.add(removeNodeBtn);
        controlPanel.add(new JLabel("From:"));
        controlPanel.add(fromField);
        controlPanel.add(new JLabel("To:"));
        controlPanel.add(toField);
        controlPanel.add(addEdgeBtn);
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
    }

    private GraphNode getNodeByName(String name) {
        for (GraphNode node : nodes) {
            if (node.name.equals(name)) return node;
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GraphApplicationGUI().setVisible(true);
        });
    }
}

