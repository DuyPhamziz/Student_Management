package views;

import models.Student;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class StudentTable extends AbstractTableModel {

    private final List<Student> students;

    // 👉 Các cột hiển thị
    private final String[] columnNames = {
        "Mã HS", "Họ và tên đệm", "Tên", "Giới tính", "Lớp", "GVCN", "Năm học"
    };

    public StudentTable(List<Student> students) {
        this.students = students;
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Student s = students.get(row);
        String[] parts = s.getName().trim().split("\\s+");
        String hoTenDem = parts.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(parts, 0, parts.length - 1)) : "";
        String ten = parts.length > 0 ? parts[parts.length - 1] : "";

        return switch (col) {
            case 0 -> s.getId();
            case 1 -> hoTenDem;
            case 2 -> ten;
            case 3 -> s.getGender();
            case 4 -> s.getClassId();
            case 5 -> s.getHomeroomTeacher();
            case 6 -> s.getSchoolYear();
            default -> null;
        };
    }
    
}
