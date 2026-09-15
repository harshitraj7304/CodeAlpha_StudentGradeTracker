package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an academic subject/course in B.Tech program.
 */
public class Course implements Serializable {
    private static final long serialVersionUID = 3L;

    private final String id;
    private String courseCode;
    private String courseName;
    private int credits;
    private SubjectType subjectType;
    private final List<GradeItem> gradeItems;

    public Course(String courseCode, String courseName, int credits) {
        this(courseCode, courseName, credits, SubjectType.THEORY);
    }

    public Course(String courseCode, String courseName, int credits, SubjectType subjectType) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits > 0 ? credits : 3;
        this.subjectType = subjectType != null ? subjectType : SubjectType.THEORY;
        this.gradeItems = new ArrayList<>();
    }

    public Course(String id, String courseCode, String courseName, int credits) {
        this.id = id != null ? id : UUID.randomUUID().toString().substring(0, 8);
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits > 0 ? credits : 3;
        this.subjectType = SubjectType.THEORY;
        this.gradeItems = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits > 0 ? credits : 3;
    }

    public SubjectType getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(SubjectType subjectType) {
        this.subjectType = subjectType != null ? subjectType : SubjectType.THEORY;
    }

    public boolean isLab() {
        return subjectType == SubjectType.LAB || subjectType == SubjectType.PRACTICAL;
    }

    public void setLab(boolean lab) {
        if (lab) {
            this.subjectType = SubjectType.LAB;
        }
    }

    public List<GradeItem> getGradeItems() {
        return Collections.unmodifiableList(gradeItems);
    }

    public void addGradeItem(GradeItem item) {
        if (item != null) {
            gradeItems.add(item);
        }
    }

    public boolean removeGradeItem(String itemId) {
        return gradeItems.removeIf(item -> item.getId().equals(itemId));
    }

    /**
     * Calculates overall weighted score percentage achieved in this subject (0 - 100%).
     */
    public double getCalculatedPercentage() {
        if (gradeItems.isEmpty()) return 0.0;

        double totalWeightedScore = 0.0;
        double totalWeightage = 0.0;

        for (GradeItem item : gradeItems) {
            totalWeightedScore += item.getPercentage() * item.getWeightage();
            totalWeightage += item.getWeightage();
        }

        if (totalWeightage <= 0) {
            double sum = 0.0;
            for (GradeItem item : gradeItems) {
                sum += item.getPercentage();
            }
            return sum / gradeItems.size();
        }

        return totalWeightedScore / totalWeightage;
    }

    /**
     * Grade Point (0.0 to 10.0 scale) based on calculated percentage.
     */
    public double getGradePoint() {
        double pct = getCalculatedPercentage();
        if (gradeItems.isEmpty()) return 0.0;
        if (pct >= 90.0) return 10.0;
        if (pct >= 80.0) return 9.0;
        if (pct >= 70.0) return 8.0;
        if (pct >= 60.0) return 7.0;
        if (pct >= 50.0) return 6.0;
        if (pct >= 40.0) return 5.0;
        return 0.0; // Fail / Backlog
    }

    /**
     * Letter Grade (O, A+, A, B+, B, C, F).
     */
    public String getLetterGrade() {
        double pct = getCalculatedPercentage();
        if (gradeItems.isEmpty()) return "N/A";
        if (pct >= 90.0) return "O";   // Outstanding
        if (pct >= 80.0) return "A+";  // Excellent
        if (pct >= 70.0) return "A";   // Very Good
        if (pct >= 60.0) return "B+";  // Good
        if (pct >= 50.0) return "B";   // Above Average
        if (pct >= 40.0) return "C";   // Pass
        return "F";                    // Fail / Backlog
    }

    /**
     * Subject pass condition: Score >= 40% (Grade Point >= 5.0).
     */
    public boolean isPassed() {
        return !gradeItems.isEmpty() && getCalculatedPercentage() >= 40.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s: %s (%d Cr, %s)", courseCode, courseName, credits, getLetterGrade());
    }
}
