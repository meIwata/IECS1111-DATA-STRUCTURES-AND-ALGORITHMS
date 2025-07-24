package data0724;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * 表達式轉換器和計算器應用程序
 * 可以將中序式轉換為後序式和前序式，並進行計算
 */
public class ExpressionConverter {

    /**
     * 程序入口點
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new ExpressionGUI();
        });
    }

    /**
     * 表達式轉換與計算的核心功能
     */
    static class ExpressionUtil {
        // 轉換中序表達式為後序
        public static String toPostfix(String infix) {
            String preprocessed = preprocessExpression(infix);
            StringBuilder postfix = new StringBuilder();
            Stack<Character> stack = new Stack<>();

            for (int i = 0; i < preprocessed.length(); i++) {
                char c = preprocessed.charAt(i);

                // 跳過空格
                if (c == ' ') continue;

                // 如果是操作數（字母或數字），添加到後序式
                if (Character.isLetterOrDigit(c)) {
                    postfix.append(c);
                }
                // 如果是左括號，壓入堆棧
                else if (c == '(') {
                    stack.push(c);
                }
                // 如果是右括號
                else if (c == ')') {
                    // 彈出並附加所有操作符直到找到左括號
                    while (!stack.isEmpty() && stack.peek() != '(') {
                        postfix.append(stack.pop());
                    }

                    // 移除左括號
                    if (!stack.isEmpty() && stack.peek() == '(') {
                        stack.pop();
                    }
                }
                // 如果是冪運算符
                else if (c == '^') {
                    while (!stack.isEmpty() && precedence(stack.peek()) > precedence(c)) {
                        postfix.append(stack.pop());
                    }
                    stack.push(c);
                }
                // 如果是一元負號（表示為'~'）
                else if (c == '~') {
                    stack.push(c);
                }
                // 如果是其他運算符
                else {
                    while (!stack.isEmpty() && precedence(stack.peek()) >= precedence(c)) {
                        postfix.append(stack.pop());
                    }
                    stack.push(c);
                }
            }

            // 彈出堆棧中剩餘的所有運算符
            while (!stack.isEmpty()) {
                postfix.append(stack.pop());
            }

            return postfix.toString();
        }

        // 轉換中序表達式為前序
        public static String toPrefix(String infix) {
            // 反轉中序表達式
            StringBuilder reversedInfix = new StringBuilder(infix).reverse();

            // 交換'('和')'
            for (int i = 0; i < reversedInfix.length(); i++) {
                if (reversedInfix.charAt(i) == '(') {
                    reversedInfix.setCharAt(i, ')');
                } else if (reversedInfix.charAt(i) == ')') {
                    reversedInfix.setCharAt(i, '(');
                }
            }

            // 轉換為後序
            String postfix = toPostfix(reversedInfix.toString());

            // 反轉後序以獲得前序
            return new StringBuilder(postfix).reverse().toString();
        }

        // 將用於顯示的表達式中的一元負號'~'轉換為'-'
        public static String formatForDisplay(String expression) {
            return expression.replace('~', '-');
        }

        // 預處理表達式以處理一元負號
        private static String preprocessExpression(String infix) {
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < infix.length(); i++) {
                char c = infix.charAt(i);

                // 處理一元負號
                if (c == '-') {
                    // 檢查是否為一元負號
                    if (i == 0 || infix.charAt(i-1) == '(' || isOperator(infix.charAt(i-1))) {
                        result.append('~'); // 使用~表示一元負號
                        continue;
                    }
                }
                result.append(c);
            }

            return result.toString();
        }

        // 返回運算符的優先級
        private static int precedence(char op) {
            switch (op) {
                case '+': case '-': return 1;
                case '*': case '/': return 2;
                case '^': return 3;
                case '~': return 4; // 一元負號具有最高優先級
            }
            return -1;
        }

        // 檢查字符是否為運算符
        private static boolean isOperator(char c) {
            return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
        }

        // 計算後序表達式的值（給定變量值）
        public static double evaluate(String postfix, Map<Character, Double> variables) {
            Stack<Double> stack = new Stack<>();

            for (int i = 0; i < postfix.length(); i++) {
                char c = postfix.charAt(i);

                if (Character.isLetter(c)) {
                    // 壓入變量值
                    if (variables.containsKey(c)) {
                        stack.push(variables.get(c));
                    } else {
                        throw new IllegalArgumentException("未提供變量'" + c + "'的值");
                    }
                } else if (c == '~') {
                    // 一元負號
                    double operand = stack.pop();
                    stack.push(-operand);
                } else if (isOperator(c)) {
                    // 二元運算符
                    double b = stack.pop();
                    double a = stack.pop();

                    switch (c) {
                        case '+': stack.push(a + b); break;
                        case '-': stack.push(a - b); break;
                        case '*': stack.push(a * b); break;
                        case '/': stack.push(a / b); break;
                        case '^': stack.push(Math.pow(a, b)); break;
                    }
                }
            }

            return stack.pop();
        }
    }

    /**
     * 表達式轉換器的圖形用戶界面
     */
    static class ExpressionGUI extends JFrame {
        private JTextField infixField;
        private JTextArea postfixArea;
        private JTextArea prefixArea;
        private JPanel variablesPanel;
        private JTextField resultField;
        private Map<Character, JTextField> variableFields;
        private String rawPostfix; // 存儲原始的後序表達式（用於計算）

        public ExpressionGUI() {
            setTitle("表達式轉換器和計算器");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            ((JPanel)getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

            // 輸入面板
            JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
            inputPanel.add(new JLabel("中序表達式:"), BorderLayout.WEST);
            infixField = new JTextField(30);
            inputPanel.add(infixField, BorderLayout.CENTER);
            JButton convertButton = new JButton("轉換");
            inputPanel.add(convertButton, BorderLayout.EAST);
            add(inputPanel, BorderLayout.NORTH);

            // 結果面板
            JPanel resultsPanel = new JPanel(new GridLayout(2, 1, 5, 5));

            JPanel postfixPanel = new JPanel(new BorderLayout());
            postfixPanel.add(new JLabel("後序表達式:"), BorderLayout.NORTH);
            postfixArea = new JTextArea(3, 30);
            postfixArea.setEditable(false);
            postfixPanel.add(new JScrollPane(postfixArea), BorderLayout.CENTER);
            resultsPanel.add(postfixPanel);

            JPanel prefixPanel = new JPanel(new BorderLayout());
            prefixPanel.add(new JLabel("前序表達式:"), BorderLayout.NORTH);
            prefixArea = new JTextArea(3, 30);
            prefixArea.setEditable(false);
            prefixPanel.add(new JScrollPane(prefixArea), BorderLayout.CENTER);
            resultsPanel.add(prefixPanel);

            add(resultsPanel, BorderLayout.CENTER);

            // 變量和計算面板
            JPanel calculationPanel = new JPanel(new BorderLayout(5, 5));

            variablesPanel = new JPanel();
            variablesPanel.setLayout(new BoxLayout(variablesPanel, BoxLayout.Y_AXIS));
            calculationPanel.add(variablesPanel, BorderLayout.CENTER);

            JPanel evaluatePanel = new JPanel(new BorderLayout(5, 5));
            JButton evaluateButton = new JButton("計算");
            evaluatePanel.add(evaluateButton, BorderLayout.WEST);
            resultField = new JTextField();
            resultField.setEditable(false);
            evaluatePanel.add(resultField, BorderLayout.CENTER);
            calculationPanel.add(evaluatePanel, BorderLayout.SOUTH);

            add(calculationPanel, BorderLayout.SOUTH);

            // 初始化變量字段映射
            variableFields = new HashMap<>();

            // 添加事件監聽器
            convertButton.addActionListener(this::convertExpression);
            evaluateButton.addActionListener(this::evaluateExpression);

            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        private void convertExpression(ActionEvent e) {
            try {
                String infix = infixField.getText();
                if (infix.isEmpty()) {
                    showError("請輸入中序表達式");
                    return;
                }

                // 儲存原始後序表達式以供計算使用
                rawPostfix = ExpressionUtil.toPostfix(infix);

                // 為顯示格式化後序和前序表達式（將~轉換為-）
                String displayPostfix = ExpressionUtil.formatForDisplay(rawPostfix);
                String displayPrefix = ExpressionUtil.formatForDisplay(ExpressionUtil.toPrefix(infix));

                postfixArea.setText(displayPostfix);
                prefixArea.setText(displayPrefix);

                updateVariableFields(infix);

            } catch (Exception ex) {
                showError("錯誤: " + ex.getMessage());
            }
        }

        private void evaluateExpression(ActionEvent e) {
            try {
                String infix = infixField.getText();
                if (infix.isEmpty()) {
                    showError("請輸入中序表達式");
                    return;
                }

                Map<Character, Double> variableValues = new HashMap<>();

                for (Map.Entry<Character, JTextField> entry : variableFields.entrySet()) {
                    String valueText = entry.getValue().getText().trim();
                    if (valueText.isEmpty()) {
                        showError("請為變量'" + entry.getKey() + "'輸入值");
                        return;
                    }
                    variableValues.put(entry.getKey(), Double.parseDouble(valueText));
                }

                // 使用原始後序表達式進行計算
                double result = ExpressionUtil.evaluate(rawPostfix, variableValues);
                resultField.setText(String.valueOf(result));

            } catch (NumberFormatException ex) {
                showError("數字格式無效");
            } catch (Exception ex) {
                showError("錯誤: " + ex.getMessage());
            }
        }

        private void updateVariableFields(String expression) {
            // 在表達式中查找所有變量名稱
            Set<Character> variables = new HashSet<>();
            for (char c : expression.toCharArray()) {
                if (Character.isLetter(c)) {
                    variables.add(c);
                }
            }

            // 清除當前變量字段
            variablesPanel.removeAll();
            variableFields.clear();

            if (!variables.isEmpty()) {
                variablesPanel.add(new JLabel("輸入變量值:"));

                // 為每個變量創建輸入字段
                for (Character variable : variables) {
                    JPanel varPanel = new JPanel(new BorderLayout(5, 5));
                    varPanel.add(new JLabel(variable + " = "), BorderLayout.WEST);
                    JTextField varField = new JTextField(10);
                    varPanel.add(varField, BorderLayout.CENTER);
                    variablesPanel.add(varPanel);

                    variableFields.put(variable, varField);
                }
            }

            variablesPanel.revalidate();
            variablesPanel.repaint();
            pack();
        }

        private void showError(String message) {
            JOptionPane.showMessageDialog(this, message, "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }
}