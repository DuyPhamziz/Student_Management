package views;

import controllers.ClassController;
import models.ClassRoom;
import utils.CSVHelper;
import utils.FilePath;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ClassPanel extends JPanel {
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtTeacher = new JTextField(15);
    private final JTextArea textArea = new JTextArea(10, 30);
    private final ClassController classController;
    private final DefaultComboBoxModel<ClassRoom> classModel;
    private Runnable onClassAdded; // listener callback

    public ClassPanel(ClassController controller, DefaultComboBoxModel<ClassRoom> model) {
        this.classController = controller;
        this.classModel = model;

        // Tạo thư mục và load dữ liệu từ CSV
        File dataDir = new File("data");
        if (!dataDir.exists()) dataDir.mkdirs();
        List<ClassRoom> existing = CSVHelper.readClassesFromCSV(FilePath.CLASS_CSV);
        for (ClassRoom c : existing) {
            classController.addClass(c);
            classModel.addElement(c);
            textArea.append(String.format("Lớp: %s - GVCN: %s\n", c.getName(), c.getTeacher()));
        }

        setLayout(new BorderLayout());

        // Form nhập liệu
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("Tên lớp:"));
        form.add(txtName);
        form.add(new JLabel("GVCN:"));
        form.add(txtTeacher);
        JButton btnAdd = new JButton("Thêm lớp");
        btnAdd.addActionListener(e -> addClass());
        form.add(btnAdd);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    // Đăng ký callback khi thêm lớp
    public void setOnClassAdded(Runnable callback) {
        this.onClassAdded = callback;
    }

    private void addClass() {
        String name = txtName.getText().trim();
        String teacher = txtTeacher.getText().trim();
        if (name.isEmpty() || teacher.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin lớp.");
            return;
        }

        ClassRoom c = new ClassRoom(name, teacher);
        classController.addClass(c);
        classModel.addElement(c);
        CSVHelper.writeClassesToCSV(classController.getAllClasses(), FilePath.CLASS_CSV);
        textArea.append(String.format("Lớp: %s - GVCN: %s\n", name, teacher));

        if (onClassAdded != null) onClassAdded.run();
        txtName.setText("");
        txtTeacher.setText("");
    }
}