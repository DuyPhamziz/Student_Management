package models;

public class Student extends Person {

    private String classId;
    private String schoolYear;
    private String homeroomTeacher;
    private String gender;
    public Student(String name, String id, String classId, String schoolYear, String homeroomTeacher, String gender) {
    super(name, id);
    this.classId = classId;
    this.schoolYear = schoolYear;
    this.homeroomTeacher = homeroomTeacher;
    this.gender = gender;
}


    public String getClassId() {
        return classId;
    }
public String getGender() {
    return gender;
}
public void setGender(String gender) {
    this.gender = gender;
}
    public String getSchoolYear() {
        return schoolYear;
    }

    public String getHomeroomTeacher() {
        return homeroomTeacher;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = schoolYear;
    }

    public void setHomeroomTeacher(String homeroomTeacher) {
        this.homeroomTeacher = homeroomTeacher;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
    public String getLastName() {
    String[] parts = name.trim().split("\\s+");
    return parts.length > 0 ? parts[parts.length - 1] : "";
}

}
