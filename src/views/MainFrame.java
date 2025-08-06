package views;

import controllers.ClassController;
import controllers.StudentController;
import controllers.TeacherController;
import models.ClassRoom;
import models.Student;
import models.Teacher;
import utils.CSVHelper;
import utils.FilePath;

import javax.swing.*;
import java.awt.*;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final StudentTable tableModel = new StudentTable(studentController.getAllStudents());
    private final JTable table = new JTable(tableModel);

    private Map<String, Teacher> teacherMap = new HashMap<>();

    public MainFrame() {
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
        studentController.getAllStudents().clear();
        studentController.getAllStudents().addAll(students);

        rebuildClassCombo();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Học sinh", createStudentPanel());
        tabs.addTab("Lớp học", new ClassPanel(classController, teacherController));
        tabs.addTab("Giáo viên", new TeacherPanel(teacherController));

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
        cbClass.addActionListener(_ -> updateTeacherField());
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

        btnAdd.addActionListener(_ -> addStudent());
        btnEdit.addActionListener(_ -> editStudent());
        btnDelete.addActionListener(_ -> deleteStudent());

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(_ -> updateFormFromTable());

        panel.add(form, BorderLayout.EAST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

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
        if (index == -1)
            return;

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
    
}

