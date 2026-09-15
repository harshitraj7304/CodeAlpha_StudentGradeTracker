package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an academic semester (Semesters 1 - 8) in the B.Tech program.
 */
public class Semester implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private int semesterNumber; // 1 to 8
    private final List<Course> subjects; // Course alias for Subject

    public Semester(int semesterNumber) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.semesterNumber = semesterNumber >= 1 && semesterNumber <= 8 ? semesterNumber : 1;
        this.subjects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(int semesterNumber) {
        this.semesterNumber = semesterNumber >= 1 && semesterNumber <= 8 ? semesterNumber : 1;
    }

    public List<Course> getSubjects() {
        return Collections.unmodifiableList(subjects);
    }

    public void addSubject(Course subject) {
        if (subject != null && !subjects.contains(subject)) {
            subjects.add(subject);
        }
    }

    public boolean removeSubject(String subjectId) {
        return subjects.removeIf(s -> s.getId().equals(subjectId));
    }

    public Course getSubjectByCode(String code) {
        for (Course c : subjects) {
            if (c.getCourseCode().equalsIgnoreCase(code)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Total credits registered in this semester.
     */
    public int getTotalCredits() {
        int total = 0;
        for (Course c : subjects) {
            total += c.getCredits();
        }
        return total;
    }

    /**
     * Earned credits in this semester (credits for subjects passed with >= 40% score).
     */
    public int getEarnedCredits() {
        int earned = 0;
        for (Course c : subjects) {
            if (c.isPassed()) {
                earned += c.getCredits();
            }
        }
        return earned;
    }

    /**
     * Count of active backlogs (failed subjects) in this semester.
     */
    public int getBacklogCount() {
        int count = 0;
        for (Course c : subjects) {
            if (!c.isPassed() && !c.getGradeItems().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Computes the Semester Grade Point Average (SGPA) on a 10.0 scale.
     * SGPA = Sum(Subject Grade Point * Credits) / Sum(Semester Credits)
     */
    public double getSgpa() {
        if (subjects.isEmpty()) return 0.0;

        double totalWeightedGradePoints = 0.0;
        int totalCredits = 0;

        for (Course c : subjects) {
            if (!c.getGradeItems().isEmpty()) {
                totalWeightedGradePoints += c.getGradePoint() * c.getCredits();
                totalCredits += c.getCredits();
            }
        }

        return totalCredits > 0 ? totalWeightedGradePoints / totalCredits : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Semester semester = (Semester) o;
        return semesterNumber == semester.semesterNumber && Objects.equals(id, semester.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, semesterNumber);
    }

    @Override
    public String toString() {
        return String.format("Semester %d (SGPA: %.2f | Credits: %d/%d)",
                semesterNumber, getSgpa(), getEarnedCredits(), getTotalCredits());
    }
}
