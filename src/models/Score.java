package models;

public class Score {
    private String studentId;
    private String subject;
    private double score;

    public Score(String studentId, String subject, double score) {
        this.studentId = studentId;
        this.subject = subject;
        this.score = score;
    }

    public String getStudentId() { return studentId; }
    public String getSubject() { return subject; }
    public double getScore() { return score; }

    public void setSubject(String subject) { this.subject = subject; }
    public void setScore(double score) { this.score = score; }
}
