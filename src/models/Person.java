package models;

public class Person {
    protected String name;
    protected String id;
    protected String gender;

    public Person(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Person(String name, String id, String gender) {
        this.name = name;
        this.id = id;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
