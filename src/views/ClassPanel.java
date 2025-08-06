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
    private final JTextField txtName = new JTextField(15);
    private final JComboBox<Teacher> cbTeacher = new JComboBox<>();
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

        setLayout(new BorderLayout());

        // Load dữ liệu giáo viên
        for (Teacher t : teacherController.getAllTeachers()) {
            cbTeacher.addItem(t);
        }

        // Load dữ liệu lớp học
        File dataDir = new File("data");
        if (!dataDir.exists())
            dataDir.mkdirs();
        ClassRoom.setTeacherController(teacherController); // Đảm bảo khi hiển thị toString()

        List<ClassRoom> existing = CSVHelper.readClassesFromCSV(FilePath.CLASS_CSV);

        // XÓA dữ liệu cũ trước khi load lại để tránh lặp
        classController.getAllClasses().clear();
        classModel.clear();

        for (ClassRoom c : existing) {
            classController.addClass(c);
            classModel.addElement(c);
        }
        // ==== Giao diện tìm kiếm lớp học ====
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm lớp học"));

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

        // Thêm panel tìm kiếm vào phía trên cùng
        add(searchPanel, BorderLayout.NORTH);

        // Giao diện nhập liệu
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin lớp học"));
        form.add(new JLabel("Tên lớp:"));
        form.add(txtName);
        form.add(new JLabel("GVCN:"));
        form.add(cbTeacher);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(e -> addClass());
        JButton btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> editClass());
        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteClass());

        JPanel buttons = new JPanel();
        buttons.add(btnAdd);
        buttons.add(btnEdit);
        buttons.add(btnDelete);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.CENTER);
        left.add(buttons, BorderLayout.SOUTH);

        // Danh sách lớp
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.addListSelectionListener(e -> {
            ClassRoom selected = classList.getSelectedValue();
            if (selected != null) {
                txtName.setText(selected.getName());
                // Tìm Teacher object từ ID
                for (int i = 0; i < cbTeacher.getItemCount(); i++) {
                    Teacher t = cbTeacher.getItemAt(i);
                    if (t.getId().equals(selected.getTeacherId())) {
                        cbTeacher.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
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

        add(left, BorderLayout.WEST);
        add(new JScrollPane(classList), BorderLayout.CENTER);
    }

    private void addClass() {
        String name = txtName.getText().trim();
        Teacher selectedTeacher = (Teacher) cbTeacher.getSelectedItem();
        if (name.isEmpty() || selectedTeacher == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin lớp.");
            return;
        }
        // Kiểm tra lớp đã tồn tại chưa
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
        classList.clearSelection();
    }

    private void updateClassList(List<ClassRoom> list) {
        classModel.clear();
        for (ClassRoom c : list) {
            classModel.addElement(c);
        }
    }

}
