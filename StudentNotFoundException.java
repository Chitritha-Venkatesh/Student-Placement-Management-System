

/**
 * Custom exception thrown when a student search operation by USN fails to locate a match.
 */
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}