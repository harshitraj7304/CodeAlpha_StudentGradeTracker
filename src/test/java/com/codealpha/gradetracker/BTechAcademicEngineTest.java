package com.codealpha.gradetracker;

import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;

import java.util.Arrays;
import java.util.List;

/**
 * Unit Test Suite for B.Tech Academic Engine (SGPA, CGPA, Backlogs, Leaderboards).
 */
public class BTechAcademicEngineTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println(" Running B.Tech Academic Engine Verification Suite");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        try {
            testSgpaCalculation();
            System.out.println("[PASS] Semester SGPA Calculation Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Semester SGPA Calculation Test: " + e.getMessage());
            failed++;
        }

        try {
            testCumulativeCgpaCalculation();
            System.out.println("[PASS] Multi-Semester Cumulative CGPA Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Multi-Semester Cumulative CGPA Test: " + e.getMessage());
            failed++;
        }

        try {
            testBacklogDetection();
            System.out.println("[PASS] Backlog & Earned Credits Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Backlog & Earned Credits Test: " + e.getMessage());
            failed++;
        }

        try {
            testAictePercentageFormula();
            System.out.println("[PASS] AICTE Percentage Conversion Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] AICTE Percentage Conversion Test: " + e.getMessage());
            failed++;
        }

        try {
            testClassLeaderboardRanking();
            System.out.println("[PASS] Class Leaderboard Rank Sorting Test");
            passed++;
        } catch (Throwable e) {
            System.err.println("[FAIL] Class Leaderboard Rank Sorting Test: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.printf(" Results: %d Passed, %d Failed\n", passed, failed);
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String msg) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(String.format("%s - Expected: %.2f, Got: %.2f", msg, expected, actual));
        }
    }

    private static void testSgpaCalculation() {
        Semester sem = new Semester(1);

        // Subject 1 (4 credits): 90% (Grade Point 10.0)
        Course sub1 = new Course("CS101", "C Programming", 4);
        sub1.addGradeItem(new GradeItem("Exam", 90, 100, AssessmentCategory.END_SEM, 1.0));
        sem.addSubject(sub1);

        // Subject 2 (3 credits): 70% (Grade Point 8.0)
        Course sub2 = new Course("MA101", "Math I", 3);
        sub2.addGradeItem(new GradeItem("Exam", 70, 100, AssessmentCategory.END_SEM, 1.0));
        sem.addSubject(sub2);

        // Total Credits = 7
        // SGPA = (10.0 * 4 + 8.0 * 3) / 7 = (40 + 24) / 7 = 64 / 7 = 9.14
        assertEquals(9.14, sem.getSgpa(), 0.02, "SGPA calculation");
    }

    private static void testCumulativeCgpaCalculation() {
        Student student = new Student("S101", "Alice", "a@test.com", "CSE", 2);

        // Sem 1 (7 credits): SGPA 10.0
        Semester sem1 = student.getSemester(1);
        Course s1 = new Course("C1", "Sub 1", 7);
        s1.addGradeItem(new GradeItem("Exam", 95, 100, AssessmentCategory.END_SEM, 1.0));
        sem1.addSubject(s1);

        // Sem 2 (5 credits): SGPA 8.0
        Semester sem2 = student.getSemester(2);
        Course s2 = new Course("C2", "Sub 2", 5);
        s2.addGradeItem(new GradeItem("Exam", 75, 100, AssessmentCategory.END_SEM, 1.0));
        sem2.addSubject(s2);

        // Total Credits = 12
        // CGPA = (10.0 * 7 + 8.0 * 5) / 12 = (70 + 40) / 12 = 110 / 12 = 9.17
        assertEquals(9.17, student.getCgpa(), 0.02, "Cumulative CGPA calculation");
    }

    private static void testBacklogDetection() {
        Student student = new Student("S102", "Charlie", "c@test.com", "CSE", 1);
        Semester sem1 = student.getSemester(1);

        // Pass Subject (4 credits, 80%)
        Course passSub = new Course("CS1", "Passed Sub", 4);
        passSub.addGradeItem(new GradeItem("Exam", 80, 100, AssessmentCategory.END_SEM, 1.0));
        sem1.addSubject(passSub);

        // Fail Subject (3 credits, 35% < 40%)
        Course failSub = new Course("MA1", "Failed Sub", 3);
        failSub.addGradeItem(new GradeItem("Exam", 35, 100, AssessmentCategory.END_SEM, 1.0));
        sem1.addSubject(failSub);

        assert student.getBacklogCount() == 1 : "Should detect 1 active backlog";
        assert student.getEarnedCredits() == 4 : "Earned credits should exclude backlogged subject";
        assert student.getTotalCredits() == 7 : "Total credits should include all subjects";
    }

    private static void testAictePercentageFormula() {
        // CGPA = 8.5 -> Percentage = (8.5 - 0.75) * 10 = 77.5%
        double pct = AcademicEngineService.convertCgpaToPercentage(8.5);
        assertEquals(77.5, pct, 0.01, "AICTE percentage formula");
    }

    private static void testClassLeaderboardRanking() {
        Student s1 = new Student("S1", "Alice", "a@test.com", "CSE");
        s1.getSemester(1).addSubject(new Course("C1", "Sub1", 4));
        s1.getSemester(1).getSubjects().get(0).addGradeItem(new GradeItem("Exam", 95, 100, AssessmentCategory.END_SEM, 1.0));

        Student s2 = new Student("S2", "Bob", "b@test.com", "CSE");
        s2.getSemester(1).addSubject(new Course("C1", "Sub1", 4));
        s2.getSemester(1).getSubjects().get(0).addGradeItem(new GradeItem("Exam", 75, 100, AssessmentCategory.END_SEM, 1.0));

        List<Student> ranked = AcademicEngineService.getRankedStudents(Arrays.asList(s2, s1));
        assert "Alice".equals(ranked.get(0).getName()) : "Alice with higher CGPA should rank #1";
    }
}
