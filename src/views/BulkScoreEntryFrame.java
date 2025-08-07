package views;

import controllers.ClassController;
import controllers.ScoreController;
import controllers.StudentController;
import models.ClassRoom;
import models.Score;
import models.Student;
import utils.CSVHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class BulkScoreEntryFrame extends JFrame {
    private JComboBox<ClassRoom> cbClass;
    private JPanel scorePanel;
    private final StudentController studentController;
    private final ScoreController scoreController;

    private final String[] subjects = {
            "Ngữ văn", "Toán", "Ngoại ngữ 1", "Giáo dục thể chất",
            "Giáo dục QP-AN", "Lịch sử", "Địa lý", "Hóa học",
            "Sinh học", "Vật lý", "Tiếng dân tộc", "Ngoại ngữ 2"
    };

    public BulkScoreEntryFrame(StudentController studentController, ScoreController scoreController,
            ClassController classController) {
        this.studentController = studentController;
        this.scoreController = scoreController;

        setTitle("📝 Nhập điểm theo lớp");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cbClass = new JComboBox<>(classController.getAllClasses().toArray(new ClassRoom[0]));
        cbClass.addActionListener(e -> loadStudents());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Chọn lớp: "));
        topPanel.add(cbClass);

        scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(scorePanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JButton btnSave = new JButton("💾 Lưu điểm");
        btnSave.addActionListener(e -> saveScores());
        JButton btnExport = new JButton("📤 Xuất bảng điểm");
        btnExport.addActionListener(e -> exportScoresByClass());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnSave);
        bottomPanel.add(btnExport);
        add(bottomPanel, BorderLayout.SOUTH);

        loadStudents();
    }

    private void loadStudents() {
        scorePanel.removeAll();

        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (selectedClass == null)
            return;

        List<Student> students = studentController.filterByClass(selectedClass.getId());

        // Tiêu đề cột
        JPanel headerPanel = new JPanel(new GridLayout(1, subjects.length + 1, 10, 10));
        headerPanel.add(new JLabel("👤 Học sinh", SwingConstants.CENTER));
        for (String subject : subjects) {
            JLabel lbl = new JLabel(subject, SwingConstants.CENTER);
            headerPanel.add(lbl);
        }
        scorePanel.add(headerPanel);

        // Dữ liệu học sinh
        for (Student student : students) {
            JPanel studentPanel = new JPanel(new GridLayout(1, subjects.length + 1, 10, 10));
            studentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            Map<String, JTextField> fields = new HashMap<>();
            JLabel nameLabel = new JLabel(student.getId() + " - " + student.getName());
            studentPanel.add(nameLabel);

            for (String subject : subjects) {
                JTextField tf = new JTextField(5);
                fields.put(subject, tf);
                studentPanel.add(tf);
            }

            studentPanel.putClientProperty("student", student);
            studentPanel.putClientProperty("fields", fields);
            scorePanel.add(studentPanel);
        }

        scorePanel.revalidate();
        scorePanel.repaint();
    }

    private void saveScores() {
        Component[] components = scorePanel.getComponents();
        int errorCount = 0;

        // Bỏ dòng đầu là tiêu đề
        for (int i = 1; i < components.length; i++) {
            Component comp = components[i];
            if (comp instanceof JPanel panel) {
                Student student = (Student) panel.getClientProperty("student");
                Map<String, JTextField> fields = (Map<String, JTextField>) panel.getClientProperty("fields");

                for (String subject : subjects) {
                    String text = fields.get(subject).getText().trim();
                    if (!text.isEmpty()) {
                        try {
                            double score = Double.parseDouble(text);
                            if (score < 0 || score > 10) {
                                throw new NumberFormatException();
                            }
                            Score newScore = new Score(student.getId(), subject, score);
                            scoreController.addOrUpdateScore(newScore);
                        } catch (NumberFormatException ex) {
                            errorCount++;
                            JOptionPane.showMessageDialog(this,
                                    "❌ Điểm không hợp lệ cho " + student.getName() + " - " + subject + ": " + text
                                            + "\nChỉ nhập số từ 0 đến 10.",
                                    "Lỗi nhập điểm", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        }

        if (errorCount == 0) {
            JOptionPane.showMessageDialog(this, "✅ Lưu điểm thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ Có " + errorCount + " lỗi khi nhập điểm. Vui lòng kiểm tra lại.");
        }
    }

    private void exportScoresByClass() {
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (selectedClass == null)
            return;

        List<Student> students = studentController.filterByClass(selectedClass.getId());
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Không có học sinh nào trong lớp này.");
            return;
        }

        List<String[]> rows = new ArrayList<>();
        List<String> header = new ArrayList<>();
        header.add("Mã HS");
        header.add("Họ tên");
        Collections.addAll(header, subjects);
        rows.add(header.toArray(new String[0]));

        for (Student student : students) {
            List<String> row = new ArrayList<>();
            row.add(student.getId());
            row.add(student.getName());

            for (String subject : subjects) {
                Score s = scoreController.getScore(student.getId(), subject);
                row.add(s != null ? String.valueOf(s.getScore()) : "");
            }

            rows.add(row.toArray(new String[0]));
        }

        // Tạo thư mục nếu chưa có
        File exportDir = new File("exports/scoreTable");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        // Xuất file vào thư mục đã tạo
        String filename = "exports/scoreTable/BangDiem_" + selectedClass.getId() + ".csv";
        boolean success = CSVHelper.writeCSV(filename, rows);

        if (success) {
            JOptionPane.showMessageDialog(this, "✅ Xuất bảng điểm thành công:\n" + filename);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Lỗi khi xuất bảng điểm.");
        }
    }

}
