package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import models.ClassRoom;
import utils.CSVHelper;

import models.Student;
import models.Teacher;
import models.Score;


public class CSVHelper {

    public static List<Student> readStudentsFromCSV(String path) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 6) {
                    Student s = new Student(values[0], values[1], values[2], values[3], values[4], values[5]);
                    s.setGender(values[5]);
                    students.add(s);
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
    s.getName(), s.getId(), s.getClassId(), s.getSchoolYear(), s.getHomeroomTeacher(), s.getGender()));

                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ClassRoom> readClassesFromCSV(String path) {
    List<ClassRoom> classes = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 2) {
                classes.add(new ClassRoom(parts[0], parts[1]));
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return classes;
}


    public static void writeClassesToCSV(List<ClassRoom> classes, String path) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
        for (ClassRoom c : classes) {
            String teacherName = "N/A";
            if (ClassRoom.getTeacherController() != null) {
                for (Teacher t : ClassRoom.getTeacherController().getAllTeachers()) {
                    if (t.getId().equals(c.getTeacherId())) {
                        teacherName = t.getName();
                        break;
                    }
                }
            }
            writer.println(c.getName() + "," + c.getTeacherId() + "," + teacherName);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    public static List<Teacher> readTeachersFromCSV(String path) {
    List<Teacher> teachers = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if (values.length == 2) {
                teachers.add(new Teacher(values[0], values[1]));
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return teachers;
}

public static void writeTeachersToCSV(List<Teacher> teachers, String path) {
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
        for (Teacher t : teachers) {
            bw.write(t.getName() + "," + t.getId());
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
public static List<Score> readScoresFromCSV(String path) {
    List<Score> scores = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String studentId = parts[0];
                String subject = parts[1];
                double score = Double.parseDouble(parts[2]);
                scores.add(new Score(studentId, subject, score));
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return scores;
}

public static void writeScoresToCSV(List<Score> scores, String path) {
    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
        for (Score s : scores) {
            bw.write(s.getStudentId() + "," + s.getSubject() + "," + s.getScore());
            bw.newLine();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
