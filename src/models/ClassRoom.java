package models;

public class ClassRoom {
    private String name;
    private String teacher;

    public ClassRoom(String name, String teacher) {
        this.name = name;
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String toString() {
        return name; // Để hiển thị trong JComboBox
    }
}
