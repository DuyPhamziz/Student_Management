package views;

import controllers.ClassController;
import controllers.ScoreController;
import controllers.StudentController;
import controllers.TeacherController;
import java.awt.*;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import models.ClassRoom;
import models.Score;
import models.Student;
import models.Teacher;
import utils.CSVHelper;
import utils.FilePath;
import java.util.Comparator;

public class MainFrame extends JFrame {

    private static final String STUDENT_CSV = FilePath.STUDENT_CSV;
    private static final String CLASS_CSV = FilePath.CLASS_CSV;

    private final StudentController studentController = new StudentController();
    private final ClassController classController = new ClassController();
    private final TeacherController teacherController = new TeacherController();

    private final DefaultComboBoxModel<ClassRoom> classModel = new DefaultComboBoxModel<>();

    private final JTextField txtName = new JTextField(15);
    private final JComboBox<ClassRoom> cbClass = new JComboBox<>(classModel);
    private final JComboBox<String> cbGender = new JComboBox<>(new String[] { "Nam", "Nữ" });
    private final JTextField txtTeacher = new JTextField(15);
    private final JButton btnAdd = new JButton("Thêm");
    private final JButton btnEdit = new JButton("Sửa");
    private final JButton btnDelete = new JButton("Xóa");
    private final JTextField txtSearchId = new JTextField(15);
    private final JButton btnSearchId = new JButton("Tìm theo MSSV");
    private final JTextField txtSearchName = new JTextField(15);
    private final JButton btnSearchName = new JButton("Tìm theo Họ tên");
    private final JButton btnReset = new JButton("Hiển thị tất cả");
    private final JComboBox<String> cbFilterGender = new JComboBox<>(new String[] { "Tất cả", "Nam", "Nữ" });
    private final JComboBox<ClassRoom> cbFilterClass = new JComboBox<>();
    private final JComboBox<Teacher> cbFilterTeacher = new JComboBox<>();
    private final JButton btnFilterGender = new JButton("Lọc theo giới tính");
    private final JButton btnFilterClass = new JButton("Lọc theo lớp");
    private final JButton btnFilterTeacher = new JButton("Lọc theo GVCN");

    private static final String[] SUBJECTS = {
            "Ngữ văn", "Toán", "Ngoại ngữ 1", "Giáo dục thể chất",
            "Giáo dục QP-AN", "Lịch sử", "Địa lý", "Hóa học",
            "Sinh học", "Vật lý", "Tiếng dân tộc", "Ngoại ngữ 2"
    };

    private final JComboBox<String> cbSubject = new JComboBox<>(SUBJECTS);

    private final StudentTable tableModel = new StudentTable(studentController.getAllStudents());
    private final JTable table = new JTable(tableModel);

    private Map<String, Teacher> teacherMap = new HashMap<>();

    public MainFrame() {

        new java.io.File("data").mkdirs();

        studentController.getAllStudents().addAll(CSVHelper.readStudentsFromCSV(STUDENT_CSV));

        rebuildClassCombo();

        setTitle("Quản lý học sinh");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        loadData();
        buildUI();
    }

    private void loadData() {
        teacherMap.clear();
        for (Teacher t : teacherController.getAllTeachers()) {
            teacherMap.put(t.getId(), t);
        }
        ClassRoom.setTeacherController(teacherController);

        List<ClassRoom> classes = CSVHelper.readClassesFromCSV(CLASS_CSV);
        classController.getAllClasses().clear();
        classController.getAllClasses().addAll(classes);

        List<Student> students = CSVHelper.readStudentsFromCSV(STUDENT_CSV);
        students.sort(Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER));
        studentController.getAllStudents().clear();
        studentController.getAllStudents().addAll(students);

        rebuildClassCombo();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        ScoreController scoreController = new ScoreController();

        tabs.addTab("Nhập điểm", createScorePanel(scoreController));
        tabs.addTab("Học sinh", createStudentPanel());
        tabs.addTab("Lớp học", new ClassPanel(classController, teacherController));
        tabs.addTab("Giáo viên", new TeacherPanel(teacherController));
        tabs.addTab("Xuất theo lớp", new ExportByClassPanel(classController, studentController));


        add(tabs);
        setVisible(true);
    }

    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin học sinh"));

        form.add(new JLabel("Họ tên:"));
        form.add(txtName);

        form.add(new JLabel("Lớp:"));
        cbClass.addActionListener(e -> updateTeacherField());
        form.add(cbClass);

        form.add(new JLabel("Giới tính:"));
        form.add(cbGender);

        form.add(new JLabel("GVCN:"));
        txtTeacher.setEditable(false);
        form.add(txtTeacher);

        JPanel btns = new JPanel();
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);

        form.add(btns);

        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnSearchId.addActionListener(e -> {
            String keyword = txtSearchId.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập mã số học sinh để tìm kiếm.");
                return;
            }

            List<Student> results = studentController.searchStudentsById(keyword);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy học sinh có mã chứa: " + keyword);
            } else {
                table.setModel(new StudentTable(results));
            }
        });

        btnSearchName.addActionListener(e -> {
            String name = txtSearchName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập tên để tìm kiếm.");
                return;
            }

            List<Student> results = studentController.searchStudentsByName(name);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy học sinh có tên chứa: " + name);
            } else {
                table.setModel(new StudentTable(results));
            }
        });

        btnReset.addActionListener(e -> {
            table.setModel(new StudentTable(studentController.getAllStudents()));
        });

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateFormFromTable());
        // Tạo panel chính chứa các dòng tìm kiếm
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm học sinh"));

        // Dòng tìm theo MSSV
        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Nhập mã số học sinh:"));
        line1.add(txtSearchId);
        line1.add(btnSearchId);

        // Dòng tìm theo tên
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("Nhập họ tên học sinh:"));
        line2.add(txtSearchName);
        line2.add(btnSearchName);

        // Nút hiển thị tất cả
        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.add(btnReset);

        // Thêm các dòng vào panel chính
        searchPanel.add(line1);
        searchPanel.add(line2);
        searchPanel.add(line3);

        // Thêm toàn bộ khung tìm kiếm vào bên trên bảng
        panel.add(searchPanel, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Phân loại học sinh"));
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        filterPanel.add(new JLabel("Giới tính:"));
        filterPanel.add(cbFilterGender);
        filterPanel.add(btnFilterGender);

        filterPanel.add(new JLabel("Lớp:"));
        cbFilterClass.removeAllItems();
        
        cbFilterClass.addItem(null); // Cho phép chọn "Tất cả"
        for (ClassRoom c : classController.getAllClasses())
            cbFilterClass.addItem(new ClassRoom(c.getName(), c.getTeacherId()) {
                @Override
                public String toString() {
                    return getDisplayNameOnly(); // chỉ hiển thị tên lớp
                }
            });
        filterPanel.add(cbFilterClass);
        filterPanel.add(btnFilterClass);

        filterPanel.add(new JLabel("GVCN:"));
        cbFilterTeacher.removeAllItems();
        cbFilterTeacher.addItem(null);
        for (Teacher t : teacherController.getAllTeachers())
            cbFilterTeacher.addItem(t);
        filterPanel.add(cbFilterTeacher);
        filterPanel.add(btnFilterTeacher);

        // Thêm vào panel:
        panel.add(filterPanel, BorderLayout.SOUTH);

        panel.add(form, BorderLayout.EAST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        btnFilterGender.addActionListener(e -> {
            String gender = (String) cbFilterGender.getSelectedItem();
            if (gender == null || gender.equals("Tất cả")) {
                table.setModel(new StudentTable(studentController.getAllStudents()));
            } else {
                table.setModel(new StudentTable(studentController.filterByGender(gender)));
            }
        });

        btnFilterClass.addActionListener(e -> {
            ClassRoom selectedClass = (ClassRoom) cbFilterClass.getSelectedItem();
            if (selectedClass == null) {
                table.setModel(new StudentTable(studentController.getAllStudents()));
            } else {
                List<Student> filtered = studentController.getAllStudents().stream()
                        .filter(s -> s.getClassId().equals(selectedClass.getName()))
                        .toList();
                table.setModel(new StudentTable(filtered));
            }
        });

        btnFilterTeacher.addActionListener(e -> {
            Teacher selectedTeacher = (Teacher) cbFilterTeacher.getSelectedItem();
            if (selectedTeacher == null) {
                table.setModel(new StudentTable(studentController.getAllStudents()));
            } else {
                List<Student> filtered = studentController.getAllStudents().stream()
                        .filter(s -> s.getHomeroomTeacher().equalsIgnoreCase(selectedTeacher.getName()))
                        .toList();
                table.setModel(new StudentTable(filtered));
            }
        });

        return panel;
    }

    private void updateTeacherField() {
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (selectedClass != null) {
            Teacher t = teacherMap.get(selectedClass.getTeacherId());
            txtTeacher.setText(t != null ? t.getName() : "");
        } else {
            txtTeacher.setText("");
        }
    }

    private void rebuildClassCombo() {
        classModel.removeAllElements();
        for (ClassRoom c : classController.getAllClasses()) {
            classModel.addElement(c);
        }
    }

    private void addStudent() {
        String name = txtName.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (name.isEmpty() || selectedClass == null || gender == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int year = Year.now().getValue();
        String schoolYear = year + "-" + (year + 1);
        String id = generateStudentId(selectedClass.getName(), year);

        Teacher t = teacherMap.get(selectedClass.getTeacherId());
        Student s = new Student(name, id, selectedClass.getName(), schoolYear, t != null ? t.getName() : "", gender);
        s.setGender(gender);

        studentController.addStudent(s);
        CSVHelper.writeStudentsToCSV(studentController.getAllStudents(), STUDENT_CSV);
        tableModel.fireTableDataChanged();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm học sinh thành công.");
    }

    private void editStudent() {
        int index = table.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn học sinh để sửa.");
            return;
        }

        String name = txtName.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        ClassRoom selectedClass = (ClassRoom) cbClass.getSelectedItem();
        if (name.isEmpty() || selectedClass == null || gender == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        Student s = studentController.getAllStudents().get(index);
        s.setName(name);
        s.setGender(gender);
        s.setClassId(selectedClass.getName());
        s.setHomeroomTeacher(teacherMap.get(selectedClass.getTeacherId()).getName());

        CSVHelper.writeStudentsToCSV(studentController.getAllStudents(), STUDENT_CSV);
        tableModel.fireTableDataChanged();
        clearForm();
        JOptionPane.showMessageDialog(this, "Sửa học sinh thành công.");
    }

    private void deleteStudent() {
        int index = table.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn học sinh để xóa.");
            return;
        }

        studentController.getAllStudents().remove(index);
        CSVHelper.writeStudentsToCSV(studentController.getAllStudents(), STUDENT_CSV);
        tableModel.fireTableDataChanged();
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa học sinh thành công.");
    }

    private void updateFormFromTable() {
        int index = table.getSelectedRow();
        if (index == -1) {
            return;
        }

        Student s = studentController.getAllStudents().get(index);
        txtName.setText(s.getName());
        cbGender.setSelectedItem(s.getGender());

        for (int i = 0; i < cbClass.getItemCount(); i++) {
            if (cbClass.getItemAt(i).getName().equals(s.getClassId())) {
                cbClass.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        cbClass.setSelectedIndex(-1);
        cbGender.setSelectedIndex(-1);
        txtTeacher.setText("");
        table.clearSelection();
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

        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));

        JComboBox<Student> cbStudent = new JComboBox<>(
                new DefaultComboBoxModel<>(studentController.getAllStudents().toArray(new Student[0])));

        JTextField txtScore = new JTextField();
        JLabel lblResult = new JLabel(" ");

        form.setBorder(BorderFactory.createTitledBorder("Nhập điểm học sinh"));
        form.add(new JLabel("Chọn học sinh:"));
        form.add(cbStudent);
        form.add(new JLabel("Môn học:"));
        form.add(cbSubject);
        form.add(new JLabel("Điểm:"));
        form.add(txtScore);

        JButton btnReport = new JButton("Xem học bạ");
        form.add(btnReport);

        JButton btnAdd = new JButton("Lưu điểm");
        form.add(btnAdd);

        panel.add(form, BorderLayout.NORTH);
        panel.add(lblResult, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            String subject = (String) cbSubject.getSelectedItem();
            double score;

            // Kiểm tra điểm hợp lệ
            try {
                score = Double.parseDouble(txtScore.getText().trim());
                if (score < 0 || score > 10) {
                    JOptionPane.showMessageDialog(panel, "Điểm phải từ 0 đến 10.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Điểm không hợp lệ.");
                return;
            }

            if (s == null || subject == null || subject.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn học sinh và môn học.");
                return;
            }

            List<Score> existingScores = scoreController.getScoresByStudentId(s.getId());
            Score existing = existingScores.stream()
                    .filter(sc -> sc.getSubject().equalsIgnoreCase(subject))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                int choice = JOptionPane.showOptionDialog(panel,
                        "Môn học này đã được nhập điểm trước đó.\nBạn có muốn thay đổi điểm không?",
                        "Điểm đã tồn tại",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[] { "Thay đổi", "Hủy bỏ" },
                        "Hủy bỏ");

                if (choice == JOptionPane.YES_OPTION) {
                    // Cập nhật điểm
                    existing.setScore(score);
                    JOptionPane.showMessageDialog(panel, "Đã cập nhật điểm mới cho môn học.");
                } else {
                    return;
                }
            } else {
                // Thêm mới
                scoreController.addScore(new Score(s.getId(), subject, score));
                JOptionPane.showMessageDialog(panel, "Đã lưu điểm thành công.");
            }

            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());
            lblResult.setText(String.format("TB: %.2f - Xếp loại: %s", avg, level));

            txtScore.setText("");
            cbSubject.setSelectedIndex(0);
        });

        btnReport.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(panel, "Chưa chọn học sinh");
                return;
            }
            List<Score> scores = scoreController.getScoresByStudentId(s.getId());
            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());

            ReportCardFrame frame = new ReportCardFrame(s, scores, avg, level);
            frame.setVisible(true);
        });

        return panel;
    }

}
