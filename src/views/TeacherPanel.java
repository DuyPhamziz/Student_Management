package views;

import controllers.TeacherController;
import models.Teacher;

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

    public TeacherPanel(TeacherController controller) {
        this.teacherController = controller;
        setLayout(new BorderLayout());

        for (Teacher t : teacherController.getAllTeachers()) {
            teacherModel.addElement(t);
        }

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Thông tin giáo viên"));
        form.add(new JLabel("Họ tên:"));
        form.add(txtName);
        form.add(new JLabel("Mã GV:"));
        form.add(txtId);

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

        teacherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btnSearchId.addActionListener(e -> {
            String keyword = txtSearchId.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập mã giáo viên để tìm.");
                return;
            }
            java.util.List<Teacher> results = teacherController.searchById(keyword);
            showSearchResults(results);
        });

        btnSearchName.addActionListener(e -> {
            String keyword = txtSearchName.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập tên giáo viên để tìm.");
                return;
            }
            java.util.List<Teacher> results = teacherController.searchByName(keyword);
            showSearchResults(results);
        });

        btnReset.addActionListener(e -> {
            refreshTeacherList();
        });

        teacherList.addListSelectionListener(e -> {
            Teacher selected = teacherList.getSelectedValue();
            if (selected != null) {
                txtName.setText(selected.getName());
                txtId.setText(selected.getId());
            }
        });
        // Giao diện tìm kiếm giáo viên
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm giáo viên"));

        // Dòng tìm theo mã GV
        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Mã GV:"));
        line1.add(txtSearchId);
        line1.add(btnSearchId);

        // Dòng tìm theo họ tên
        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("Họ tên:"));
        line2.add(txtSearchName);
        line2.add(btnSearchName);

        // Dòng reset
        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.add(btnReset);

        // Thêm các dòng vào searchPanel
        searchPanel.add(line1);
        searchPanel.add(line2);
        searchPanel.add(line3);

        // Thêm panel tìm kiếm vào phía Bắc (trên cùng)
        add(searchPanel, BorderLayout.NORTH);

        add(left, BorderLayout.WEST);
        add(new JScrollPane(teacherList), BorderLayout.CENTER);
    }

    private void addTeacher() {
        String name = txtName.getText().trim();
        String id = txtId.getText().trim();
        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ.");
            return;
        }

        Teacher t = new Teacher(name, id);
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
        if (name.isEmpty() || id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ.");
            return;
        }

        Teacher t = new Teacher(name, id);
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
        teacherList.clearSelection();
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
