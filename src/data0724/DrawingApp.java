package data0724;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.ArrayList;

public class DrawingApp extends JFrame {
    private DrawPanel drawPanel;
    private JButton undoButton, redoButton;
    private JComboBox<String> colorBox;
    private JComboBox<Integer> sizeBox;
    private Stack<DrawAction> undoStack = new Stack<>();
    private Stack<DrawAction> redoStack = new Stack<>();
    private final int MAX_HISTORY = 5;

    public DrawingApp() {
        setTitle("畫圖程式");
        setSize(660, 540);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        colorBox = new JComboBox<>(new String[]{"黑色", "紅色", "綠色", "藍色"});
        sizeBox = new JComboBox<Integer>(new Integer[]{2, 4, 8, 12, 16});
        undoButton = new JButton("Undo");
        redoButton = new JButton("Redo");
        controlPanel.add(new JLabel("顏色:"));
        controlPanel.add(colorBox);
        controlPanel.add(new JLabel("粗細:"));
        controlPanel.add(sizeBox);
        controlPanel.add(undoButton);
        controlPanel.add(redoButton);
        add(controlPanel, BorderLayout.NORTH);

        colorBox.addActionListener(e -> drawPanel.setColor(getSelectedColor()));
        sizeBox.addActionListener(e -> drawPanel.setStroke((int)sizeBox.getSelectedItem()));
        undoButton.addActionListener(e -> undo());
        redoButton.addActionListener(e -> redo());

        drawPanel.setColor(getSelectedColor());
        drawPanel.setStroke((int)sizeBox.getSelectedItem());
    }

    private Color getSelectedColor() {
        String c = (String) colorBox.getSelectedItem();
        switch (c) {
            case "紅色": return Color.RED;
            case "綠色": return Color.GREEN;
            case "藍色": return Color.BLUE;
            default: return Color.BLACK;
        }
    }

    private void undo() {
        if (!drawPanel.actions.isEmpty() && undoStack.size() > 0) {
            DrawAction last = drawPanel.actions.remove(drawPanel.actions.size() - 1);
            if (redoStack.size() == MAX_HISTORY) redoStack.remove(0);
            redoStack.push(last);
            undoStack.pop();
            drawPanel.repaint();
            undoButton.setEnabled(!undoStack.isEmpty());
            redoButton.setEnabled(true);
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            DrawAction action = redoStack.pop();
            drawPanel.actions.add(action);
            if (undoStack.size() == MAX_HISTORY) undoStack.remove(0);
            undoStack.push(action);
            drawPanel.repaint();
            undoButton.setEnabled(true);
            redoButton.setEnabled(!redoStack.isEmpty());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DrawingApp().setVisible(true));
    }

    class DrawAction {
        ArrayList<DrawPoint> path;
        Color color;
        int stroke;
        DrawAction(ArrayList<DrawPoint> path, Color color, int stroke) {
            this.path = path;
            this.color = color;
            this.stroke = stroke;
        }
    }

    class DrawPanel extends JPanel {
        ArrayList<DrawAction> actions = new ArrayList<>();
        ArrayList<DrawPoint> currentPath = null;
        Color color = Color.BLACK;
        int stroke = 2;

        public DrawPanel() {
            setPreferredSize(new Dimension(640, 480));
            setBackground(Color.WHITE);
            MouseAdapter ma = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    currentPath = new ArrayList<>();
                    currentPath.add(new DrawPoint(e.getPoint(), color, stroke));
                }
                public void mouseDragged(MouseEvent e) {
                    if (currentPath != null) {
                        currentPath.add(new DrawPoint(e.getPoint(), color, stroke));
                        repaint();
                    }
                }
                public void mouseReleased(MouseEvent e) {
                    if (currentPath != null) {
                        currentPath.add(new DrawPoint(e.getPoint(), color, stroke));
                        DrawAction action = new DrawAction(new ArrayList<>(currentPath), color, stroke);
                        actions.add(action);
                        if (undoStack.size() == MAX_HISTORY) undoStack.remove(0);
                        undoStack.push(action);
                        redoStack.clear();
                        redoButton.setEnabled(false);
                        undoButton.setEnabled(true);
                        currentPath = null;
                        repaint();
                    }
                }
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }
        public void setColor(Color c) { this.color = c; }
        public void setStroke(int s) { this.stroke = s; }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (DrawAction action : actions) {
                drawPath(g, action);
            }
            if (currentPath != null) drawPath(g, currentPath, color, stroke);
        }
        private void drawPath(Graphics g, DrawAction action) {
            drawPath(g, action.path, action.color, action.stroke);
        }
        private void drawPath(Graphics g, ArrayList<DrawPoint> path, Color color, int stroke) {
            for (int i = 1; i < path.size(); i++) {
                DrawPoint p1 = path.get(i-1), p2 = path.get(i);
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(color);
                g2.setStroke(new BasicStroke(stroke));
                g2.drawLine(p1.point.x, p1.point.y, p2.point.x, p2.point.y);
            }
        }
    }
    static class DrawPoint {
        Point point;
        Color color;
        int stroke;
        DrawPoint(Point p, Color c, int s) {
            point = p; color = c; stroke = s;
        }
    }
}
