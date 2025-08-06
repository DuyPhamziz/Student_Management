package controllers;

import utils.CSVHelper;
import utils.FilePath;

import java.util.*;

import models.Student;

public class StudentController {
    private List<Student> students;

    public StudentController() {
        students = CSVHelper.readStudentsFromCSV(FilePath.STUDENT_CSV);
    }

    public void addStudent(Student s) {
        students.add(s);
        CSVHelper.writeStudentsToCSV(students, FilePath.STUDENT_CSV);
    }

    public List<Student> getAllStudents() {
        return students;
    }

    public List<Student> searchStudentsById(String keyword) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getId().toLowerCase().contains(keyword.toLowerCase().trim())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Student> searchStudentsByName(String name) {
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase().contains(name.toLowerCase().trim())) {
                result.add(s);
            }
        }
        return result;
    }

    public List<Student> filterByGender(String gender) {
        if (gender.equalsIgnoreCase("Tất cả"))
            return new ArrayList<>(students);
        return students.stream()
                .filter(s -> s.getGender().equalsIgnoreCase(gender))
                .toList();
    }

    public List<Student> filterByClass(String classId) {
        return students.stream()
                .filter(s -> s.getClassId().equalsIgnoreCase(classId))
                .toList();
    }

    public List<Student> filterByTeacher(String teacherName) {
        return students.stream()
                .filter(s -> s.getHomeroomTeacher().equalsIgnoreCase(teacherName))
                .toList();
    }

}
