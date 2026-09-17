package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Model representing a B.Tech Student in the system.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 2L;

    private String studentId;
    private String name;
    private String email;
    private String department; // Branch / Stream (e.g. CSE)
    private int currentSemester; // 1 to 8
    private final Map<Integer, Semester> semesters;

    public Student(String studentId, String name, String email, String department) {
        this(studentId, name, email, department, 1);
    }

    public Student(String studentId, String name, String email, String department, int currentSemester) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.department = department != null && !department.trim().isEmpty() ? department : "CSE";
        this.currentSemester = currentSemester >= 1 && currentSemester <= 8 ? currentSemester : 1;
        this.semesters = new HashMap<>();

        // Initialize default semesters 1 through 8
        for (int i = 1; i <= 8; i++) {
            semesters.put(i, new Semester(i));
        }
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(int currentSemester) {
        this.currentSemester = currentSemester >= 1 && currentSemester <= 8 ? currentSemester : 1;
    }

    public Semester getSemester(int semNum) {
        if (!semesters.containsKey(semNum)) {
            semesters.put(semNum, new Semester(semNum));
        }
        return semesters.get(semNum);
    }

    public Map<Integer, Semester> getSemesters() {
        return Collections.unmodifiableMap(semesters);
    }

    // --- Backward Compatibility methods for flat Course access ---
    public List<Course> getCourses() {
        List<Course> all = new ArrayList<>();
        for (Semester s : semesters.values()) {
            all.addAll(s.getSubjects());
        }
        return Collections.unmodifiableList(all);
    }

    public void addCourse(Course course) {
        getSemester(currentSemester).addSubject(course);
    }

    public boolean removeCourse(String courseId) {
        boolean removed = false;
        for (Semester s : semesters.values()) {
            if (s.removeSubject(courseId)) {
                removed = true;
            }
        }
        return removed;
    }

    public Course getCourseById(String courseId) {
        for (Semester s : semesters.values()) {
            for (Course c : s.getSubjects()) {
                if (c.getId().equals(courseId)) {
                    return c;
                }
            }
        }
        return null;
    }

    // --- Cumulative Performance Metrics ---

    /**
     * Total Registered Credits across all semesters.
     */
    public int getTotalCredits() {
        int total = 0;
        for (Semester s : semesters.values()) {
            total += s.getTotalCredits();
        }
        return total;
    }

    /**
     * Total Earned Credits (passed subjects) across all semesters.
     */
    public int getEarnedCredits() {
        int earned = 0;
        for (Semester s : semesters.values()) {
            earned += s.getEarnedCredits();
        }
        return earned;
    }

    /**
     * Count of active backlogs across all semesters.
     */
    public int getBacklogCount() {
        int backlogs = 0;
        for (Semester s : semesters.values()) {
            backlogs += s.getBacklogCount();
        }
        return backlogs;
    }

    /**
     * Total Graded Credits across all semesters.
     */
    public int getGradedCredits() {
        int graded = 0;
        for (Semester s : semesters.values()) {
            graded += s.getGradedCredits();
        }
        return graded;
    }

    /**
     * Calculates Cumulative Grade Point Average (CGPA) on a 10.0 scale across all active semesters.
     * Semesters are weighted by graded credits only to avoid distorting partial semesters.
     */
    public double getCgpa() {
        double totalWeightedSgpa = 0.0;
        int totalCredits = 0;

        for (Semester s : semesters.values()) {
            int gradedCredits = s.getGradedCredits();
            if (gradedCredits > 0) {
                totalWeightedSgpa += s.getSgpa() * gradedCredits;
                totalCredits += gradedCredits;
            }
        }

        return totalCredits > 0 ? totalWeightedSgpa / totalCredits : 0.0;
    }

    /**
     * Equivalent percentage using standard AICTE formula: (CGPA - 0.75) * 10
     */
    public double getEquivalentPercentage() {
        double cgpa = getCgpa();
        if (cgpa <= 0) return 0.0;
        return Math.max(0.0, (cgpa - 0.75) * 10.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(studentId, student.studentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, Sem %d, CGPA: %.2f)", studentId, name, department, currentSemester, getCgpa());
    }
}
