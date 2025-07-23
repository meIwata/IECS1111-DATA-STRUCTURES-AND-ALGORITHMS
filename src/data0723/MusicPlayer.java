package data0723;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class MusicPlayer extends JFrame {
    private LinkedList<String> playlist = new LinkedList<>();
    private int currentIndex = -1;
    private JLabel currentSongLabel;
    private JTextArea playlistArea;

    public MusicPlayer() {
        setTitle("簡易音樂播放器");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 顯示目前歌曲
        currentSongLabel = new JLabel("目前無播放歌曲");
        add(currentSongLabel, BorderLayout.NORTH);

        // 播放清單顯示
        playlistArea = new JTextArea();
        playlistArea.setEditable(false);
        add(new JScrollPane(playlistArea), BorderLayout.CENTER);

        // 按鈕區
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("新增歌曲");
        JButton removeButton = new JButton("刪除歌曲");
        JButton nextButton = new JButton("下一首");
        JButton prevButton = new JButton("上一首");
        JButton showButton = new JButton("顯示清單");
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(showButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 事件處理
        addButton.addActionListener(e -> addSongByChooser());
        removeButton.addActionListener(e -> removeSongByDialog());
        nextButton.addActionListener(e -> playNext());
        prevButton.addActionListener(e -> playPrevious());
        showButton.addActionListener(e -> showPlaylist());
    }

    // 新增歌曲（用檔案選擇器）
    private void addSongByChooser() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getName();
            addSong(fileName);
        }
    }

    // 刪除歌曲（用輸入框）
    private void removeSongByDialog() {
        String title = JOptionPane.showInputDialog(this, "輸入要刪除的歌曲名稱:");
        if (title != null && !title.isEmpty()) {
            removeSong(title);
        }
    }

    // 新增歌曲到清單尾端
    public void addSong(String title) {
        playlist.add(title);
        if (currentIndex == -1) {
            currentIndex = 0;
        }
        updateCurrentSong();
    }

    // 刪除第一個出現的指定歌曲
    public void removeSong(String title) {
        int idx = playlist.indexOf(title);
        if (idx != -1) {
            playlist.remove(idx);
            if (idx == currentIndex) {
                if (playlist.size() == 0) currentIndex = -1;
                else if (currentIndex >= playlist.size()) currentIndex = playlist.size() - 1;
            } else if (idx < currentIndex) {
                currentIndex--;
            }
            updateCurrentSong();
        } else {
            JOptionPane.showMessageDialog(this, "找不到該歌曲");
        }
    }

    // 播放下一首
    public void playNext() {
        if (playlist.size() == 0) return;
        if (currentIndex < playlist.size() - 1) {
            currentIndex++;
            updateCurrentSong();
        }
    }

    // 播放上一首
    public void playPrevious() {
        if (playlist.size() == 0) return;
        if (currentIndex > 0) {
            currentIndex--;
            updateCurrentSong();
        }
    }

    // 顯示清單
    public void showPlaylist() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playlist.size(); i++) {
            if (i == currentIndex) sb.append("▶ ");
            sb.append(playlist.get(i)).append("\n");
        }
        playlistArea.setText(sb.toString());
    }

    // 顯示目前歌曲
    public void updateCurrentSong() {
        if (currentIndex >= 0 && currentIndex < playlist.size()) {
            currentSongLabel.setText("目前播放: " + playlist.get(currentIndex));
        } else {
            currentSongLabel.setText("目前無播放歌曲");
        }
        showPlaylist();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MusicPlayer().setVisible(true));
    }
}

