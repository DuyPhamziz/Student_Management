package views;

import controllers.TeacherController;
import models.Teacher;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class TeacherPanel extends JPanel {

    private final JTextField txtName = new JTextField(15);
    private final JTextField txtId = new JTextField(15);
    private final DefaultListModel<Teacher> teacherModel = new DefaultListModel<>();
    private final JList<Teacher> teacherList = new JList<>(teacherModel);
    private final TeacherController teacherController;
    private final JTextField txtSearchId = new JTextField(15);
    private final JTextField txtSearchName = new JTextField(15);
    private final JButton btnSearchId = new JButton("Tìm theo Mã GV");
    private final JButton btnSearchName = new JButton("Tìm theo Họ tên");
    private final JButton btnReset = new JButton("Hiển thị tất cả");
    private final JTextField txtRank = new JTextField(15);
    private final JTextField txtSubject = new JTextField(15);
    private final JTextField txtCategorySubject = new JTextField(15);
    private final JComboBox<String> cbGender = new JComboBox<>(new String[] { "Nam", "Nữ", "Khác" });

    public TeacherPanel(TeacherController controller) {
        this.teacherController = controller;
        setLayout(new BorderLayout());

        // Load dữ liệu giáo viên vào danh sách
        for (Teacher t : teacherController.getAllTeachers()) {
            teacherModel.addElement(t);
        }

        // Form nhập liệu
        JPanel form = new JPanel(new GridLayout(6, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giáo viên"));
        form.add(new JLabel("Họ tên:"));
        form.add(txtName);
        form.add(new JLabel("Mã GV:"));
        form.add(txtId);
        form.add(new JLabel("Giới tính:"));
        form.add(cbGender);
        form.add(new JLabel("Học hàm / Chức vụ:"));
        form.add(txtRank);
        form.add(new JLabel("Môn giảng dạy:"));
        form.add(txtSubject);
        form.add(new JLabel("Tổ bộ môn:"));
        form.add(txtCategorySubject);

        // Các nút chức năng
        JButton btnAdd = new JButton("Thêm");
        btnAdd.addActionListener(e -> addTeacher());

        JButton btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> editTeacher());

        JButton btnDelete = new JButton("Xóa");
        btnDelete.addActionListener(e -> deleteTeacher());

        JPanel btns = new JPanel();
        btns.add(btnAdd);
        btns.add(btnEdit);
        btns.add(btnDelete);

        JPanel left = new JPanel(new BorderLayout());
        left.add(form, BorderLayout.CENTER);
        left.add(btns, BorderLayout.SOUTH);

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm giáo viên"));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Mã GV:"));
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

        // Bắt sự kiện tìm kiếm
        btnSearchId.addActionListener(e -> {
            String keyword = txtSearchId.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập mã giáo viên để tìm.");
                return;
            }
            List<Teacher> results = teacherController.searchById(keyword);
            showSearchResults(results);
        });

        btnSearchName.addActionListener(e -> {
            String keyword = txtSearchName.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập tên giáo viên để tìm.");
                return;
            }
            List<Teacher> results = teacherController.searchByName(keyword);
            showSearchResults(results);
        });

        btnReset.addActionListener(e -> refreshTeacherList());

        // Chọn item trong danh sách
        teacherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherList.addListSelectionListener(e -> {
            Teacher selected = teacherList.getSelectedValue();
            if (selected != null) {
                txtName.setText(selected.getName());
                txtId.setText(selected.getId());
                cbGender.setSelectedItem(selected.getGender());
                txtRank.setText(selected.getRank());
                txtSubject.setText(selected.getSubject());
                txtCategorySubject.setText(selected.getCategorySubject());
            }
        });

        // Thêm các thành phần vào giao diện
        add(searchPanel, BorderLayout.NORTH);
        add(left, BorderLayout.WEST);
        add(new JScrollPane(teacherList), BorderLayout.CENTER);
    }

    

    private void addTeacher() {
        String name = txtName.getText().trim();
        String id = txtId.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        String rank = txtRank.getText().trim();
        String subject = txtSubject.getText().trim();
        String categorySubject = txtCategorySubject.getText().trim();

        if (name.isEmpty() || id.isEmpty() || rank.isEmpty() || subject.isEmpty() || categorySubject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ.");
            return;
        }

        Teacher t = new Teacher(name, id, gender, rank, subject, categorySubject);
        teacherController.addTeacher(t);
        teacherModel.addElement(t);
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm giáo viên thành công.");
    }

    private void editTeacher() {
        int index = teacherList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn giáo viên để sửa.");
            return;
        }

        String name = txtName.getText().trim();
        String id = txtId.getText().trim();
        String gender = (String) cbGender.getSelectedItem();
        String rank = txtRank.getText().trim();
        String subject = txtSubject.getText().trim();
        String categorySubject = txtCategorySubject.getText().trim();

        if (name.isEmpty() || id.isEmpty() || rank.isEmpty() || subject.isEmpty() || categorySubject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ.");
            return;
        }

        Teacher t = new Teacher(name, id, gender, rank, subject, categorySubject);
        teacherController.updateTeacher(index, t);
        teacherModel.set(index, t);
        clearForm();
        JOptionPane.showMessageDialog(this, "Sửa giáo viên thành công.");
    }

    private void deleteTeacher() {
        int index = teacherList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Chọn giáo viên để xóa.");
            return;
        }

        teacherController.deleteTeacher(index);
        teacherModel.remove(index);
        clearForm();
        JOptionPane.showMessageDialog(this, "Xóa giáo viên thành công.");

    }

    private void clearForm() {
        txtName.setText("");
        txtId.setText("");
        txtRank.setText("");
        txtSubject.setText("");
        txtCategorySubject.setText("");
    }

    private void showSearchResults(java.util.List<Teacher> results) {
        teacherModel.clear();
        for (Teacher t : results) {
            teacherModel.addElement(t);
        }
    }

    private void refreshTeacherList() {
        teacherModel.clear();
        for (Teacher t : teacherController.getAllTeachers()) {
            teacherModel.addElement(t);
        }
    }

}
