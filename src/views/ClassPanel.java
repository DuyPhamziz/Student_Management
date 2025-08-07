package views;

import controllers.ClassController;
import controllers.TeacherController;
import models.ClassRoom;
import models.Teacher;
import utils.CSVHelper;
import utils.FilePath;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ClassPanel extends JPanel {
    private final JTextField txtName = new JTextField(20);
    private final JComboBox<Teacher> cbTeacher = new JComboBox<>();
    private final JLabel lblRank = new JLabel();
    private final JLabel lblPosition = new JLabel();
    private final JLabel lblSubject = new JLabel();

    private final DefaultListModel<ClassRoom> classModel = new DefaultListModel<>();
    private final JList<ClassRoom> classList = new JList<>(classModel);
    private final JTextField txtSearchName = new JTextField(15);
    private final JTextField txtSearchTeacherId = new JTextField(15);
    private final JButton btnSearchName = new JButton("Tìm theo tên lớp");
    private final JButton btnSearchTeacherId = new JButton("Tìm theo Mã GV");
    private final JButton btnReset = new JButton("Hiển thị tất cả");

    private final ClassController classController;

    public ClassPanel(ClassController controller, TeacherController teacherController) {
        this.classController = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Load GVCN vào combobox
        for (Teacher t : teacherController.getAllTeachers()) {
            cbTeacher.addItem(t);
        }

        // Tạo thư mục nếu chưa có
        File dataDir = new File("data");
        if (!dataDir.exists())
            dataDir.mkdirs();

        // Gán teacherController cho ClassRoom
        ClassRoom.setTeacherController(teacherController);

        // Load dữ liệu lớp học
        List<ClassRoom> existing = CSVHelper.readClassesFromCSV(FilePath.CLASS_CSV);
        classController.getAllClasses().clear();
        classModel.clear();
        for (ClassRoom c : existing) {
            classController.addClass(c);
            classModel.addElement(c);
        }

        // ------------------------- PANEL TÌM KIẾM -------------------------
        JPanel searchPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("🔍 Tìm kiếm lớp học"));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Tên lớp:"));
        line1.add(txtSearchName);
        line1.add(btnSearchName);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("Mã GV:"));
        line2.add(txtSearchTeacherId);
        line2.add(btnSearchTeacherId);

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.add(btnReset);

        searchPanel.add(line1);
        searchPanel.add(line2);
        searchPanel.add(line3);

        add(searchPanel, BorderLayout.NORTH);

        // ------------------------- FORM THÔNG TIN LỚP -------------------------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("📝 Thông tin lớp học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Tên lớp:"), gbc);
        gbc.gridx = 1;
        form.add(txtName, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("GVCN:"), gbc);
        gbc.gridx = 1;
        form.add(cbTeacher, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Trình độ:"), gbc);
        gbc.gridx = 1;
        form.add(lblRank, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 1;
        form.add(lblPosition, gbc);
        row++;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Bộ môn:"), gbc);
        gbc.gridx = 1;
        form.add(lblSubject, gbc);
        row++;

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        form.add(buttonPanel, gbc);

        // ------------------------- DANH SÁCH LỚP -------------------------
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollList = new JScrollPane(classList);
        scrollList.setBorder(BorderFactory.createTitledBorder("📋 Danh sách lớp học"));

        // ------------------------- TỔNG HỢP -------------------------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, scrollList);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        // ------------------------- EVENT -------------------------
        classList.addListSelectionListener(e -> {
            ClassRoom selected = classList.getSelectedValue();
            if (selected != null) {
                txtName.setText(selected.getName());
                for (int i = 0; i < cbTeacher.getItemCount(); i++) {
                    Teacher t = cbTeacher.getItemAt(i);
                    if (t.getId().equals(selected.getTeacherId())) {
                        cbTeacher.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        cbTeacher.addActionListener(e -> {
            Teacher selected = (Teacher) cbTeacher.getSelectedItem();
            if (selected != null) {
                lblRank.setText(selected.getRank());
                lblPosition.setText(selected.getCategorySubject());
                lblSubject.setText(selected.getSubject());
            } else {
                lblRank.setText("");
                lblPosition.setText("");
                lblSubject.setText("");
            }
        });

        btnAdd.addActionListener(e -> addClass());
        btnEdit.addActionListener(e -> editClass());
        btnDelete.addActionListener(e -> deleteClass());

        btnSearchName.addActionListener(e -> {
            String keyword = txtSearchName.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên lớp.");
                return;
            }
            List<ClassRoom> results = classController.searchByClassName(keyword);
            updateClassList(results);
        });

        btnSearchTeacherId.addActionListener(e -> {
            String keyword = txtSearchTeacherId.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã giáo viên.");
                return;
            }
            List<ClassRoom> results = classController.searchByTeacherId(keyword);
            updateClassList(results);
        });

        btnReset.addActionListener(e -> {
            updateClassList(classController.getAllClasses());
        });
    }

    private void addClass() {
        String name = txtName.getText().trim();
        Teacher selectedTeacher = (Teacher) cbTeacher.getSelectedItem();
        if (name.isEmpty() || selectedTeacher == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin lớp.");
            return;
        }
        for (ClassRoom c : classController.getAllClasses()) {
            if (c.getName().equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this, "Lớp đã tồn tại.");
                return;
            }
        }

        ClassRoom c = new ClassRoom(name, selectedTeacher.getId());
        classController.addClass(c);
        classModel.addElement(c);
        CSVHelper.writeClassesToCSV(classController.getAllClasses(), FilePath.CLASS_CSV);
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm lớp học thành công.");
    }

    private void editClass() {
        int index = classList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn lớp để sửa.");
            return;
        }

        String name = txtName.getText().trim();
        Teacher selectedTeacher = (Teacher) cbTeacher.getSelectedItem();
        if (name.isEmpty() || selectedTeacher == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        ClassRoom updated = new ClassRoom(name, selectedTeacher.getId());
        classController.getAllClasses().set(index, updated);
        classModel.set(index, updated);
        CSVHelper.writeClassesToCSV(classController.getAllClasses(), FilePath.CLASS_CSV);
        clearForm();
        JOptionPane.showMessageDialog(this, "Sửa lớp học thành công.");
    }

    private void deleteClass() {
        int index = classList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn lớp để xóa.");
            return;
        }

        classController.getAllClasses().remove(index);
        classModel.remove(index);
        CSVHelper.writeClassesToCSV(classController.getAllClasses(), FilePath.CLASS_CSV);
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa lớp học thành công.");
    }

    private void clearForm() {
        txtName.setText("");
        cbTeacher.setSelectedIndex(-1);
        lblRank.setText("");
        lblPosition.setText("");
        lblSubject.setText("");
        classList.clearSelection();
    }

    private void updateClassList(List<ClassRoom> list) {
        classModel.clear();
        for (ClassRoom c : list) {
            classModel.addElement(c);
        }
    }
}
