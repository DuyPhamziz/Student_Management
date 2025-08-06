package controllers;

import java.util.*;
import java.util.stream.Collectors;
import models.Score;
import utils.CSVHelper;
import utils.FilePath;

public class ScoreController {
    private List<Score> scores;

    public ScoreController() {
        scores = CSVHelper.readScoresFromCSV(FilePath.SCORE_CSV);
    }

    public void addScore(Score score) {
        scores.add(score);
        CSVHelper.writeScoresToCSV(scores, FilePath.SCORE_CSV);
    }

    public List<Score> getScoresByStudent(String studentId) {
        return scores.stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public double getAverage(String studentId) {
        List<Score> studentScores = getScoresByStudent(studentId);
        return studentScores.stream().mapToDouble(Score::getScore).average().orElse(0);
    }

    public String classify(String studentId) {
        double avg = getAverage(studentId);
        if (avg >= 8) return "Giỏi";
        else if (avg >= 6.5) return "Khá";
        else if (avg >= 5) return "Trung bình";
        else return "Yếu";
    }
    public List<Score> getScoresByStudentId(String studentId) {
    return scores.stream()
                 .filter(score -> score.getStudentId().equals(studentId))
                 .collect(Collectors.toList());
}
public boolean hasScore(String studentId, String subject) {
    return scores.stream()
        .anyMatch(score -> 
            score.getStudentId().equals(studentId) && 
            score.getSubject().equalsIgnoreCase(subject)
        );
}

}
