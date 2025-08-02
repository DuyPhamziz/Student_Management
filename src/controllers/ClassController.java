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
}
