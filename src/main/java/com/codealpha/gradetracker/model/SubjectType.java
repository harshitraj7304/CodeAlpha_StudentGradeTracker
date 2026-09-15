package com.codealpha.gradetracker.model;

/**
 * Subject types for B.Tech academic curriculum.
 */
public enum SubjectType {
    THEORY("Theory", "Core lecture course"),
    PRACTICAL("Practical", "Hands-on practical session"),
    LAB("Lab Practical", "Laboratory course"),
    PROJECT("Capstone Project", "Project / Dissertation"),
    ELECTIVE("Department Elective", "Specialized elective course"),
    INTERNSHIP("Industry Internship", "Industrial training"),
    SEMINAR("Technical Seminar", "Presentation & research seminar");

    private final String displayName;
    private final String description;

    SubjectType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
