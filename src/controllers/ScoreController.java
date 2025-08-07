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

    public Score getScore(String studentId, String subject) {
        for (Score s : scores) {
            if (s.getStudentId().equals(studentId) && s.getSubject().equals(subject)) {
                return s;
            }
        }
        return null;
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
        if (avg >= 8)
            return "Giỏi";
        else if (avg >= 6.5)
            return "Khá";
        else if (avg >= 5)
            return "Trung bình";
        else
            return "Yếu";
    }

    public List<Score> getScoresByStudentId(String studentId) {
        return scores.stream()
                .filter(score -> score.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public boolean hasScore(String studentId, String subject) {
        return scores.stream()
                .anyMatch(score -> score.getStudentId().equals(studentId) &&
                        score.getSubject().equalsIgnoreCase(subject));
    }

    public void addOrUpdateScore(Score newScore) {
        boolean updated = false;
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            if (s.getStudentId().equals(newScore.getStudentId()) && s.getSubject().equals(newScore.getSubject())) {
                scores.set(i, newScore);
                updated = true;
                break;
            }
        }
        if (!updated) {
            scores.add(newScore);
        }
        CSVHelper.writeScoresToCSV(scores, FilePath.SCORE_CSV);
    }

    public List<Score> getScores() {
        return scores;
    }

}
