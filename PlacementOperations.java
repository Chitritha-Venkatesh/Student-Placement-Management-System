

import java.util.List;

/**
 * Interface defining the operational contract for placement assessments.
 * Highlights the concept of abstraction and interfaces (polymorphism).
 */
public interface PlacementOperations {
    /**
     * Checks if the student meets minimum CGPA rules for placements.
     * @param student The student profile to check
     * @return true if eligible, false otherwise
     */
    boolean checkEligibility(Student student);

    /**
     * Calculates the placement score based on credentials.
     * @param student The student profile to check
     * @return calculated score out of 100.0
     */
    double calculatePlacementScore(Student student);

    /**
     * Recommends recruiters based on the calculated score.
     * @param student The student profile to check
     * @return List of company names recommended
     */
    List<String> getCompanyRecommendations(Student student);
}