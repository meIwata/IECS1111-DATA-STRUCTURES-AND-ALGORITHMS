package data0819;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class StockDataGenerator extends JFrame {
    private JTextField stockCountField, dateCountField, maxFileSizeField, outputField;
    private JButton chooseButton, generateButton;
    private JFileChooser fileChooser = new JFileChooser();

    private static final String[] COMMON_CHINESE = {
            "長", "榮", "台", "積", "電", "鴻", "海", "富", "邦", "金", "聯", "發", "科", "國", "泰", "證", "大", "統", "中", "鋼",
            "華", "信", "南", "亞", "塑", "永", "豐", "餘", "宏", "群", "旺", "新", "光", "人", "壽", "環", "球", "晶"
    };

    public StockDataGenerator() {
        setTitle("股票測試資料產生器");
        setLayout(new GridLayout(6, 2, 10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        stockCountField = new JTextField("18000");
        dateCountField = new JTextField("2000");
        maxFileSizeField = new JTextField("500");
        outputField = new JTextField();
        outputField.setEditable(false);

        chooseButton = new JButton("選擇儲存位置");
        generateButton = new JButton("開始產生");

        add(new JLabel("股票檔數:"));
        add(stockCountField);
        add(new JLabel("交易日期數:"));
        add(dateCountField);
        add(new JLabel("最大檔案(MB):"));
        add(maxFileSizeField);
        add(new JLabel("輸出檔案:"));
        add(outputField);
        add(chooseButton);
        add(generateButton);

        chooseButton.addActionListener(e -> {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int ret = fileChooser.showSaveDialog(this);
            if (ret == JFileChooser.APPROVE_OPTION) {
                outputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        generateButton.addActionListener(e -> generateData());

        setSize(500, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String genStockName(Random rnd) {
        int len = 2 + rnd.nextInt(5);
        StringBuilder sb = new StringBuilder();
        Set<Integer> used = new HashSet<>();
        for (int i=0; i < len; i++) {
            int idx;
            do {
                idx = rnd.nextInt(COMMON_CHINESE.length);
            } while (used.contains(idx));
            used.add(idx);
            sb.append(COMMON_CHINESE[idx]);
        }
        return sb.toString();
    }

    private List<String> genTradeDates(int count) {
        List<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int added = 0;
        while (added < count) {
            if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY &&
                    cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                dates.add(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
                added++;
            }
            cal.add(Calendar.DATE, -1);
        }
        Collections.reverse(dates);
        return dates;
    }

    private void generateData() {
        try {
            int stockCount = Integer.parseInt(stockCountField.getText().trim());
            int dateCount = Integer.parseInt(dateCountField.getText().trim());
            int maxMB = Integer.parseInt(maxFileSizeField.getText().trim());
            String outPath = outputField.getText().trim();

            if (outPath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請選擇輸出檔案!");
                return;
            }

            // 估算一列大約 130 bytes，計算最大可有幾天
            long rowSize = 130;
            long maxRows = (maxMB * 1024L * 1024L) / rowSize;
            int daysPerStock = (int)Math.min(dateCount, maxRows / stockCount);

            List<String> tradeDates = genTradeDates(daysPerStock);
            Random rnd = new Random();

            try (BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(outPath), StandardCharsets.UTF_8))) {
                bw.write("股票代碼,股票名稱,交易日期,成交量,成交金額,當日單筆最大成交量,當日單筆最大成交金額,當日單筆最小成交量,當日單筆最小成交成交金額\n");
                Set<String> usedNames = new HashSet<>();
                for (int i=0; i < stockCount; i++) {
                    String code = String.format("%06d", 100000 + i);
                    String name;
                    do {
                        name = genStockName(rnd);
                    } while (usedNames.contains(name));
                    usedNames.add(name);
                    for (String day : tradeDates) {
                        int totalVol = 1000 + rnd.nextInt(999999);
                        int maxVol = totalVol == 1000 ? 1000 : 1 + rnd.nextInt(totalVol);
                        int minVol = 1 + rnd.nextInt(maxVol);
                        int totalAmt = totalVol * (10 + rnd.nextInt(1000));
                        int maxAmt = maxVol * (10 + rnd.nextInt(1000));
                        int minAmt = minVol * (10 + rnd.nextInt(1000));
                        bw.write(String.join(",", code, name, day,
                                String.valueOf(totalVol),
                                String.valueOf(totalAmt),
                                String.valueOf(maxVol),
                                String.valueOf(maxAmt),
                                String.valueOf(minVol),
                                String.valueOf(minAmt)));
                        bw.write("\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "資料產生完成! 實際每檔天數: " + daysPerStock);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "發生錯誤: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockDataGenerator::new);
    }
}