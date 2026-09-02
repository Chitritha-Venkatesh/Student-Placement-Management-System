


import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Student in the Placement Management System.
 * Inherits from Person.
 * Demonstrates:
 * - Inheritance (extends Person)
 * - Encapsulation (private variables and getters/setters)
 * - Checked Exception handling integration
 */
public class Student extends Person {
    private String usn;
    private double cgpa;
    private String branch;
    private int certificationsCount;
    private int internshipsCount;
    private List<String> skills;

    /**
     * Default Constructor
     */
    public Student() {
        super("Unknown");
        this.usn = "Unknown";
        this.cgpa = 0.0;
        this.branch = "Unknown";
        this.certificationsCount = 0;
        this.internshipsCount = 0;
        this.skills = new ArrayList<>();
    }

    /**
     * Parameterized Constructor
     * Uses setters to enforce validations which throw checked exceptions.
     */
    public Student(String name, String usn, double cgpa, String branch, 
                   int certificationsCount, int internshipsCount, List<String> skills) 
                   throws InvalidStudentDataException {
        super(name);
        setUsn(usn);
        setCgpa(cgpa);
        setBranch(branch);
        setCertificationsCount(certificationsCount);
        setInternshipsCount(internshipsCount);
        this.skills = (skills != null) ? new ArrayList<>(skills) : new ArrayList<>();
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) throws InvalidStudentDataException {
        if (usn == null || usn.trim().isEmpty()) {
            throw new InvalidStudentDataException("USN cannot be empty.");
        }
        this.usn = usn.trim().toUpperCase();
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) throws InvalidStudentDataException {
        if (cgpa < 0.0 || cgpa > 10.0) {
            throw new InvalidStudentDataException("CGPA must be between 0.0 and 10.0.");
        }
        this.cgpa = cgpa;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) throws InvalidStudentDataException {
        if (branch == null || branch.trim().isEmpty()) {
            throw new InvalidStudentDataException("Branch cannot be empty.");
        }
        this.branch = branch.trim().toUpperCase();
    }

    public int getCertificationsCount() {
        return certificationsCount;
    }

    public void setCertificationsCount(int certificationsCount) throws InvalidStudentDataException {
        if (certificationsCount < 0) {
            throw new InvalidStudentDataException("Certifications count cannot be negative.");
        }
        this.certificationsCount = certificationsCount;
    }

    public int getInternshipsCount() {
        return internshipsCount;
    }

    public void setInternshipsCount(int internshipsCount) throws InvalidStudentDataException {
        if (internshipsCount < 0) {
            throw new InvalidStudentDataException("Internships count cannot be negative.");
        }
        this.internshipsCount = internshipsCount;
    }

    public List<String> getSkills() {
        return new ArrayList<>(skills); // Return copy
    }

    public void setSkills(List<String> skills) {
        this.skills = (skills != null) ? new ArrayList<>(skills) : new ArrayList<>();
    }

    public void addSkill(String skill) {
        if (skill != null && !skill.trim().isEmpty()) {
            this.skills.add(skill.trim());
        }
    }

    public String getSkillsAsString() {
        if (skills.isEmpty()) {
            return "None";
        }
        return String.join(", ", skills);
    }

    @Override
    public String toString() {
        return String.format(
            "USN: %-12s | Name: %-15s | Branch: %-5s | CGPA: %-5.2f | Certs: %-2d | Internships: %-2d | Skills: [%s]",
            usn, name, branch, cgpa, certificationsCount, internshipsCount, getSkillsAsString()
        );
    }
}