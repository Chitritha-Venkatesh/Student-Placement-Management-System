

/**
 * Custom exception thrown when a student's data fails validation checks.
 * Examples: negative certifications/internships count, CGPA not in 0.0 - 10.0 range.
 */
public class InvalidStudentDataException extends Exception {
    public InvalidStudentDataException(String message) {
        super(message);
    }
}