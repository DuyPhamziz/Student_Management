package models;

public class Teacher extends Person {
    private String rank;
    private String subject;
    private String categorySubject;

    public Teacher(String name, String id, String gender, String rank, String subject, String categorySubject) {
        super(name, id, gender);
        this.rank = rank;
        this.subject = subject;
        this.categorySubject = categorySubject;
    }

    public String getRank() {
        return rank;
    }

    public String getSubject() {
        return subject;
    }

    public String getCategorySubject() {
        return categorySubject;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s, %s, %s", name, id, rank, subject, categorySubject);
    }

}
