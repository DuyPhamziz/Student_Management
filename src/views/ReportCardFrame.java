package views;

import models.Score;
import models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportCardFrame extends JFrame {
    public ReportCardFrame(Student student, List<Score> scores, double avg, String level) {
        setTitle("Học bạ của " + student.getName());
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setText("Tên: " + student.getName() +
                "\nMã số: " + student.getId() +
                "\nLớp: " + student.getClassId() +
                "\nNăm học: " + student.getSchoolYear() +
                "\nGVCN: " + student.getHomeroomTeacher());
        panel.add(info, BorderLayout.NORTH);

        String[] columnNames = {"Môn học", "Điểm"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (Score sc : scores) {
            tableModel.addRow(new Object[]{sc.getSubject(), sc.getScore()});
        }

        JTable table = new JTable(tableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel lblAvg = new JLabel(String.format("Trung bình: %.2f - Xếp loại: %s", avg, level));
        lblAvg.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblAvg, BorderLayout.SOUTH);

        add(panel);
    }
}
