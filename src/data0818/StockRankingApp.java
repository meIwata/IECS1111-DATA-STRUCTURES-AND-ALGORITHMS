package data0818;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class StockRecord {
    String code;
    LocalDate date;
    int volume;
    long amount;
    public StockRecord(String code, LocalDate date, int volume, long amount) {
        this.code = code;
        this.date = date;
        this.volume = volume;
        this.amount = amount;
    }
}

public class StockRankingApp extends JFrame {
    private List<StockRecord> records = new ArrayList<>();
    private List<String> stockCodes = new ArrayList<>();
    private List<LocalDate> dates = new ArrayList<>();
    private JTextField startDateField, endDateField, kField;
    private JComboBox<String> metricBox;
    private JTable table;

    public StockRankingApp() {
        generateData();
        setTitle("股票排行榜");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("起始日期(yyyy-MM-dd):"));
        startDateField = new JTextField(10);
        inputPanel.add(startDateField);
        inputPanel.add(new JLabel("結束日期(yyyy-MM-dd):"));
        endDateField = new JTextField(10);
        inputPanel.add(endDateField);
        inputPanel.add(new JLabel("前K名:"));
        kField = new JTextField(5);
        inputPanel.add(kField);
        metricBox = new JComboBox<>(new String[]{"成交量", "成交金額"});
        inputPanel.add(metricBox);
        JButton searchBtn = new JButton("查詢");
        inputPanel.add(searchBtn);
        add(inputPanel, BorderLayout.NORTH);
        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRanking();
            }
        });
    }

    private void generateData() {
        // 生成日期
        LocalDate start = LocalDate.of(2024, 1, 1);
        for (int i = 0; i < 400; i++) {
            dates.add(start.plusDays(i));
        }
        // 生成股票代碼
        for (int i = 1; i <= 1800; i++) {
            stockCodes.add(String.format("STK%04d", i));
        }
        Random rand = new Random();
        // 生成交易資料
        for (LocalDate date : dates) {
            for (String code : stockCodes) {
                int volume = rand.nextInt(100000) + 1000;
                long amount = (long) (volume * (rand.nextInt(1000) + 10));
                records.add(new StockRecord(code, date, volume, amount));
            }
        }
    }

    private void showRanking() {
        String startStr = startDateField.getText().trim();
        String endStr = endDateField.getText().trim();
        String metric = (String) metricBox.getSelectedItem();
        int k = 10;
        try {
            k = Integer.parseInt(kField.getText().trim());
        } catch (Exception ex) {
            k = 10;
        }
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startStr);
            endDate = LocalDate.parse(endStr);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "日期格式錯誤");
            return;
        }
        // 檢查區間是否有資料，若無則自動生成該區間假資料
        boolean hasData = records.stream().anyMatch(r -> !r.date.isBefore(startDate) && !r.date.isAfter(endDate));
        if (!hasData) {
            Random rand = new Random();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                for (String code : stockCodes) {
                    int volume = rand.nextInt(100000) + 1000;
                    long amount = (long) (volume * (rand.nextInt(1000) + 10));
                    records.add(new StockRecord(code, date, volume, amount));
                }
            }
        }
        // 区间筛选
        List<StockRecord> filtered = records.stream()
                .filter(r -> !r.date.isBefore(startDate) && !r.date.isAfter(endDate))
                .collect(Collectors.toList());
        // 按股票分组
        Map<String, List<StockRecord>> grouped = filtered.stream().collect(Collectors.groupingBy(r -> r.code));
        // 统计总成交量/金额
        List<Object[]> ranking = new ArrayList<>();
        for (String code : grouped.keySet()) {
            long totalVolume = grouped.get(code).stream().mapToLong(r -> r.volume).sum();
            long totalAmount = grouped.get(code).stream().mapToLong(r -> r.amount).sum();
            ranking.add(new Object[]{code, totalVolume, totalAmount});
        }
        // 排序
        if ("成交量".equals(metric)) {
            ranking.sort((a, b) -> Long.compare((Long) b[1], (Long) a[1]));
        } else {
            ranking.sort((a, b) -> Long.compare((Long) b[2], (Long) a[2]));
        }
        // 取前K
        List<Object[]> topK = ranking.stream().limit(k).collect(Collectors.toList());
        // 显示
        DefaultTableModel model = new DefaultTableModel(new Object[]{"股票代碼", "總成交量", "總成交金額"}, 0);
        for (Object[] row : topK) {
            model.addRow(row);
        }
        table.setModel(model);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StockRankingApp().setVisible(true);
        });
    }
}
