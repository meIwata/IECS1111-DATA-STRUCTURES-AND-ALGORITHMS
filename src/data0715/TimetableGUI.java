package data0715;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TimetableGUI extends JFrame {
    private static final String[] DAYS = {"星期一", "星期二", "星期三", "星期四", "星期五"};
    private static final String[] PERIODS = {"1", "2", "3", "4", "5", "6"};
    private static final Map<String, Integer> COURSE_NAME_TO_ID = new HashMap<>();
    private static final Map<Integer, String> COURSE_ID_TO_NAME = new HashMap<>();
    private JTable table;
    private DefaultTableModel model;

    static {
        COURSE_NAME_TO_ID.put("計算機概論", 1);
        COURSE_NAME_TO_ID.put("離散數學", 2);
        COURSE_NAME_TO_ID.put("資料結構", 3);
        COURSE_NAME_TO_ID.put("資料庫理論", 4);
        COURSE_NAME_TO_ID.put("上機實習", 5);
        for (Map.Entry<String, Integer> entry : COURSE_NAME_TO_ID.entrySet()) {
            COURSE_ID_TO_NAME.put(entry.getValue(), entry.getKey());
        }
    }

    public TimetableGUI() {
        setTitle("課表編輯器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 表格模型
        String[] columnNames = new String[DAYS.length + 1];
        columnNames[0] = "節次";
        System.arraycopy(DAYS, 0, columnNames, 1, DAYS.length);
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0;
            }
        };
        for (int i = 0; i < PERIODS.length; i++) {
            Object[] row = new Object[DAYS.length + 1];
            row[0] = PERIODS[i];
            for (int j = 1; j <= DAYS.length; j++) {
                row[j] = "";
            }
            model.addRow(row);
        }
        table = new JTable(model);
        table.setRowHeight(32);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 課程拖拉面板
        JPanel coursePanel = new JPanel();
        coursePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        for (String course : COURSE_NAME_TO_ID.keySet()) {
            JLabel label = new JLabel(course);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            label.setOpaque(true);
            label.setBackground(new Color(220, 240, 255));
            label.setTransferHandler(new TransferHandler("text"));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    JComponent comp = (JComponent) evt.getSource();
                    TransferHandler handler = comp.getTransferHandler();
                    handler.exportAsDrag(comp, evt, TransferHandler.COPY);
                }
            });
            coursePanel.add(label);
        }
        add(coursePanel, BorderLayout.NORTH);

        // 表格拖放支持
        table.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.stringFlavor);
            }
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;
                JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
                int row = dl.getRow();
                int col = dl.getColumn();
                if (col == 0) return false;
                try {
                    String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    // 進入編輯狀態
                    if (!table.isEditing() || table.getEditingRow() != row || table.getEditingColumn() != col) {
                        table.editCellAt(row, col);
                    }
                    model.setValueAt(data, row, col);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            public int getSourceActions(JComponent c) {
                return COPY;
            }
        });

        // 支援直接輸入數字代碼自動轉課名
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.getDefaultEditor(Object.class).addCellEditorListener(new javax.swing.event.CellEditorListener() {
            @Override
            public void editingStopped(javax.swing.event.ChangeEvent e) {
                int row = table.getEditingRow();
                int col = table.getEditingColumn();
                if (col > 0 && row >= 0) {
                    Object val = model.getValueAt(row, col);
                    if (val != null) {
                        String str = val.toString().trim();
                        try {
                            int code = Integer.parseInt(str);
                            String cname = COURSE_ID_TO_NAME.get(code);
                            if (cname != null) {
                                model.setValueAt(cname, row, col);
                            }
                        } catch (NumberFormatException ex) {
                            // 非數字，忽略
                        }
                    }
                }
            }
            @Override
            public void editingCanceled(javax.swing.event.ChangeEvent e) {}
        });

        // 保存按鈕
        JButton saveBtn = new JButton("保存");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[][] timetable = new int[PERIODS.length][DAYS.length];
                for (int i = 0; i < PERIODS.length; i++) {
                    for (int j = 0; j < DAYS.length; j++) {
                        String val = (String) model.getValueAt(i, j + 1);
                        int code = 0;
                        if (val != null) {
                            val = val.trim();
                            // 先查課名
                            Integer c = COURSE_NAME_TO_ID.get(val);
                            if (c != null) {
                                code = c;
                            } else {
                                // 再判斷是否為1~5的數字
                                try {
                                    int num = Integer.parseInt(val);
                                    if (COURSE_ID_TO_NAME.containsKey(num)) {
                                        code = num;
                                        // 轉換成課名顯示在表格
                                        model.setValueAt(COURSE_ID_TO_NAME.get(num), i, j + 1);
                                    }
                                } catch (NumberFormatException ex) {
                                    // 不是數字，忽略
                                }
                            }
                        }
                        timetable[i][j] = code;
                    }
                }
                // 輸出陣列
                System.out.println("課表編號陣列:");
                for (int i = 0; i < PERIODS.length; i++) {
                    for (int j = 0; j < DAYS.length; j++) {
                        System.out.print(timetable[i][j]);
                        if (j < DAYS.length - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        });

        // 清除按鈕
        JButton clearBtn = new JButton("清除");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < PERIODS.length; i++) {
                    for (int j = 1; j <= DAYS.length; j++) {
                        model.setValueAt("", i, j);
                    }
                }
            }
        });

        // 下方課名對照
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(saveBtn);
        btnPanel.add(clearBtn);
        infoPanel.add(btnPanel);
        StringBuilder sb = new StringBuilder("課名與編號對照:  ");
        for (Map.Entry<String, Integer> entry : COURSE_NAME_TO_ID.entrySet()) {
            sb.append(entry.getValue()).append(". ").append(entry.getKey()).append("   ");
        }
        JLabel infoLabel = new JLabel(sb.toString());
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);

        setSize(700, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TimetableGUI::new);
    }
}
