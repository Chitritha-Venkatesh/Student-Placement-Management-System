

/**
 * Base class representing a Person.
 * Highlights the concept of Inheritance: Student extends Person.
 */
public class Person {
    protected String name;

    /**
     * Default Constructor
     */
    public Person() {
        this.name = "Unknown";
    }

    /**
     * Parameterized Constructor
     */
    public Person(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "Unknown";
        } else {
            this.name = name.trim();
        }
    }
}