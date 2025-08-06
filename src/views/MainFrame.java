package views;

import controllers.ClassController;
import controllers.ScoreController;
import controllers.StudentController;
import java.awt.*;
import java.time.Year;
import java.util.List;
import javax.swing.*;
import models.ClassRoom;
import models.Score;
import models.Student;
import utils.CSVHelper;
import utils.FilePath;




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

        new java.io.File("data").mkdirs();

        studentController.getAllStudents().addAll(CSVHelper.readStudentsFromCSV(STUDENT_CSV));

        rebuildClassCombo();

        setTitle("Quản lý học sinh");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        ScoreController scoreController = new ScoreController();

        tabs.addTab("Nhập điểm", createScorePanel(scoreController));
        tabs.addTab("Học sinh", createStudentPanel());


        ClassPanel classPanel = new ClassPanel(classController, classModel);

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

    private JPanel createScorePanel(ScoreController scoreController) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        JComboBox<Student> cbStudent = new JComboBox<>(new DefaultComboBoxModel<>(studentController.getAllStudents().toArray(new Student[0])));
        JTextField txtSubject = new JTextField();
        JTextField txtScore = new JTextField();
        JLabel lblResult = new JLabel(" ");

        form.setBorder(BorderFactory.createTitledBorder("Nhập điểm học sinh"));
        form.add(new JLabel("Chọn học sinh:"));
        form.add(cbStudent);
        form.add(new JLabel("Môn học:"));
        form.add(txtSubject);
        form.add(new JLabel("Điểm:"));
        form.add(txtScore);

        JButton btnAdd = new JButton("Lưu điểm");
        form.add(btnAdd);

        JButton btnReport = new JButton("Xem học bạ");
        form.add(btnReport);

        panel.add(form, BorderLayout.NORTH);
        panel.add(lblResult, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            String subject = txtSubject.getText().trim();
            double score;

            try {
                score = Double.parseDouble(txtScore.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Điểm không hợp lệ");
                return;
            }

            if (s == null || subject.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn học sinh và nhập môn học");
                return;
            }

            scoreController.addScore(new Score(s.getId(), subject, score));
            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());
            lblResult.setText(String.format("TB: %.2f - Xếp loại: %s", avg, level));

            txtSubject.setText("");
            txtScore.setText("");
        });

        btnReport.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(panel, "Chưa chọn học sinh");
                return;
            }
            List<Score> scores = scoreController.getScoresByStudent(s.getId());
            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());

            ReportCardFrame frame = new ReportCardFrame(s, scores, avg, level);
            frame.setVisible(true);
        });

        return panel;
    }
}
