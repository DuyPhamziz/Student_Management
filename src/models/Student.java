package models;

import models.Person;

public class Student extends Person {
    private String classId;
    private String schoolYear;
    private String homeroomTeacher;

    public Student(String name, String id, String classId, String schoolYear, String homeroomTeacher) {
        super(name, id);
        this.classId = classId;
        this.schoolYear = schoolYear;
        this.homeroomTeacher = homeroomTeacher;
    }

    public String getClassId() {
        return classId;
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

}
