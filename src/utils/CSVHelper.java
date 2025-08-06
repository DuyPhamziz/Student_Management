package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import models.ClassRoom;
import models.Score;
import models.Student;



public class CSVHelper {

    public static List<Student> readStudentsFromCSV(String path) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 5) {
                    students.add(new Student(values[0], values[1], values[2], values[3], values[4]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    public static void writeStudentsToCSV(List<Student> students, String path) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            for (Student s : students) {
                bw.write(String.join(",",
                        s.getName(), s.getId(), s.getClassId(), s.getSchoolYear(), s.getHomeroomTeacher()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ClassRoom> readClassesFromCSV(String path) {
        List<ClassRoom> classes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 2) {
                    classes.add(new ClassRoom(values[0], values[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static void writeClassesToCSV(List<ClassRoom> classes, String path) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            for (ClassRoom c : classes) {
                bw.write(String.join(",", c.getName(), c.getTeacher()));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Score> readScoresFromCSV(String path) {
        List<Score> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    scores.add(new Score(parts[0], parts[1], Double.parseDouble(parts[2])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public static void writeScoresToCSV(List<Score> scores, String path) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), StandardCharsets.UTF_8))) {
            for (Score s : scores) {
                bw.write(String.join(",", s.getStudentId(), s.getSubject(), String.valueOf(s.getScore())));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
// readScoresFromCSV, writeScoresToCSV
