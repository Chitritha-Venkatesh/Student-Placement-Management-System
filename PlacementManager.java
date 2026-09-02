
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class that manages student profiles in memory (ArrayList)
 * and persists data into a CSV file.
 * Implements PlacementOperations interface.
 */
public class PlacementManager implements PlacementOperations {
    private final List<Student> students;

    public PlacementManager() {
        this.students = new ArrayList<>();
    }

    /**
     * Registers a new student and saves changes to file.
     * @param student The student object to register
     * @throws InvalidStudentDataException if student is null or duplicate USN exists
     */
    public void addStudent(Student student) throws InvalidStudentDataException {
        if (student == null) {
            throw new InvalidStudentDataException("Student data cannot be null.");
        }
        
        // Check for duplicates
        try {
            Student existing = searchStudentByUsn(student.getUsn());
            if (existing != null) {
                throw new InvalidStudentDataException("Student with USN " + student.getUsn() + " already exists.");
            }
        } catch (StudentNotFoundException e) {
            // This is good, student does not exist, so we can add
        }

        students.add(student);
    }

    /**
     * Gets a list of all current students.
     */
    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    /**
     * Search student by USN. Throws StudentNotFoundException if missing.
     * @param usn University Seat Number
     * @return Student record
     * @throws StudentNotFoundException if not found
     */
    public Student searchStudentByUsn(String usn) throws StudentNotFoundException {
        if (usn == null || usn.trim().isEmpty()) {
            throw new StudentNotFoundException("Search query was empty.");
        }
        String targetUsn = usn.trim().toUpperCase();
        for (Student student : students) {
            if (student.getUsn().equals(targetUsn)) {
                return student;
            }
        }
        throw new StudentNotFoundException("Student with USN " + targetUsn + " not found.");
    }

    // Implementing interface methods

    @Override
    public boolean checkEligibility(Student student) {
        if (student == null) return false;
        return student.getCgpa() >= 7.5;
    }

    @Override
    public double calculatePlacementScore(Student student) {
        if (student == null) return 0.0;

        double cgpaContribution = student.getCgpa() * 6.0;
        int validCerts = Math.min(student.getCertificationsCount(), 4);
        double certContribution = validCerts * 5.0;

        int validInternships = Math.min(student.getInternshipsCount(), 2);
        double internshipContribution = validInternships * 10.0;

        return Math.min(cgpaContribution + certContribution + internshipContribution, 100.0);
    }

    @Override
    public List<String> getCompanyRecommendations(Student student) {
        if (student == null) {
            return Arrays.asList("No data");
        }

        double score = calculatePlacementScore(student);
        if (score > 80.0) {
            return Arrays.asList("Google", "Microsoft", "Amazon");
        } else if (score > 60.0) {
            return Arrays.asList("Infosys", "Accenture", "TCS Digital");
        } else {
            return Arrays.asList("Skill Improvement Required");
        }
    }

    // --- File Persistence (Advanced I/O) ---

    /**
     * Saves all in-memory student profiles to a CSV file.
     * @param filePath Path to target csv file
     * @throws IOException on write failures
     */
    public void saveDataToFile(String filePath) throws IOException {
        File file = new File(filePath);
        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV Header
            writer.write("Name,USN,CGPA,Branch,Certifications,Internships,Skills");
            writer.newLine();

            for (Student student : students) {
                // Skills list is saved as a semicolon-separated string to avoid breaking commas in CSV
                String skillsString = String.join(";", student.getSkills());
                String line = String.format("%s,%s,%.2f,%s,%d,%d,%s",
                        student.getName(),
                        student.getUsn(),
                        student.getCgpa(),
                        student.getBranch(),
                        student.getCertificationsCount(),
                        student.getInternshipsCount(),
                        skillsString
                );
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Loads student profiles from CSV. Gracefully handles missing files by leaving system empty.
     * @param filePath Path to csv file
     * @throws IOException on parsing issues
     */
    public void loadDataFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return; // No file to load, start clean
        }

        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine(); // Skip Header
            if (header == null) return;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Split line by commas
                String[] data = line.split(",", -1);
                if (data.length < 7) continue;

                try {
                    String name = data[0].trim();
                    String usn = data[1].trim();
                    double cgpa = Double.parseDouble(data[2].trim());
                    String branch = data[3].trim();
                    int certs = Integer.parseInt(data[4].trim());
                    int internships = Integer.parseInt(data[5].trim());
                    
                    // Parse semicolon separated skills
                    List<String> skills = new ArrayList<>();
                    if (!data[6].trim().isEmpty()) {
                        String[] skillTokens = data[6].split(";");
                        for (String s : skillTokens) {
                            if (!s.trim().isEmpty()) {
                                skills.add(s.trim());
                            }
                        }
                    }

                    // Reconstruct student object and add directly
                    Student student = new Student(name, usn, cgpa, branch, certs, internships, skills);
                    students.add(student);
                } catch (NumberFormatException | InvalidStudentDataException e) {
                    // Log error and continue reading next lines
                    System.err.println("Warning: Skipped corrupt record: " + line + " (Reason: " + e.getMessage() + ")");
                }
            }
        }
    }
}