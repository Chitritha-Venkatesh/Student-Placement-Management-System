
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class containing the menu interface.
 * Implements robust validation and custom exception catching.
 * Persists data via file IO.
 */
public class Main {
    private static final PlacementManager manager = new PlacementManager();
    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "data/students.csv";

    public static void main(String[] presumption) {
        // Step 1: Load student database from file
        try {
            manager.loadDataFromFile(DATA_FILE);
            System.out.println("[System] Loaded student records from: " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("[System Error] Failed to load data from storage: " + e.getMessage());
        }

        // Step 2: Seed data if database is empty (First-time run)
        if (manager.getAllStudents().isEmpty()) {
            System.out.println("[System] Database is empty. Seeding initial demonstration data...");
            seedInitialData();
            saveDatabase();
        }

        boolean running = true;
        System.out.println("==================================================");
        System.out.println("      WELCOME TO PLACEMENT MANAGEMENT SYSTEM      ");
        System.out.println("==================================================");

        while (running) {
            printMenu();
            int choice = readIntegerInput("Enter your choice (1-7): ", 1, 7);

            switch (choice) {
                case 1:
                    addStudentMenu();
                    break;
                case 2:
                    viewAllStudentsMenu();
                    break;
                case 3:
                    searchStudentMenu();
                    break;
                case 4:
                    checkEligibilityMenu();
                    break;
                case 5:
                    calculateScoreMenu();
                    break;
                case 6:
                    recommendCompaniesMenu();
                    break;
                case 7:
                    System.out.println("\nSaving database changes... Goodbye!");
                    saveDatabase();
                    running = false;
                    break;
                default:
                    System.out.println("Invalid selection.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                   MAIN MENU                      ");
        System.out.println("--------------------------------------------------");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by USN");
        System.out.println("4. Check Placement Eligibility");
        System.out.println("5. Calculate Placement Score");
        System.out.println("6. Recommend Companies");
        System.out.println("7. Save & Exit");
        System.out.println("--------------------------------------------------");
    }

    /**
     * Submenu to add a student. Catches InvalidStudentDataException.
     */
    private static void addStudentMenu() {
        System.out.println("\n>>> ADD NEW STUDENT <<<");
        
        System.out.print("Enter USN (e.g., 1RV22IS001): ");
        String usn = scanner.nextLine().trim();

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Branch (e.g., ISE, CSE, ECE): ");
        String branch = scanner.nextLine().trim();

        double cgpa = readDoubleInput("Enter CGPA (0.0 to 10.0): ", 0.0, 10.0);
        int certs = readIntegerInput("Enter Number of Certifications: ", 0, 100);
        int internships = readIntegerInput("Enter Number of Internships: ", 0, 100);

        System.out.print("Enter Skills (comma separated): ");
        String skillsInput = scanner.nextLine().trim();
        List<String> skills = new ArrayList<>();
        if (!skillsInput.isEmpty()) {
            String[] tokens = skillsInput.split(",");
            for (String token : tokens) {
                if (!token.trim().isEmpty()) {
                    skills.add(token.trim());
                }
            }
        }

        try {
            // Instantiate student object. Constructor validations might throw InvalidStudentDataException.
            Student s = new Student(name, usn, cgpa, branch, certs, internships, skills);
            // Attempt to register student.
            manager.addStudent(s);
            System.out.println("\nSUCCESS: Student record successfully added!");
            System.out.println(s);
            
            // Save immediately on database change
            saveDatabase();
        } catch (InvalidStudentDataException e) {
            // Custom exception caught and reported to the user
            System.out.println("\nVALIDATION ERROR: Failed to add student. " + e.getMessage());
        }
    }

    private static void viewAllStudentsMenu() {
        System.out.println("\n>>> VIEW ALL STUDENTS <<<");
        List<Student> list = manager.getAllStudents();
        if (list.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.println("Total Records: " + list.size());
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
        for (Student s : list) {
            System.out.println(s);
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------------");
    }

    /**
     * Submenu to search. Catches StudentNotFoundException.
     */
    private static void searchStudentMenu() {
        System.out.println("\n>>> SEARCH STUDENT BY USN <<<");
        System.out.print("Enter USN to search: ");
        String usn = scanner.nextLine().trim();

        try {
            Student s = manager.searchStudentByUsn(usn);
            System.out.println("\nRecord Found:");
            System.out.println("--------------------------------------------------");
            System.out.println("Name:           " + s.getName());
            System.out.println("USN:            " + s.getUsn());
            System.out.println("Branch:         " + s.getBranch());
            System.out.println("CGPA:           " + s.getCgpa());
            System.out.println("Certifications: " + s.getCertificationsCount());
            System.out.println("Internships:    " + s.getInternshipsCount());
            System.out.println("Skills:         " + s.getSkillsAsString());
            System.out.println("--------------------------------------------------");
        } catch (StudentNotFoundException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    /**
     * Submenu to check placement eligibility. Catches StudentNotFoundException.
     */
    private static void checkEligibilityMenu() {
        System.out.println("\n>>> CHECK PLACEMENT ELIGIBILITY <<<");
        System.out.print("Enter student USN: ");
        String usn = scanner.nextLine().trim();

        try {
            Student s = manager.searchStudentByUsn(usn);
            boolean eligible = manager.checkEligibility(s);
            System.out.println("\nPlacement Eligibility Result:");
            System.out.println("--------------------------------------------------");
            System.out.println("Student Name: " + s.getName());
            System.out.println("CGPA:         " + s.getCgpa());
            System.out.println("STATUS:       " + (eligible ? "ELIGIBLE" : "NOT ELIGIBLE"));
            System.out.println("Reason:       " + (eligible ? "CGPA satisfies minimum 7.5 requirement." 
                                                          : "CGPA is below the minimum 7.5 requirement."));
            System.out.println("--------------------------------------------------");
        } catch (StudentNotFoundException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    /**
     * Submenu to calculate score. Catches StudentNotFoundException.
     */
    private static void calculateScoreMenu() {
        System.out.println("\n>>> CALCULATE PLACEMENT SCORE <<<");
        System.out.print("Enter student USN: ");
        String usn = scanner.nextLine().trim();

        try {
            Student s = manager.searchStudentByUsn(usn);
            double score = manager.calculatePlacementScore(s);
            double cgpaContribution = s.getCgpa() * 6.0;
            int validCerts = Math.min(s.getCertificationsCount(), 4);
            int validInternships = Math.min(s.getInternshipsCount(), 2);

            System.out.println("\nPlacement Score Summary:");
            System.out.println("--------------------------------------------------");
            System.out.println("Student Name:          " + s.getName());
            System.out.println("CGPA Contribution:     " + String.format("%.2f", cgpaContribution));
            System.out.println("Certifications Cont.:  " + (validCerts * 5) + " (" + validCerts + " valid * 5)");
            System.out.println("Internships Cont.:     " + (validInternships * 10) + " (" + validInternships + " valid * 10)");
            System.out.println("--------------------------------------------------");
            System.out.println("FINAL PLACEMENT SCORE: " + String.format("%.2f", score) + " / 100.0");
            System.out.println("--------------------------------------------------");
        } catch (StudentNotFoundException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    /**
     * Submenu to recommend companies. Catches StudentNotFoundException.
     */
    private static void recommendCompaniesMenu() {
        System.out.println("\n>>> RECOMMEND COMPANIES <<<");
        System.out.print("Enter student USN: ");
        String usn = scanner.nextLine().trim();

        try {
            Student s = manager.searchStudentByUsn(usn);
            double score = manager.calculatePlacementScore(s);
            List<String> recommendations = manager.getCompanyRecommendations(s);

            System.out.println("\nCompany Recommendations:");
            System.out.println("--------------------------------------------------");
            System.out.println("Student Name:    " + s.getName());
            System.out.println("Placement Score: " + String.format("%.2f", score));
            System.out.println("Recommendations: " + String.join(", ", recommendations));
            System.out.println("--------------------------------------------------");
        } catch (StudentNotFoundException e) {
            System.out.println("\nERROR: " + e.getMessage());
        }
    }

    /**
     * Saves changes to the file system.
     */
    private static void saveDatabase() {
        try {
            manager.saveDataToFile(DATA_FILE);
            System.out.println("[System] Database changes successfully saved to disk.");
        } catch (IOException e) {
            System.out.println("[System Error] Failed to write database to disk: " + e.getMessage());
        }
    }

    /**
     * Seed initial mock data for first execution.
     */
    private static void seedInitialData() {
        try {
            manager.addStudent(new Student(
                "Aditya Sen", "1RV22IS001", 9.2, "ISE", 
                3, 2, Arrays.asList("Java", "Spring Boot", "SQL")
            ));
            manager.addStudent(new Student(
                "Bhumika Gowda", "1RV22IS012", 8.4, "ISE", 
                2, 1, Arrays.asList("Python", "Machine Learning", "HTML")
            ));
            manager.addStudent(new Student(
                "Chaitanya K", "1RV22IS019", 7.1, "ISE", 
                1, 0, Arrays.asList("C++", "Data Structures")
            ));
            manager.addStudent(new Student(
                "Deepa Nair", "1RV22IS025", 7.8, "ISE", 
                0, 0, Arrays.asList("Javascript", "Web Development")
            ));
        } catch (InvalidStudentDataException e) {
            System.err.println("Critical Error seeding data: " + e.getMessage());
        }
    }

    private static int readIntegerInput(String prompt, int min, int max) {
        int val;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    break;
                } else {
                    System.out.println("Value out of range (" + min + " - " + max + ").");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
        return val;
    }

    private static double readDoubleInput(String prompt, double min, double max) {
        double val;
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    break;
                } else {
                    System.out.println("Value out of range (" + min + " - " + max + ").");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a decimal number.");
            }
        }
        return val;
    }
}