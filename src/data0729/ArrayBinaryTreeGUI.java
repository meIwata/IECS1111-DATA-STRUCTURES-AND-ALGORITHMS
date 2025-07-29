package data0729;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ArrayBinaryTreeGUI extends JFrame {
    private static final int TREE_SIZE = 31; // 支援最多 31 個節點（5 層）
    private int[] tree = new int[TREE_SIZE];
    private JTextField inputField, autoGenField;
    private JTextArea traversalArea;
    private JPanel treePanel;
    private JCheckBox inorderCheck, preorderCheck, postorderCheck;

    public ArrayBinaryTreeGUI() {
        Arrays.fill(tree, Integer.MIN_VALUE); // 空節點用 Integer.MIN_VALUE

        setTitle("陣列建立二元樹教學");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 上方操作區
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));

        // 輸入與按鈕
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(8);
        inputPanel.add(new JLabel("輸入要加入/刪除的數字:"));
        inputPanel.add(inputField);

        JButton insertBtn = new JButton("加入節點");
        JButton deleteBtn = new JButton("刪除節點");
        inputPanel.add(insertBtn);
        inputPanel.add(deleteBtn);

        // 自動產生
        JPanel autoGenPanel = new JPanel();
        autoGenField = new JTextField(5);
        JButton autoGenBtn = new JButton("自動產生N個不重覆數字並加入");
        autoGenPanel.add(new JLabel("N:"));
        autoGenPanel.add(autoGenField);
        autoGenPanel.add(autoGenBtn);

        // 拜訪選擇
        JPanel visitPanel = new JPanel();
        inorderCheck = new JCheckBox("Inorder");
        preorderCheck = new JCheckBox("Preorder");
        postorderCheck = new JCheckBox("Postorder");
        JButton visitBtn = new JButton("拜訪並輸出");
        visitPanel.add(new JLabel("選擇拜訪方式:"));
        visitPanel.add(inorderCheck);
        visitPanel.add(preorderCheck);
        visitPanel.add(postorderCheck);
        visitPanel.add(visitBtn);

        controlPanel.add(inputPanel);
        controlPanel.add(autoGenPanel);
        controlPanel.add(visitPanel);

        add(controlPanel, BorderLayout.NORTH);

        // 畫樹區
        treePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawTree(g);
            }
        };
        treePanel.setPreferredSize(new Dimension(900, 350));
        add(treePanel, BorderLayout.CENTER);

        // 拜訪結果顯示
        traversalArea = new JTextArea(7, 80);
        traversalArea.setEditable(false);
        add(new JScrollPane(traversalArea), BorderLayout.SOUTH);

        // 事件
        insertBtn.addActionListener(e -> handleInsert());
        deleteBtn.addActionListener(e -> handleDelete());
        autoGenBtn.addActionListener(e -> handleAutoGen());
        visitBtn.addActionListener(e -> handleTraversal());

        setVisible(true);
    }

    // 插入節點（陣列左到右尋找空位）
    private void handleInsert() {
        String text = inputField.getText().trim();
        if (text.matches("-?\\d+")) {
            int val = Integer.parseInt(text);
            if (exists(val)) {
                JOptionPane.showMessageDialog(this, "數字已存在於樹中！");
                return;
            }
            for (int i = 0; i < TREE_SIZE; i++) {
                if (tree[i] == Integer.MIN_VALUE) {
                    tree[i] = val;
                    treePanel.repaint();
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "樹已滿！");
        }
    }

    // 刪除節點
    private void handleDelete() {
        String text = inputField.getText().trim();
        if (text.matches("-?\\d+")) {
            int val = Integer.parseInt(text);
            int idx = findIndex(val);
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "樹中找不到該數字！");
                return;
            }
            // 若為樹葉，直接刪除；否則刪除子樹
            if (isLeaf(idx)) {
                tree[idx] = Integer.MIN_VALUE;
            } else {
                removeSubtree(idx);
            }
            treePanel.repaint();
        }
    }

    // 自動產生不重覆資料並加入
    private void handleAutoGen() {
        String text = autoGenField.getText().trim();
        if (text.matches("\\d+")) {
            int N = Integer.parseInt(text);
            Set<Integer> existed = new HashSet<>();
            for (int v : tree) if (v != Integer.MIN_VALUE) existed.add(v);
            Random rand = new Random();
            int count = 0, tries = 0;
            while (count < N && tries < N*10) {
                int val = rand.nextInt(1000);
                if (!existed.contains(val)) {
                    for (int i = 0; i < TREE_SIZE; i++) {
                        if (tree[i] == Integer.MIN_VALUE) {
                            tree[i] = val;
                            existed.add(val);
                            count++;
                            break;
                        }
                    }
                }
                tries++;
            }
            treePanel.repaint();
        }
    }

    // 拜訪並輸出
    private void handleTraversal() {
        traversalArea.setText("");
        if (inorderCheck.isSelected()) {
            List<Integer> res = new ArrayList<>();
            inorder(0, res);
            traversalArea.append("Inorder: " + res.toString() + "\n");
        }
        if (preorderCheck.isSelected()) {
            List<Integer> res = new ArrayList<>();
            preorder(0, res);
            traversalArea.append("Preorder: " + res.toString() + "\n");
        }
        if (postorderCheck.isSelected()) {
            List<Integer> res = new ArrayList<>();
            postorder(0, res);
            traversalArea.append("Postorder: " + res.toString() + "\n");
        }
    }

    // 畫樹
    private void drawTree(Graphics g) {
        int w = treePanel.getWidth();
        int h = treePanel.getHeight();
        int nodeSize = 32;
        int[] xs = new int[TREE_SIZE];
        int[] ys = new int[TREE_SIZE];
        for (int i = 0; i < TREE_SIZE; i++) {
            int level = (int)(Math.log(i + 1) / Math.log(2));
            int maxNodes = (int)Math.pow(2, level);
            int pos = i - (maxNodes - 1);
            ys[i] = 30 + level * 60;
            xs[i] = w / (maxNodes + 1) * (pos + 1);
        }
        g.setFont(new Font("Arial", Font.BOLD, 14));
        for (int i = 0; i < TREE_SIZE; i++) {
            if (tree[i] != Integer.MIN_VALUE) {
                // 畫線
                int left = 2 * i + 1, right = 2 * i + 2;
                if (left < TREE_SIZE && tree[left] != Integer.MIN_VALUE) {
                    g.drawLine(xs[i], ys[i], xs[left], ys[left]);
                }
                if (right < TREE_SIZE && tree[right] != Integer.MIN_VALUE) {
                    g.drawLine(xs[i], ys[i], xs[right], ys[right]);
                }
            }
        }
        for (int i = 0; i < TREE_SIZE; i++) {
            if (tree[i] != Integer.MIN_VALUE) {
                g.setColor(Color.ORANGE);
                g.fillOval(xs[i] - nodeSize / 2, ys[i] - nodeSize / 2, nodeSize, nodeSize);
                g.setColor(Color.BLACK);
                g.drawOval(xs[i] - nodeSize / 2, ys[i] - nodeSize / 2, nodeSize, nodeSize);
                String s = String.valueOf(tree[i]);
                g.drawString(s, xs[i] - 8, ys[i] + 5);
            }
        }
    }

    // 樹葉判斷
    private boolean isLeaf(int idx) {
        int left = 2 * idx + 1, right = 2 * idx + 2;
        return (left >= TREE_SIZE || tree[left] == Integer.MIN_VALUE)
                && (right >= TREE_SIZE || tree[right] == Integer.MIN_VALUE);
    }

    // 刪除子樹
    private void removeSubtree(int idx) {
        if (idx >= TREE_SIZE || tree[idx] == Integer.MIN_VALUE) return;
        tree[idx] = Integer.MIN_VALUE;
        removeSubtree(2 * idx + 1);
        removeSubtree(2 * idx + 2);
    }

    // 節點是否存在
    private boolean exists(int val) {
        for (int i : tree) if (i == val) return true;
        return false;
    }

    // 找值所在 index
    private int findIndex(int val) {
        for (int i = 0; i < TREE_SIZE; i++) {
            if (tree[i] == val) return i;
        }
        return -1;
    }

    // 拜訪演算法
    private void inorder(int idx, List<Integer> res) {
        if (idx >= TREE_SIZE || tree[idx] == Integer.MIN_VALUE) return;
        inorder(2 * idx + 1, res);
        res.add(tree[idx]);
        inorder(2 * idx + 2, res);
    }

    private void preorder(int idx, List<Integer> res) {
        if (idx >= TREE_SIZE || tree[idx] == Integer.MIN_VALUE) return;
        res.add(tree[idx]);
        preorder(2 * idx + 1, res);
        preorder(2 * idx + 2, res);
    }

    private void postorder(int idx, List<Integer> res) {
        if (idx >= TREE_SIZE || tree[idx] == Integer.MIN_VALUE) return;
        postorder(2 * idx + 1, res);
        postorder(2 * idx + 2, res);
        res.add(tree[idx]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ArrayBinaryTreeGUI::new);
    }
}