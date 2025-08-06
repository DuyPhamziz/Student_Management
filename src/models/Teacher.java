package models;

public class Teacher extends Person {
    public Teacher(String name, String id) {
        super(name, id);
    }

    @Override
    public String toString() {
        return name;
    }
}
