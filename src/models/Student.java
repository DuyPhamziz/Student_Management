package models;

public class Student extends Person {

    private String classId;
    private String schoolYear;
    private String homeroomTeacher;
    private String gender;
    private String nation;
    private String date;
    private String placeBirth;
    private String placeLive;
    private String Parent;

    public Student(String name, String id, String classId, String schoolYear, String homeroomTeacher, String gender,
            String nation, String date, String placeBirth, String placeLive, String parent) {
        super(name, id);
        this.classId = classId;
        this.schoolYear = schoolYear;
        this.homeroomTeacher = homeroomTeacher;
        this.gender = gender;
        this.nation = nation;
        this.date = date;
        this.placeBirth = placeBirth;
        this.placeLive = placeLive;
        Parent = parent;
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

    public String getNation() {
        return nation;
    }

    public String getDate() {
        return date;
    }

    public String getPlaceBirth() {
        return placeBirth;
    }

    public String getPlaceLive() {
        return placeLive;
    }

    public String getParent() {
        return Parent;
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

    public void setNation(String nation) {
        this.nation = nation;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setPlaceBirth(String placeBirth) {
        this.placeBirth = placeBirth;
    }

    public void setPlaceLive(String placeLive) {
        this.placeLive = placeLive;
    }

    public void setParent(String parent) {
        Parent = parent;
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
