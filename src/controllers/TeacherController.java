package controllers;

import models.Teacher;
import utils.CSVHelper;
import utils.FilePath;

import java.util.ArrayList;
import java.util.List;

public class TeacherController {
    private final List<Teacher> teachers = new ArrayList<>();

    public TeacherController() {
        teachers.addAll(CSVHelper.readTeachersFromCSV(FilePath.TEACHER_CSV));
    }

    public void addTeacher(Teacher t) {
        teachers.add(t);
        CSVHelper.writeTeachersToCSV(teachers, FilePath.TEACHER_CSV);
    }

    public void updateTeacher(int index, Teacher t) {
        teachers.set(index, t);
        CSVHelper.writeTeachersToCSV(teachers, FilePath.TEACHER_CSV);
    }

    public void deleteTeacher(int index) {
        teachers.remove(index);
        CSVHelper.writeTeachersToCSV(teachers, FilePath.TEACHER_CSV);
    }

    public List<Teacher> getAllTeachers() {
        return teachers;
    }

    public List<Teacher> searchById(String keyword) {
        List<Teacher> result = new ArrayList<>();
        for (Teacher t : teachers) {
            if (t.getId().toLowerCase().contains(keyword.toLowerCase().trim())) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Teacher> searchByName(String keyword) {
        List<Teacher> result = new ArrayList<>();
        for (Teacher t : teachers) {
            if (t.getName().toLowerCase().contains(keyword.toLowerCase().trim())) {
                result.add(t);
            }
        }
        return result;
    }

}
