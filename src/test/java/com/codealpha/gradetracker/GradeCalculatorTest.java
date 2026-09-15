package com.codealpha.gradetracker;

import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.util.GradeCalculator;

import java.util.Arrays;
import java.util.Map;

/**
 * Verification test suite for GradeCalculator and domain models.
 */
public class GradeCalculatorTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Running Phase 1 GradeCalculator Test Suite");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        try {
            testLetterGradeMapping();
            System.out.println("[PASS] Letter Grade Mapping Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Letter Grade Mapping Test: " + e.getMessage());
            failed++;
        }

        try {
            testGpaCalculations();
            System.out.println("[PASS] GPA Scale Calculations Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] GPA Scale Calculations Test: " + e.getMessage());
            failed++;
        }

        try {
            testWeightedCoursePercentage();
            System.out.println("[PASS] Weighted Course Percentage Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Weighted Course Percentage Test: " + e.getMessage());
            failed++;
        }

        try {
            testStudentCumulativeGpa();
            System.out.println("[PASS] Student Cumulative GPA Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Student Cumulative GPA Test: " + e.getMessage());
            failed++;
        }

        try {
            testGradeDistribution();
            System.out.println("[PASS] Grade Distribution Analytics Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Grade Distribution Analytics Test: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.printf(" Results: %d Passed, %d Failed\n", passed, failed);
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(String.format("%s - Expected: %.2f, Got: %.2f", message, expected, actual));
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(String.format("%s - Expected: %s, Got: %s", message, expected, actual));
        }
    }

    private static void testLetterGradeMapping() {
        assertEquals("A+", GradeCalculator.getLetterGrade(95.0), "95% should map to A+");
        assertEquals("A", GradeCalculator.getLetterGrade(87.0), "87% should map to A");
        assertEquals("B+", GradeCalculator.getLetterGrade(78.0), "78% should map to B+");
        assertEquals("C", GradeCalculator.getLetterGrade(57.0), "57% should map to C");
        assertEquals("F", GradeCalculator.getLetterGrade(42.0), "42% should map to F");
    }

    private static void testGpaCalculations() {
        assertEquals(4.0, GradeCalculator.getGpa4Scale(92.0), 0.01, "92% GPA 4.0 scale");
        assertEquals(3.0, GradeCalculator.getGpa4Scale(72.0), 0.01, "72% GPA 4.0 scale");
        assertEquals(0.0, GradeCalculator.getGpa4Scale(40.0), 0.01, "40% GPA 4.0 scale");

        assertEquals(10.0, GradeCalculator.getGpa10Scale(92.0), 0.01, "92% GPA 10.0 scale");
        assertEquals(7.5, GradeCalculator.getGpa10Scale(72.0), 0.01, "72% GPA 10.0 scale");
    }

    private static void testWeightedCoursePercentage() {
        Course cs101 = new Course("CS101", "Computer Science 1", 4);
        // Assignment 1: 90/100 (90%) with 30% weight
        cs101.addGradeItem(new GradeItem("Assignment 1", 90, 100, AssessmentCategory.ASSIGNMENT, 0.30));
        // Midterm: 80/100 (80%) with 30% weight
        cs101.addGradeItem(new GradeItem("Midterm Exam", 80, 100, AssessmentCategory.MIDTERM, 0.30));
        // Final Exam: 100/100 (100%) with 40% weight
        cs101.addGradeItem(new GradeItem("Final Exam", 100, 100, AssessmentCategory.FINAL_EXAM, 0.40));

        // Weighted sum = 90*0.3 + 80*0.3 + 100*0.4 = 27 + 24 + 40 = 91.0%
        assertEquals(91.0, cs101.getCalculatedPercentage(), 0.01, "Weighted course percentage should be 91.0%");
    }

    private static void testStudentCumulativeGpa() {
        Student s1 = new Student("S101", "Alice Smith", "alice@univ.edu", "Computer Science");

        // Course 1 (4 credits): 90% (A+) -> GPA 4.0
        Course c1 = new Course("CS101", "CS 1", 4);
        c1.addGradeItem(new GradeItem("Exam", 90, 100, AssessmentCategory.FINAL_EXAM, 1.0));
        s1.addCourse(c1);

        // Course 2 (3 credits): 70% (B) -> GPA 3.0
        Course c2 = new Course("MATH201", "Calculus", 3);
        c2.addGradeItem(new GradeItem("Exam", 70, 100, AssessmentCategory.FINAL_EXAM, 1.0));
        s1.addCourse(c2);

        GradeSummary summary = GradeCalculator.calculateStudentSummary(s1);

        // Total Credits = 7
        // Weighted Percentage = (90*4 + 70*3) / 7 = (360 + 210) / 7 = 570 / 7 = 81.43%
        // Weighted GPA4 = (4.0*4 + 3.0*3) / 7 = (16 + 9) / 7 = 25 / 7 = 3.57
        assertEquals(81.43, summary.getOverallPercentage(), 0.05, "Student overall percentage");
        assertEquals(3.57, summary.getGpa4Scale(), 0.02, "Student cumulative GPA (4.0 scale)");
    }

    private static void testGradeDistribution() {
        Student s1 = new Student("S1", "Alice", "a@test.com", "CS");
        Course c1 = new Course("C1", "Course 1", 3);
        c1.addGradeItem(new GradeItem("Final", 95, 100, AssessmentCategory.FINAL_EXAM, 1.0));
        s1.addCourse(c1);

        Student s2 = new Student("S2", "Bob", "b@test.com", "CS");
        Course c2 = new Course("C1", "Course 1", 3);
        c2.addGradeItem(new GradeItem("Final", 75, 100, AssessmentCategory.FINAL_EXAM, 1.0));
        s2.addCourse(c2);

        Map<String, Integer> dist = GradeCalculator.calculateGradeDistribution(Arrays.asList(s1, s2));
        assertEquals(1, (int) dist.get("A+"), "One student should have A+");
        assertEquals(1, (int) dist.get("B+"), "One student should have B+");
    }
}
