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
}
