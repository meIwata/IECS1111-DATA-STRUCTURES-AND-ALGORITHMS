package data0828;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SearchCompareGUI extends JFrame {
    static final int DATA_SIZE = 1000000; // 與 transactions.csv 一致
    static final int TEST_SIZE = 10;
    int[] dataInt; // TransactionID 整數陣列
    HashSet<Integer> hashSetInt;

    public SearchCompareGUI() {
        setTitle("搜尋效能比較");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        generateData();
        showResult();
    }

    private void generateData() {
        dataInt = new int[DATA_SIZE];
        hashSetInt = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader("transactions.csv"))) {
            String line = br.readLine(); // 跳過標題
            int idx = 0;
            while ((line = br.readLine()) != null && idx < DATA_SIZE) {
                String[] parts = line.split(",");
                if (parts.length > 0) {
                    String transactionId = parts[0];
                    int idNum = Integer.parseInt(transactionId.substring(1));
                    dataInt[idx++] = idNum;
                    hashSetInt.add(idNum);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "找不到 transactions.csv，請先產生交易資料！", "錯誤", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private int[] getExistingKeys() {
        Random rand = new Random();
        int[] keys = new int[TEST_SIZE];
        for (int i = 0; i < TEST_SIZE; i++) {
            keys[i] = dataInt[rand.nextInt(DATA_SIZE)];
        }
        return keys;
    }

    private int[] getNonExistingKeys() {
        Random rand = new Random();
        int[] keys = new int[TEST_SIZE];
        for (int i = 0; i < TEST_SIZE; i++) {
            int k;
            do {
                k = DATA_SIZE + rand.nextInt(10000) + 1;
            } while (hashSetInt.contains(k));
            keys[i] = k;
        }
        return keys;
    }

    private long avgTimeLinear(int[] keys) {
        long total = 0;
        for (int k : keys) {
            long t1 = System.nanoTime();
            boolean found = false;
            for (int v : dataInt) {
                if (v == k) { found = true; break; }
            }
            long t2 = System.nanoTime();
            total += (t2 - t1);
        }
        return total / TEST_SIZE;
    }

    private long avgTimeBinary(int[] keys) {
        long total = 0;
        Arrays.sort(dataInt); // 確保排序
        for (int k : keys) {
            long t1 = System.nanoTime();
            Arrays.binarySearch(dataInt, k);
            long t2 = System.nanoTime();
            total += (t2 - t1);
        }
        return total / TEST_SIZE;
    }

    private long avgTimeInterpolation(int[] keys) {
        long total = 0;
        Arrays.sort(dataInt); // 確保排序
        for (int k : keys) {
            long t1 = System.nanoTime();
            interpolationSearch(dataInt, k);
            long t2 = System.nanoTime();
            total += (t2 - t1);
        }
        return total / TEST_SIZE;
    }

    private long avgTimeHash(int[] keys) {
        long total = 0;
        for (int k : keys) {
            long t1 = System.nanoTime();
            hashSetInt.contains(k);
            long t2 = System.nanoTime();
            total += (t2 - t1);
        }
        return total / TEST_SIZE;
    }

    // 插補搜尋 (int版)
    private int interpolationSearch(int[] arr, int x) {
        int lo = 0, hi = arr.length - 1;
        while (lo <= hi && x >= arr[lo] && x <= arr[hi]) {
            if (arr[hi] == arr[lo]) {
                if (arr[lo] == x) return lo;
                return -1;
            }
            int pos = lo + ((x - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo]);
            if (pos < lo || pos > hi) break; // 防止超界
            if (arr[pos] == x) return pos;
            if (arr[pos] < x) lo = pos + 1;
            else hi = pos - 1;
        }
        return -1;
    }

    private void showResult() {
        int[] existKeys = getExistingKeys();
        int[] nonExistKeys = getNonExistingKeys();
        long linExist = avgTimeLinear(existKeys);
        long binExist = avgTimeBinary(existKeys);
        long intExist = avgTimeInterpolation(existKeys);
        long hashExist = avgTimeHash(existKeys);
        long linNon = avgTimeLinear(nonExistKeys);
        long binNon = avgTimeBinary(nonExistKeys);
        long intNon = avgTimeInterpolation(nonExistKeys);
        long hashNon = avgTimeHash(nonExistKeys);

        String[] columns = {"搜尋方法", "存在KEY平均時間(ns)", "不存在KEY平均時間(ns)", "比線性搜尋快多少倍"};
        Object[][] rows = {
            {"線性搜尋", linExist, linNon, "1"},
            {"二分搜尋", binExist, binNon, String.format("%.2f", (double)linExist/binExist)},
            {"插補搜尋", intExist, intNon, String.format("%.2f", (double)linExist/intExist)},
            {"雜湊搜尋", hashExist, hashNon, String.format("%.2f", (double)linExist/hashExist)}
        };
        DefaultTableModel model = new DefaultTableModel(rows, columns);
        JTable table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchCompareGUI().setVisible(true));
    }
}
