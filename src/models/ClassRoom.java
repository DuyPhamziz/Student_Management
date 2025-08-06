package models;

import controllers.TeacherController;

public class ClassRoom {
    private String name;
    private String teacherId;
    private static TeacherController teacherController;
    public ClassRoom(String name, String teacherId) {
    this.name = name;
    this.teacherId = teacherId;
}

    public String getName() {
        return name;
    }


    @Override
public String toString() {
    String teacherName = "N/A";
    if (teacherController != null) {
        for (Teacher t : teacherController.getAllTeachers()) {
            if (t.getId().equals(teacherId)) {
                teacherName = t.getName();
                break;
            }
        }
    }
    return name + " - GVCN: " + teacherName;
}


public String getDisplayNameOnly() {
    return name;
}
    


public String getTeacherId() {
    return teacherId;
}



public static void setTeacherController(TeacherController controller) {
    teacherController = controller;
}



public static TeacherController getTeacherController() {
    return teacherController;
}

}
