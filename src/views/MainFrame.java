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
import java.util.Optional;

import javax.swing.*;
import models.ClassRoom;
import models.Score;
import models.Student;
import models.Teacher;
import utils.CSVHelper;
import utils.FilePath;
import java.util.Comparator;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

public class MainFrame extends JFrame {

    private static final String STUDENT_CSV = FilePath.STUDENT_CSV;
    private static final String CLASS_CSV = FilePath.CLASS_CSV;

    private final StudentController studentController = new StudentController();
    private final ClassController classController = new ClassController();
    private final TeacherController teacherController = new TeacherController();

    private final DefaultComboBoxModel<ClassRoom> classModel = new DefaultComboBoxModel<>();

    private final JTextField txtName = new JTextField(15);
    private final JTextField txtNation = new JTextField(15);
    private final JTextField txtDate = new JTextField(15);
    private final JTextField txtPlaceBirth = new JTextField(15);
    private final JTextField txtPlaceLive = new JTextField(15);
    private final JTextField txtParent = new JTextField(15);

    private final JComboBox<ClassRoom> cbClass = new JComboBox<>(classModel);
    private final JComboBox<String> cbGender = new JComboBox<>(new String[]{"Nam", "Nữ"});
    private final JTextField txtTeacher = new JTextField(15);
    private final JButton btnAdd = new JButton("Thêm");
    private final JButton btnEdit = new JButton("Sửa");
    private final JButton btnDelete = new JButton("Xóa");
    private final JTextField txtSearchId = new JTextField(15);
    private final JButton btnSearchId = new JButton("Tìm theo Mã HS");
    private final JTextField txtSearchName = new JTextField(15);
    private final JButton btnSearchName = new JButton("Tìm theo Họ tên");
    private final JButton btnReset = new JButton("Hiển thị tất cả");
    private final JComboBox<String> cbFilterGender = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ"});
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
        cbFilterClass.removeAllItems();
        for (ClassRoom c : classController.getAllClasses()) {
            cbFilterClass.addItem(c);
        }

        cbFilterTeacher.removeAllItems();
        for (Teacher t : teacherController.getAllTeachers()) {
            cbFilterTeacher.addItem(t);
        }
        buildUI();
    }   
    private void refreshStudentComboBox(JComboBox<Student> cbStudent) {
    cbStudent.setModel(new DefaultComboBoxModel<>(
        studentController.getAllStudents().toArray(new Student[0])
    ));
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- FORM BÊN TRÁI ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Nhập thông tin học sinh"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Tên học sinh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Giới tính:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbGender, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Lớp học:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbClass, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("GVCN:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtTeacher, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Dân tộc:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNation, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Ngày sinh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDate, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nơi sinh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPlaceBirth, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Nơi ở hiện tại:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPlaceLive, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Phụ huynh:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtParent, gbc);
        row++;

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        // --- SEARCH PANEL ---
        JPanel searchPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm học sinh"));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Mã HS:"));
        line1.add(txtSearchId);
        line1.add(btnSearchId);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("Họ tên:"));
        line2.add(txtSearchName);
        line2.add(btnSearchName);

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.add(btnReset);

        searchPanel.add(line1);
        searchPanel.add(line2);
        searchPanel.add(line3);

        // --- FILTER PANEL ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Lọc học sinh"));

        filterPanel.add(new JLabel("Giới tính:"));
        filterPanel.add(cbFilterGender);
        filterPanel.add(btnFilterGender);

        filterPanel.add(new JLabel("Lớp:"));
        filterPanel.add(cbFilterClass);
        filterPanel.add(btnFilterClass);

        filterPanel.add(new JLabel("GVCN:"));
        filterPanel.add(cbFilterTeacher);
        filterPanel.add(btnFilterTeacher);

        // --- TABLE ---
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(table);

        // --- COMBINE SEARCH + FILTER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        // --- TỔNG HỢP ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, tableScroll);
        splitPane.setResizeWeight(0.3); // 30% cho form, 70% cho bảng

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        // ====== SỰ KIỆN TÌM KIẾM ======
        btnSearchId.addActionListener(e -> {
            String searchId = txtSearchId.getText().trim().toLowerCase();
            if (searchId.isEmpty())
                return;

            List<Student> result = studentController.getAllStudents().stream()
                    .filter(s -> s.getId().toLowerCase().contains(searchId))
                    .toList();

            tableModel.setStudents(result);
        });

        btnSearchName.addActionListener(e -> {
            String searchName = txtSearchName.getText().trim().toLowerCase();
            if (searchName.isEmpty())
                return;

            List<Student> result = studentController.getAllStudents().stream()
                    .filter(s -> s.getName().toLowerCase().contains(searchName))
                    .toList();

            tableModel.setStudents(result);
        });

        btnReset.addActionListener(e -> {
            tableModel.setStudents(studentController.getAllStudents());
        });
        btnFilterGender.addActionListener(e -> {
            String selectedGender = (String) cbFilterGender.getSelectedItem();
            if ("Tất cả".equals(selectedGender)) {
                tableModel.setStudents(studentController.getAllStudents());
            } else {
                List<Student> filtered = studentController.getAllStudents().stream()
                        .filter(s -> s.getGender().equalsIgnoreCase(selectedGender))
                        .toList();
                tableModel.setStudents(filtered);
            }
        });
        btnFilterClass.addActionListener(e -> {
            ClassRoom selectedClass = (ClassRoom) cbFilterClass.getSelectedItem();
            if (selectedClass == null)
                return;

            String classId = selectedClass.getName();
            List<Student> filtered = studentController.getAllStudents().stream()
                    .filter(s -> s.getClassId().equals(classId))
                    .toList();
            tableModel.setStudents(filtered);
        });
        btnFilterTeacher.addActionListener(e -> {
            Teacher selectedTeacher = (Teacher) cbFilterTeacher.getSelectedItem();
            if (selectedTeacher == null)
                return;

            String teacherName = selectedTeacher.getName();
            List<Student> filtered = studentController.getAllStudents().stream()
                    .filter(s -> s.getHomeroomTeacher().equals(teacherName))
                    .toList();
            tableModel.setStudents(filtered);
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
        String nation = txtNation.getText().trim();
        String date = txtDate.getText().trim();
        String placeBirth = txtPlaceBirth.getText().trim();
        String placeLive = txtPlaceLive.getText().trim();
        String parent = txtParent.getText().trim();

        if (name.isEmpty() || selectedClass == null || gender == null || nation.isEmpty() || date.isEmpty()
                || placeBirth.isEmpty() || placeLive.isEmpty() || parent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        int year = Year.now().getValue();
        String schoolYear = year + "-" + (year + 1);
        String id = generateStudentId(selectedClass.getName(), year);

        Teacher t = teacherMap.get(selectedClass.getTeacherId());
        String homeroomTeacher = t != null ? t.getName() : "";

        Student s = new Student(name, id, selectedClass.getName(), schoolYear, homeroomTeacher, gender,
                nation, date, placeBirth, placeLive, parent);

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
        String nation = txtNation.getText().trim();
        String date = txtDate.getText().trim();
        String placeBirth = txtPlaceBirth.getText().trim();
        String placeLive = txtPlaceLive.getText().trim();
        String parent = txtParent.getText().trim();

        if (name.isEmpty() || selectedClass == null || gender == null || nation.isEmpty() || date.isEmpty()
                || placeBirth.isEmpty() || placeLive.isEmpty() || parent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        Student s = studentController.getAllStudents().get(index);
        s.setName(name);
        s.setGender(gender);
        s.setClassId(selectedClass.getName());
        s.setHomeroomTeacher(teacherMap.get(selectedClass.getTeacherId()).getName());
        s.setNation(nation);
        s.setDate(date);
        s.setPlaceBirth(placeBirth);
        s.setPlaceLive(placeLive);
        s.setParent(parent);

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

        String gender = s.getGender().trim();
        if (gender.equalsIgnoreCase("Nam")) {
            cbGender.setSelectedItem("Nam");
        } else if (gender.equalsIgnoreCase("Nữ")) {
            cbGender.setSelectedItem("Nữ");
        } else {
            cbGender.setSelectedIndex(0); // fallback
        }

        txtNation.setText(s.getNation());
        txtDate.setText(s.getDate());
        txtPlaceBirth.setText(s.getPlaceBirth());
        txtPlaceLive.setText(s.getPlaceLive());
        txtParent.setText(s.getParent());

        for (int i = 0; i < cbClass.getItemCount(); i++) {
            if (cbClass.getItemAt(i).getName().equals(s.getClassId())) {
                cbClass.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        txtName.setText("");
        cbGender.setSelectedIndex(0);
        cbClass.setSelectedIndex(0);
        txtNation.setText("");
        txtDate.setText("");
        txtPlaceBirth.setText("");
        txtPlaceLive.setText("");
        txtParent.setText("");
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
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 1. Khởi tạo model trước
        DefaultTableModel scoreTableModel = new DefaultTableModel(new Object[]{"Môn học", "Điểm"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Double.class : String.class;
            }

        };

        // 2. Bảng nhập điểm
        JTable scoreTable = new JTable(scoreTableModel) {
            @Override
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                if (columnIndex == 0) {
                    columnIndex = 1;
                }
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };

        scoreTable.setDefaultEditor(String.class, null);
        scoreTable.setRowHeight(21);
        scoreTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scoreTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        scoreTable.setShowGrid(true);
        scoreTable.setGridColor(Color.LIGHT_GRAY);
        scoreTable.setSelectionBackground(new Color(220, 240, 255));
        scoreTable.setSelectionForeground(Color.BLACK);
        scoreTable.setSurrendersFocusOnKeystroke(true);
        scoreTable.setFocusTraversalKeysEnabled(false);
        scoreTable.getTableHeader().setReorderingAllowed(false);
        scoreTable.getTableHeader().setResizingAllowed(false);
        scoreTable.setDefaultEditor(String.class, null); // Không cho sửa cột 0
        scoreTable.setSurrendersFocusOnKeystroke(true);
        scoreTable.setFocusTraversalKeysEnabled(false);

        // ⚙️ Căn chỉnh cột
        scoreTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Môn học
        scoreTable.getColumnModel().getColumn(1).setPreferredWidth(60);  // Điểm

        // 🎯 Căn giữa cột Điểm
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        scoreTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

// 🌈 Xen kẽ màu dòng cho toàn bảng
        scoreTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Nếu là cột 1 (Điểm), giữ renderer đã set riêng
                if (column == 1) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa luôn nếu cần
                }

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }

                return c;
            }
        });

        // 3. Thêm môn học
        for (String subject : SUBJECTS) {
            scoreTableModel.addRow(new Object[]{subject, null});
        }

        JScrollPane scrollPane = new JScrollPane(scoreTable);
        scrollPane.setPreferredSize(new Dimension(260, 277));
        scoreTable.setShowVerticalLines(true);
        scoreTable.setShowHorizontalLines(true);
        scoreTable.setGridColor(Color.GRAY); // rõ hơn Color.LIGHT_GRAY

        // 4. Combobox chọn học sinh
        JComboBox<Student> cbStudent = new JComboBox<>(new DefaultComboBoxModel<>(
                studentController.getAllStudents().toArray(new Student[0])
        ));
        JTextField txtSearchStudent = new JTextField(15); // 🔍 ô tìm tên học sinh
        JTextField txtSearchIdStudent = new JTextField(15); // 🔍 ô tìm MSSV
        JButton btnSearchIdStudent = new JButton("Tìm Mã HS");

        JButton btnSearchStudent = new JButton("Tìm");    // nút tìm

        // 5. Label kết quả
        JLabel lblResult = new JLabel(" ", SwingConstants.CENTER);
        lblResult.setFont(new Font("Arial", Font.BOLD, 14));
        lblResult.setForeground(new Color(0, 102, 0));

        // 6. Form chọn học sinh
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nhập điểm học sinh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Học sinh:"), gbc);
        gbc.gridx = 1;
        form.add(cbStudent, gbc);
        // 🔍 MSSV
        gbc.gridy++;
        gbc.gridx = 0;
        form.add(new JLabel("Mã HS:"), gbc);
        gbc.gridx = 1;
        JPanel mssvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        txtSearchIdStudent.setPreferredSize(new Dimension(100, 25));
        btnSearchIdStudent.setPreferredSize(new Dimension(80, 25));
        mssvPanel.add(txtSearchIdStudent);
        mssvPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        mssvPanel.add(btnSearchIdStudent);
        form.add(mssvPanel, gbc);

// 🔍 Tên
        gbc.gridy++;
        gbc.gridx = 0;
        form.add(new JLabel("Tìm học sinh:"), gbc);
        gbc.gridx = 1;
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        txtSearchStudent.setPreferredSize(new Dimension(100, 25));
        btnSearchStudent.setPreferredSize(new Dimension(80, 25));
        namePanel.add(txtSearchStudent);
        namePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        namePanel.add(btnSearchStudent);
        form.add(namePanel, gbc);

        // 7. Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnBulkEntry = new JButton("📥 Nhập điểm hàng loạt");
        JButton btnSave = new JButton("💾 Lưu điểm");
        JButton btnReport = new JButton("📄 Xem học bạ");
        buttonPanel.add(btnBulkEntry);
        buttonPanel.add(btnSave);
        buttonPanel.add(btnReport);

        // 8. Panel trên
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        JPanel tableWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));

        tableWrapper.setBorder(BorderFactory.createTitledBorder("Bảng điểm"));
        tableWrapper.add(scrollPane);
        panel.add(tableWrapper, BorderLayout.CENTER);

        panel.add(lblResult, BorderLayout.SOUTH);

        // === SỰ KIỆN ===
        btnBulkEntry.addActionListener(e -> {
            BulkScoreEntryFrame bulkFrame = new BulkScoreEntryFrame(
                    studentController, scoreController, classController);
            bulkFrame.setVisible(true);
        });
        btnSearchStudent.addActionListener(e -> {
            String keyword = txtSearchStudent.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Nhập tên học sinh để tìm.");
                return;
            }

            ComboBoxModel<Student> model = cbStudent.getModel();
            boolean found = false;
            for (int i = 0; i < model.getSize(); i++) {
                Student s = model.getElementAt(i);
                if (s.getName().toLowerCase().contains(keyword)) {
                    cbStudent.setSelectedIndex(i); // ✅ Chọn học sinh tương ứng
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(panel, "Không tìm thấy học sinh tên chứa: " + keyword);
            }
        });
        btnSearchIdStudent.addActionListener(e -> {
    String keyword = txtSearchIdStudent.getText().trim();
    if (keyword.isEmpty()) {
        JOptionPane.showMessageDialog(panel, "Nhập mã số học sinh để tìm.");
        return;
    }

    ComboBoxModel<Student> model = cbStudent.getModel();
    boolean found = false;
    for (int i = 0; i < model.getSize(); i++) {
        Student s = model.getElementAt(i);
        if (s.getId().trim().equalsIgnoreCase(keyword.trim())) {
            cbStudent.setSelectedIndex(i); // ✅ chọn học sinh tương ứng
            found = true;
            break;
        }
    }

    if (!found) {
        JOptionPane.showMessageDialog(panel, "Không tìm thấy học sinh có mã: " + keyword);
    }
});


        btnSave.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn học sinh.");
                return;
            }

            for (int i = 0; i < scoreTableModel.getRowCount(); i++) {
                String subject = (String) scoreTableModel.getValueAt(i, 0);
                Object scoreObj = scoreTableModel.getValueAt(i, 1);

                if (scoreObj == null) {
                    continue;
                }

                double score;
                try {
                    score = Double.parseDouble(scoreObj.toString());
                    if (score < 0 || score > 10) {
                        JOptionPane.showMessageDialog(panel, "Điểm " + subject + " phải từ 0 đến 10.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Điểm " + subject + " không hợp lệ.");
                    return;
                }

                List<Score> existingScores = scoreController.getScoresByStudentId(s.getId());
                Optional<Score> existing = existingScores.stream()
                        .filter(sc -> sc.getSubject().equalsIgnoreCase(subject))
                        .findFirst();

                if (existing.isPresent()) {
                    existing.get().setScore(score);
                } else {
                    scoreController.addScore(new Score(s.getId(), subject, score));
                }
            }

            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());
            lblResult.setText(String.format("🎓 Điểm TB: %.2f - Xếp loại: %s", avg, level));
            JOptionPane.showMessageDialog(panel, "✅ Đã lưu toàn bộ điểm.");
            refreshStudentComboBox(cbStudent);
        });

        btnReport.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            if (s == null) {
                JOptionPane.showMessageDialog(panel, "Vui lòng chọn học sinh.");
                return;
            }

            List<Score> scores = scoreController.getScoresByStudentId(s.getId());
            double avg = scoreController.getAverage(s.getId());
            String level = scoreController.classify(s.getId());

            ReportCardFrame report = new ReportCardFrame(s, scores, avg, level);
            report.setVisible(true);
        });
        cbStudent.addActionListener(e -> {
            Student s = (Student) cbStudent.getSelectedItem();
            if (s == null) {
                return;
            }

            List<Score> scores = scoreController.getScoresByStudentId(s.getId());
            for (int i = 0; i < scoreTableModel.getRowCount(); i++) {
                String subject = (String) scoreTableModel.getValueAt(i, 0);
                Optional<Score> existing = scores.stream()
                        .filter(sc -> sc.getSubject().equalsIgnoreCase(subject))
                        .findFirst();
                scoreTableModel.setValueAt(existing.map(Score::getScore).orElse(null), i, 1);
            }
        });

        return panel;
    }

}
