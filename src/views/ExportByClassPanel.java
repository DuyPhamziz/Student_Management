package views;

import controllers.ClassController;
import controllers.StudentController;
import models.ClassRoom;
import models.Student;
import utils.CSVHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ExportByClassPanel extends JPanel {
    private final JComboBox<ClassRoom> cbClass = new JComboBox<>();
    private final JButton btnExport = new JButton("Xuất danh sách");
    private final JTable table = new JTable();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final StudentController studentController;

    public ExportByClassPanel(ClassController classController, StudentController studentController) {
        this.studentController = studentController;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Xuất danh sách học sinh theo lớp"));

        // --- Top panel ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Chọn lớp:"));

        for (ClassRoom c : classController.getAllClasses()) {
            cbClass.addItem(c);
        }

        topPanel.add(cbClass);
        topPanel.add(btnExport);
        add(topPanel, BorderLayout.NORTH);

        // --- Table setup ---
        table.setModel(tableModel);
        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Events ---
        cbClass.addActionListener(e -> updateTable());
        btnExport.addActionListener(e -> exportStudentsByClass());

        // --- Initial display ---
        updateTable();
    }

    private void updateTable() {
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (selectedClass == null) return;

        String className = selectedClass.getName();

        List<Student> students = studentController.getAllStudents().stream()
                .filter(s -> s.getClassId().equals(className))
                .collect(Collectors.toList());

        // Set table headers
        tableModel.setColumnIdentifiers(new Object[] { "Mã số", "Họ tên", "Giới tính", "Năm học", "GVCN" });

        // Clear old data
        tableModel.setRowCount(0);

        // Add rows
        for (Student s : students) {
            tableModel.addRow(new Object[] {
                    s.getId(),
                    s.getName(),
                    s.getGender(),
                    s.getSchoolYear(),
                    s.getHomeroomTeacher()
            });
        }
    }

    private void exportStudentsByClass() {
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp.");
            return;
        }

        String className = selectedClass.getName();

        List<Student> students = studentController.getAllStudents().stream()
                .filter(s -> s.getClassId().equals(className))
                .collect(Collectors.toList());

        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có học sinh trong lớp " + className);
            return;
        }

        File exportDir = new File("exports");
        if (!exportDir.exists()) exportDir.mkdirs();

        String filename = "exports/" + className + ".csv";
        CSVHelper.writeStudentsToCSV(students, filename);

        JOptionPane.showMessageDialog(this, "Đã xuất danh sách lớp " + className + " ra file:\n" + filename);
    }
}
