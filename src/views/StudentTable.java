package views;

import java.util.List;
import javax.swing.table.AbstractTableModel;
import models.Student;

public class StudentTable extends AbstractTableModel {

    private final List<Student> students;
    private final String[] columnNames = {"Mã số", "Tên", "Lớp", "Niên khóa", "GVCN", "Giới tính"};

    public StudentTable(List<Student> students) {
        this.students = students;
    }

    public void setStudents(List<Student> students) {
        fireTableDataChanged();
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
        return switch (col) {
            case 0 -> s.getId();
            case 1 -> s.getName();
            case 2 -> s.getClassId();
            case 3 -> s.getSchoolYear();
            case 4 -> s.getHomeroomTeacher();
            case 5 -> s.getGender(); // ➕ THÊM GIỚI TÍNH
            default -> null;
        };
    }
}
