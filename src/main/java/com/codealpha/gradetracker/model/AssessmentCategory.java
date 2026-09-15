package com.codealpha.gradetracker.model;

/**
 * Categories for different types of academic assessments in B.Tech program.
 */
public enum AssessmentCategory {
    ASSIGNMENT("Assignment", 0.15),
    QUIZ("Quiz", 0.10),
    MIDTERM("Midterm Exam", 0.25),
    END_SEM("End-Sem Examination", 0.40),
    FINAL_EXAM("Final Exam", 0.40), // Alias for backward compatibility
    LAB_PRACTICAL("Lab Practical", 0.20),
    PROJECT("Cap-stone Project", 0.30),
    OTHER("Other", 0.10);

    private final String displayName;
    private final double defaultWeightage;

    AssessmentCategory(String displayName, double defaultWeightage) {
        this.displayName = displayName;
        this.defaultWeightage = defaultWeightage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultWeightage() {
        return defaultWeightage;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
