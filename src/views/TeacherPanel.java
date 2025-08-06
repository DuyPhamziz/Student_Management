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
        teacherList.addListSelectionListener(e -> {
            Teacher selected = teacherList.getSelectedValue();
            if (selected != null) {
                txtName.setText(selected.getName());
                txtId.setText(selected.getId());
            }
        });

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
}
