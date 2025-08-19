package data0819;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class StockExternalSortGUI extends JFrame {
    private JButton openButton, sortButton;
    private JComboBox<String> sortFieldCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private File selectedFile;

    private final String[] headers = {
            "股票代碼", "股票名稱", "交易日期", "成交量", "成交金額",
            "當日單筆最大成交量", "當日單筆最大成交金額", "當日單筆最小成交量", "當日單筆最小成交成交金額"
    };
    private JLabel timerLabel;

    public StockExternalSortGUI() {
        setTitle("股票資料排序 (外部合併排序)");
        setSize(1100, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        openButton = new JButton("選擇CSV檔");
        sortButton = new JButton("排序並顯示前15");
        sortButton.setEnabled(false);

        sortFieldCombo = new JComboBox<>(new String[]{"成交量", "成交金額"});
        timerLabel = new JLabel("排序耗時: 尚未執行");
        JPanel topPanel = new JPanel();
        topPanel.add(openButton);
        topPanel.add(new JLabel("排序依："));
        topPanel.add(sortFieldCombo);
        topPanel.add(sortButton);
        topPanel.add(timerLabel); // 新增計時器顯示

        tableModel = new DefaultTableModel(headers, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        openButton.addActionListener(e -> chooseFile());
        sortButton.addActionListener(e -> runExternalSort());

        setVisible(true); // 修正：確保視窗顯示
    }

    private void chooseFile() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fc.getSelectedFile();
            sortButton.setEnabled(true);
            setTitle("股票資料排序 (已選檔案: " + selectedFile.getName() + ")");
        }
    }

    private void runExternalSort() {
        if (selectedFile == null) return;
        int sortCol = (sortFieldCombo.getSelectedIndex() == 0) ? 3 : 4; // 成交量或成交金額
        sortButton.setEnabled(false); // 排序期間不可重複點擊
        timerLabel.setText("排序中...");
        SwingWorker<List<String[]>, Void> worker = new SwingWorker<>() {
            long startTime, endTime;
            @Override
            protected List<String[]> doInBackground() throws Exception {
                startTime = System.currentTimeMillis();
                List<String[]> result = externalMergeSortTopN(selectedFile, sortCol, 15);
                endTime = System.currentTimeMillis();
                return result;
            }
            @Override
            protected void done() {
                try {
                    List<String[]> top15 = get();
                    tableModel.setRowCount(0);
                    for (String[] row : top15) {
                        String[] display = Arrays.copyOf(row, headers.length);
                        tableModel.addRow(display);
                    }
                    timerLabel.setText("排序耗時: " + (endTime - startTime) + " 毫秒");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(StockExternalSortGUI.this, "排序發生錯誤: " + ex.getMessage());
                    ex.printStackTrace();
                    timerLabel.setText("排序失敗");
                } finally {
                    sortButton.setEnabled(true); // 排序完成後可再點擊
                }
            }
        };
        worker.execute();
    }

    // 外部merge sort，只回傳前N筆
    private List<String[]> externalMergeSortTopN(File csvFile, int sortCol, int topN) throws IOException {
        int batchSize = 100_000;
        List<File> tempFiles = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8));
        String header = br.readLine();
        String line;
        List<String[]> buffer = new ArrayList<>(batchSize);

        // 分批排序寫入暫存檔
        while ((line = br.readLine()) != null) {
            String[] arr = safeSplit(line);
            if(arr.length < 5) continue; // 資料不正確
            buffer.add(arr);
            if (buffer.size() >= batchSize) {
                tempFiles.add(sortAndSaveBatch(buffer, sortCol));
                buffer.clear();
            }
        }
        if (!buffer.isEmpty()) {
            tempFiles.add(sortAndSaveBatch(buffer, sortCol));
        }
        br.close();

        // 多路歸併
        PriorityQueue<RowPointer> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(
                        parseIntSafe(b.row[sortCol]), parseIntSafe(a.row[sortCol])
                )
        );
        List<BufferedReader> readers = new ArrayList<>();
        for (File f : tempFiles) {
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
            String l = r.readLine();
            if (l != null) pq.add(new RowPointer(safeSplit(l), r));
            readers.add(r);
        }
        List<String[]> result = new ArrayList<>();
        while (!pq.isEmpty() && result.size() < topN) {
            RowPointer rp = pq.poll();
            result.add(rp.row);
            String l = rp.reader.readLine();
            if (l != null) {
                pq.add(new RowPointer(safeSplit(l), rp.reader));
            }
        }
        for (BufferedReader r : readers) r.close();
        for (File f : tempFiles) f.delete();
        return result;
    }

    private File sortAndSaveBatch(List<String[]> buffer, int sortCol) throws IOException {
        buffer.sort((a, b) -> Integer.compare(
                parseIntSafe(b[sortCol]), parseIntSafe(a[sortCol])));
        File temp = File.createTempFile("stocksort", ".tmp");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temp), StandardCharsets.UTF_8))) {
            for (String[] row : buffer) {
                bw.write(String.join(",", row));
                bw.write("\n");
            }
        }
        return temp;
    }

    private static String[] safeSplit(String line) {
        // 基本分割，進階可用CSV parser
        return line.split(",", -1);
    }
    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch(Exception e) { return 0; }
    }

    static class RowPointer {
        String[] row;
        BufferedReader reader;
        RowPointer(String[] row, BufferedReader reader) { this.row = row; this.reader = reader; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StockExternalSortGUI::new);
    }
}