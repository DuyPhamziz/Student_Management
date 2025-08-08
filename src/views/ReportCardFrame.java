package views;

import models.Score;
import models.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportCardFrame extends JFrame {

    private static final String[] SUBJECTS = {
        "Ngữ văn", "Toán", "Ngoại ngữ 1", "Giáo dục thể chất",
        "Giáo dục QP-AN", "Lịch sử", "Địa lý", "Hóa học",
        "Sinh học", "Vật lý", "Tiếng dân tộc", "Ngoại ngữ 2"
    };

    public ReportCardFrame(Student student, List<Score> scores, double avg, String level) {
        setTitle("📄 Học bạ của " + student.getName());
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- Thông tin học sinh ---
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("🧑 Thông tin học sinh"));

infoPanel.add(new JLabel("Họ tên: " + student.getName()));
infoPanel.add(new JLabel("Mã số HS: " + student.getId()));

infoPanel.add(new JLabel("Lớp: " + student.getClassId()));
infoPanel.add(new JLabel("Năm học: " + student.getSchoolYear()));

infoPanel.add(new JLabel("Giới tính: " + student.getGender()));
infoPanel.add(new JLabel("GVCN: " + student.getHomeroomTeacher()));

infoPanel.add(new JLabel("Dân tộc: " + student.getNation()));
infoPanel.add(new JLabel("Ngày sinh: " + student.getDate()));


infoPanel.add(new JLabel("Nơi ở: " + student.getPlaceLive()));

infoPanel.add(new JLabel("Phụ huynh: " + student.getParent()));
infoPanel.add(new JLabel("<html>Nơi sinh: " + student.getPlaceBirth() + "</html>"));
        // --- Bảng điểm ---
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("📘 Bảng điểm"));

        // Header bảng
        String[] columnNames = {"Môn học", "Điểm"};
        String[][] data = new String[SUBJECTS.length][2];

        Map<String, Score> scoreMap = scores.stream()
                .collect(Collectors.toMap(Score::getSubject, s -> s));

        for (int i = 0; i < SUBJECTS.length; i++) {
            data[i][0] = SUBJECTS[i];
            data[i][1] = scoreMap.containsKey(SUBJECTS[i])
                    ? String.valueOf(scoreMap.get(SUBJECTS[i]).getScore()) : "Chưa nhập điểm";
        }

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false);
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        scorePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Kết quả tổng ---
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblAvg = new JLabel(String.format("Điểm trung bình: %.2f", avg), SwingConstants.CENTER);
        JLabel lblLevel = new JLabel("Xếp loại: " + level, SwingConstants.CENTER);
        lblAvg.setFont(new Font("Arial", Font.BOLD, 14));
        lblLevel.setFont(new Font("Arial", Font.BOLD, 14));

        resultPanel.add(lblAvg);
        resultPanel.add(lblLevel);

        // --- Gộp lại ---
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scorePanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}
