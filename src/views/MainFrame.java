package views;

import controllers.ClassController;
import controllers.StudentController;
import models.ClassRoom;
import models.Student;
import utils.CSVHelper;
import utils.FilePath;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.List;

public class MainFrame extends JFrame {
    private static final String STUDENT_CSV = FilePath.STUDENT_CSV;
    private static final String CLASS_CSV = FilePath.CLASS_CSV;

    private final StudentController studentController = new StudentController();
    private final ClassController classController = new ClassController();
    private final DefaultComboBoxModel<ClassRoom> classModel = new DefaultComboBoxModel<>();

    private final JTextField txtName = new JTextField(15);
    private final JComboBox<ClassRoom> cbClass = new JComboBox<>(classModel);
    private final JTextField txtTeacher = new JTextField(15);

    private final StudentTable tableModel = new StudentTable(studentController.getAllStudents());
    private final JTable table = new JTable(tableModel);

    public MainFrame() {
        // Tạo thư mục data nếu chưa có
        new java.io.File("data").mkdirs();
        // Load dữ liệu lớp và học sinh từ CSV
        // classController.getAllClasses().clear();
        // classController.getAllClasses().addAll(CSVHelper.readClassesFromCSV(CLASS_CSV));
        studentController.getAllStudents().addAll(CSVHelper.readStudentsFromCSV(STUDENT_CSV));
        // Đồng bộ JComboBox lớp
        rebuildClassCombo();

        setTitle("Quản lý học sinh");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // Tab học sinh
        tabs.addTab("Học sinh", createStudentPanel());

        // Tab lớp học
        ClassPanel classPanel = new ClassPanel(classController, classModel);
        // Khi thêm lớp, lưu CSV và cập nhật combo
        classPanel.setOnClassAdded(() -> {
            CSVHelper.writeClassesToCSV(classController.getAllClasses(), CLASS_CSV);
            rebuildClassCombo();
        });
        tabs.addTab("Lớp học", classPanel);

        add(tabs);
        setVisible(true);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder("Thêm học sinh"));

        form.add(new JLabel("Họ tên:"));
        form.add(txtName);

        form.add(new JLabel("Lớp:"));
        cbClass.addActionListener(e -> {
            ClassRoom selected = (ClassRoom) cbClass.getSelectedItem();
            txtTeacher.setText(selected != null ? selected.getTeacher() : "");
        });
        form.add(cbClass);

        form.add(new JLabel("GVCN:"));
        txtTeacher.setEditable(false);
        form.add(txtTeacher);

        JButton btnAdd = new JButton("Thêm học sinh");
        btnAdd.addActionListener(e -> addStudent());
        form.add(btnAdd);

        panel.add(form, BorderLayout.EAST);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(22);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void addStudent() {
        String name = txtName.getText().trim();
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (name.isEmpty() || selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin");
            return;
        }

        int year = Year.now().getValue();
        String schoolYear = year + "-" + (year + 1);
        String id = generateStudentId(selectedClass.getName(), year);

        Student s = new Student(name, id, selectedClass.getName(), schoolYear, selectedClass.getTeacher());
        studentController.addStudent(s);

        // Lưu học sinh ra CSV
        CSVHelper.writeStudentsToCSV(studentController.getAllStudents(), STUDENT_CSV);

        tableModel.setStudents(studentController.getAllStudents());
        txtName.setText("");
    }

    private void rebuildClassCombo() {
        classModel.removeAllElements();
        for (ClassRoom c : classController.getAllClasses()) {
            classModel.addElement(c);
        }
    }

    private String generateStudentId(String classId, int year) {
        long count = studentController.getAllStudents().stream()
                .filter(s -> s.getClassId().equals(classId))
                .count() + 1;
        String yearCode = String.format("%03d", year % 1000);
        String countCode = String.format("%03d", count);
        return "HS" + yearCode + countCode;
    }
}