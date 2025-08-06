package controllers;

import models.ClassRoom;
import java.util.ArrayList;
import java.util.List;

public class ClassController {
    private final List<ClassRoom> classList = new ArrayList<>();

    public void addClass(ClassRoom c) {
        classList.add(c);
    }

    public List<ClassRoom> getAllClasses() {
        return classList;
    }

    public List<ClassRoom> searchByClassName(String keyword) {
        List<ClassRoom> result = new ArrayList<>();
        for (ClassRoom c : getAllClasses()) {
            if (c.getName().toLowerCase().contains(keyword.toLowerCase().trim())) {
                result.add(c);
            }
        }
        return result;
    }

    public List<ClassRoom> searchByTeacherId(String keyword) {
        List<ClassRoom> result = new ArrayList<>();
        for (ClassRoom c : getAllClasses()) {
            if (c.getTeacherId().toLowerCase().contains(keyword.toLowerCase().trim())) {
                result.add(c);
            }
        }
        return result;
    }

}
