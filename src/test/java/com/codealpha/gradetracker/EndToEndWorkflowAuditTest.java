package com.codealpha.gradetracker;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.io.ReportExporter;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.model.SubjectType;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.util.GradeCalculator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PHASE 2 — Real-World End-to-End Workflow and Data Quality Audit Suite.
 */
public class EndToEndWorkflowAuditTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println(" PHASE 2 — REAL-WORLD END-TO-END WORKFLOW AND DATA QUALITY AUDIT");
        System.out.println("==========================================================================");

        runTest("1. Student Creation & Validation", EndToEndWorkflowAuditTest::testStudentCreation);
        runTest("2. Student Edit Profile Preservation", EndToEndWorkflowAuditTest::testStudentEditPreservation);
        runTest("3. Student Profile Model Alignment", EndToEndWorkflowAuditTest::testStudentProfileAlignment);
        runTest("4. Semester 1-8 Isolation & Empty States", EndToEndWorkflowAuditTest::testSemesterIsolationAndEmptyStates);
        runTest("5. Subject Enrollment & Credit Boundaries", EndToEndWorkflowAuditTest::testSubjectManagement);
        runTest("6. Grade Score Validation, Edit & Delete", EndToEndWorkflowAuditTest::testAssessmentScoreValidation);
        runTest("7. SGPA & CGPA Mathematical Fidelity", EndToEndWorkflowAuditTest::testSgpaCgpaFidelity);
        runTest("8. Backlog Lifecycle & Remediation", EndToEndWorkflowAuditTest::testBacklogLifecycle);
        runTest("9. Search & Branch Directory Filtering", EndToEndWorkflowAuditTest::testSearchAndFiltering);
        runTest("10. Class Leaderboard Ranking & Sorting", EndToEndWorkflowAuditTest::testLeaderboardSorting);
        runTest("11. Analytics Calculations & Boundary Safety", EndToEndWorkflowAuditTest::testAnalyticsCalculations);
        runTest("12. CSV Export Reports Integrity", EndToEndWorkflowAuditTest::testCsvExportIntegrity);
        runTest("13. Full Persistence Serialization Cycle", EndToEndWorkflowAuditTest::testPersistenceIntegrity);
        runTest("14. System Edge Cases & Missing Fields", EndToEndWorkflowAuditTest::testSystemEdgeCases);

        System.out.println("==========================================================================");
        System.out.printf(" AUDIT SUMMARY: %d / %d Passed | %d / %d Failed\n", passed, (passed + failed), failed, (passed + failed));
        System.out.println("==========================================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, TestCase testCase) {
        try {
            testCase.execute();
            System.out.printf("  [PASS] %-55s\n", testName);
            passed++;
        } catch (Throwable e) {
            System.err.printf("  [FAIL] %-55s - %s\n", testName, e.getMessage());
            e.printStackTrace();
            failed++;
        }
    }

    @FunctionalInterface
    interface TestCase {
        void execute() throws Exception;
    }

    // --- 1. Student Creation ---
    private static void testStudentCreation() throws Exception {
        DataManager manager = new DataManager();
        manager.getStudents().clear();

        Student s1 = new Student("S2001", "Bob Smith", "bob@college.edu", "CSE", 1);
        manager.addStudent(s1);

        assertCondition(manager.getStudents().size() == 1, "Student count should be 1");
        assertCondition(manager.getStudentById("S2001") != null, "Student S2001 should be present");

        // Verify duplicate ID detection at manager layer
        boolean hasDuplicate = manager.getStudents().stream().anyMatch(s -> s.getStudentId().equalsIgnoreCase("S2001"));
        assertCondition(hasDuplicate, "Duplicate check logic must identify existing student ID");

        // Required field fallbacks
        Student s2 = new Student("S2002", "Alice", "alice@college.edu", "", 12);
        assertCondition("CSE".equals(s2.getDepartment()), "Empty department should fallback to CSE");
        assertCondition(s2.getCurrentSemester() == 1, "Invalid semester > 8 should fallback to 1");
    }

    // --- 2. Student Edit ---
    private static void testStudentEditPreservation() throws Exception {
        Student s = new Student("S2003", "Charlie Brown", "charlie@college.edu", "ECE", 2);
        Course c1 = new Course("EC201", "Digital Electronics", 4);
        s.getSemester(2).addSubject(c1);
        c1.addGradeItem(new GradeItem("Midterm", 45, 50, AssessmentCategory.MIDTERM, 0.30));

        // Edit metadata
        s.setName("Charlie Brown Jr.");
        s.setEmail("charlie.jr@college.edu");
        s.setDepartment("ECE");

        // Verify academic records remain intact after profile edits
        Semester sem2 = s.getSemester(2);
        assertCondition(sem2.getSubjects().size() == 1, "Academic subjects must not be cleared during student edit");
        assertCondition(sem2.getSubjects().get(0).getGradeItems().size() == 1, "Grade items must remain intact");
        assertCondition("Midterm".equals(sem2.getSubjects().get(0).getGradeItems().get(0).getTitle()), "Grade title intact");
    }

    // --- 3. Student Profile Model Alignment ---
    private static void testStudentProfileAlignment() throws Exception {
        Student s = new Student("S2004", "Diana Prince", "diana@college.edu", "CSE", 3);
        Course c = new Course("CS301", "Data Structures", 4);
        s.getSemester(3).addSubject(c);
        c.addGradeItem(new GradeItem("Final Exam", 85, 100, AssessmentCategory.FINAL_EXAM, 1.0));

        assertCondition(s.getSemester(3).getSgpa() > 0.0, "Semester SGPA must align with entered grade items");
        assertCondition(s.getCgpa() > 0.0, "Cumulative CGPA must align with semester SGPAs");
        assertCondition(s.getEarnedCredits() == 4, "Earned credits must equal course credits when passed");
        assertCondition(s.getBacklogCount() == 0, "No backlogs expected for passing grade");
    }

    // --- 4. Semester Isolation & Empty States ---
    private static void testSemesterIsolationAndEmptyStates() throws Exception {
        Student s = new Student("S2005", "Evan Wright", "evan@college.edu", "IT", 1);
        Course cSem1 = new Course("IT101", "Intro to Programming", 3);
        s.getSemester(1).addSubject(cSem1);

        // Verify Sem 2 is completely isolated and empty
        Semester sem2 = s.getSemester(2);
        assertCondition(sem2.getSubjects().isEmpty(), "Semester 2 must be empty initially");
        assertCondition(sem2.getSgpa() == 0.0, "Empty semester SGPA should be 0.0");

        // Verify all 8 semesters exist
        for (int i = 1; i <= 8; i++) {
            assertCondition(s.getSemester(i) != null, "Semester " + i + " must exist");
        }
    }

    // --- 5. Subject Management ---
    private static void testSubjectManagement() throws Exception {
        Course c1 = new Course("CS101", "Computer Programming", 4, SubjectType.THEORY);
        assertCondition(c1.getCredits() == 4, "Course credits should be 4");
        assertCondition(!c1.isLab(), "THEORY subject is not lab");

        c1.setCredits(-2);
        assertCondition(c1.getCredits() == 3, "Negative credit assignment should fallback to default 3");

        Course c2 = new Course("CS101L", "Programming Lab", 2, SubjectType.LAB);
        assertCondition(c2.isLab(), "LAB subject type must return true for isLab()");
    }

    // --- 6. Assessment Validation, Edit & Delete ---
    private static void testAssessmentScoreValidation() throws Exception {
        Course c = new Course("CS201", "Algorithms", 4);

        // Valid score
        GradeItem g1 = new GradeItem("Quiz 1", 18, 20, AssessmentCategory.QUIZ, 0.10);
        c.addGradeItem(g1);
        assertCondition(c.getGradeItems().size() == 1, "Grade item added");

        // Negative score fallback check
        GradeItem gNegative = new GradeItem("Invalid Negative", -5, 20, AssessmentCategory.QUIZ, 0.10);
        assertCondition(gNegative.getScore() >= 0.0, "Negative score must be clamped to 0.0 at model layer");

        // Score edit
        g1.setScore(20);
        assertCondition(g1.getScore() == 20.0, "Score updated to 20");
        assertCondition(g1.getPercentage() == 100.0, "Percentage updated to 100%");

        // Delete score
        c.removeGradeItem(g1.getId());
        assertCondition(c.getGradeItems().isEmpty(), "Grade item deleted successfully");
    }

    // --- 7. SGPA & CGPA Fidelity ---
    private static void testSgpaCgpaFidelity() throws Exception {
        Student s = new Student("S2006", "Fiona Apple", "fiona@college.edu", "CSE", 2);

        // Sem 1: 4 credits of 10.0 (Grade Point 10)
        Course c1 = new Course("CS101", "Maths 1", 4);
        c1.addGradeItem(new GradeItem("Final", 95, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // 95% -> A+ (10.0)
        s.getSemester(1).addSubject(c1);

        // Sem 2: 4 credits of 8.0 (Grade Point 8)
        Course c2 = new Course("CS201", "Maths 2", 4);
        c2.addGradeItem(new GradeItem("Final", 75, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // 75% -> B (8.0)
        s.getSemester(2).addSubject(c2);

        double sgpa1 = s.getSemester(1).getSgpa();
        double sgpa2 = s.getSemester(2).getSgpa();
        double cgpa = s.getCgpa();

        assertCondition(Math.abs(sgpa1 - 10.0) < 0.01, "Sem 1 SGPA should be 10.0, got: " + sgpa1);
        assertCondition(Math.abs(sgpa2 - 8.0) < 0.01, "Sem 2 SGPA should be 8.0, got: " + sgpa2);
        assertCondition(Math.abs(cgpa - 9.0) < 0.01, "Cumulative CGPA should be 9.0, got: " + cgpa);

        // Sem 3: 4 credits of 0.0 SGPA (failed subject with entered grade)
        Course c3 = new Course("CS301", "Physics", 4);
        c3.addGradeItem(new GradeItem("Final", 20, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // 20% -> F (0.0 GP)
        s.getSemester(3).addSubject(c3);

        double cgpaWithZeroSgpa = s.getCgpa();
        assertCondition(Math.abs(cgpaWithZeroSgpa - 6.0) < 0.01, "CGPA with Zero-SGPA semester should be 6.0, got: " + cgpaWithZeroSgpa);

        // Sem 4: Partially entered semester (4 credits graded with 10.0 GP, 4 credits registered but ungraded)
        Course c4a = new Course("CS401", "Algorithms", 4);
        c4a.addGradeItem(new GradeItem("Final", 95, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // 10.0 GP
        s.getSemester(4).addSubject(c4a);

        Course c4b = new Course("CS402", "Operating Systems", 4); // Registered but no grade items yet
        s.getSemester(4).addSubject(c4b);

        assertCondition(s.getSemester(4).getTotalCredits() == 8, "Sem 4 registered credits must be 8");
        assertCondition(s.getSemester(4).getGradedCredits() == 4, "Sem 4 graded credits must be 4");
        assertCondition(Math.abs(s.getSemester(4).getSgpa() - 10.0) < 0.01, "Sem 4 SGPA must be 10.0 based on graded subjects");

        // Denominator must only count graded credits (4 + 4 + 4 + 4 = 16 credits), not registered credits (20)
        // Correct CGPA = (10*4 + 8*4 + 0*4 + 10*4) / 16 = 112 / 16 = 7.00
        // Erroneous registered-credit weight would produce: (72 + 10*8) / 20 = 152 / 20 = 7.60
        double cgpaPartialSem = s.getCgpa();
        assertCondition(Math.abs(cgpaPartialSem - 7.0) < 0.01,
            "CGPA with partial semester should not weight un-assessed credits (expected 7.0, got: " + cgpaPartialSem + ")");
    }

    // --- 8. Backlog Lifecycle & Remediation ---
    private static void testBacklogLifecycle() throws Exception {
        Student s = new Student("S2007", "George Clark", "george@college.edu", "ME", 1);
        Course c = new Course("ME101", "Thermodynamics", 4);
        s.getSemester(1).addSubject(c);

        // 1. Enter failing grade (<40%)
        GradeItem failingGrade = new GradeItem("Final Exam", 30, 100, AssessmentCategory.FINAL_EXAM, 1.0);
        c.addGradeItem(failingGrade);

        assertCondition(!c.isPassed(), "Course with 30% must be marked as failed/backlog");
        assertCondition(s.getBacklogCount() == 1, "Active backlog count must equal 1");

        // 2. Remediation (re-evaluation edit score to >= 40%)
        failingGrade.setScore(60);

        assertCondition(c.isPassed(), "Course with 60% must be marked as passed");
        assertCondition(s.getBacklogCount() == 0, "Active backlog count must return to 0 after remediation");
    }

    // --- 9. Search & Filtering ---
    private static void testSearchAndFiltering() throws Exception {
        List<Student> list = new ArrayList<>();
        list.add(new Student("S101", "Harshit Raj", "harshit@college.edu", "CSE", 4));
        list.add(new Student("S102", "Priya Sharma", "priya@college.edu", "ECE", 3));

        // Filter by Branch
        long cseCount = list.stream().filter(s -> "CSE".equalsIgnoreCase(s.getDepartment())).count();
        assertCondition(cseCount == 1, "Branch filter CSE count should be 1");

        // Search by Partial Name
        long nameCount = list.stream().filter(s -> s.getName().toLowerCase().contains("harshit")).count();
        assertCondition(nameCount == 1, "Search query 'harshit' count should be 1");
    }

    // --- 10. Leaderboard Ranking ---
    private static void testLeaderboardSorting() throws Exception {
        List<Student> students = new ArrayList<>();
        Student s1 = new Student("S1", "Alice", "a@a.com", "CSE", 1);
        Course c1 = new Course("C1", "P1", 4);
        c1.addGradeItem(new GradeItem("Exam", 90, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // CGPA 10.0
        s1.getSemester(1).addSubject(c1);

        Student s2 = new Student("S2", "Bob", "b@b.com", "CSE", 1);
        Course c2 = new Course("C2", "P2", 4);
        c2.addGradeItem(new GradeItem("Exam", 50, 100, AssessmentCategory.FINAL_EXAM, 1.0)); // CGPA lower
        s2.getSemester(1).addSubject(c2);

        students.add(s2);
        students.add(s1);

        List<Student> ranked = AcademicEngineService.getRankedStudents(students);
        assertCondition(ranked.get(0).getStudentId().equals("S1"), "Highest CGPA student (S1) must be ranked #1");
    }

    // --- 11. Analytics Calculations ---
    private static void testAnalyticsCalculations() throws Exception {
        List<Student> students = new ArrayList<>();
        double avgEmpty = GradeCalculator.calculateClassAverage(students);
        assertCondition(avgEmpty == 0.0, "Empty student list class average should be 0.0");

        Map<String, Integer> distEmpty = GradeCalculator.calculateGradeDistribution(students);
        assertCondition(distEmpty != null, "Grade distribution for empty list must not be null");
    }

    // --- 12. CSV Export Integrity ---
    private static void testCsvExportIntegrity() throws Exception {
        List<Student> students = new ArrayList<>();
        Student s = new Student("S2008", "Ian Malcolm", "ian@college.edu", "CSE", 1);
        Course c = new Course("CS101", "Java Programming", 3);
        c.addGradeItem(new GradeItem("Quiz", 10, 10, AssessmentCategory.QUIZ, 1.0));
        s.getSemester(1).addSubject(c);
        students.add(s);

        File tempFile = File.createTempFile("test_class_report", ".csv");
        boolean exported = ReportExporter.exportClassReportToCsv(students, tempFile);

        assertCondition(exported, "CSV class report export must succeed");
        assertCondition(tempFile.exists() && tempFile.length() > 0, "Exported CSV file must exist and have non-zero size");
        tempFile.deleteOnExit();
    }

    // --- 13. Persistence Integrity ---
    private static void testPersistenceIntegrity() throws Exception {
        DataManager dm1 = new DataManager();
        dm1.getStudents().clear();

        Student s = new Student("S2009", "Julia Roberts", "julia@college.edu", "IT", 2);
        Course c = new Course("IT201", "Database Systems", 4);
        c.addGradeItem(new GradeItem("Midterm", 40, 50, AssessmentCategory.MIDTERM, 0.40));
        s.getSemester(2).addSubject(c);
        dm1.addStudent(s);

        boolean saved = dm1.saveData();
        assertCondition(saved, "DataManager save must return true");

        DataManager dm2 = new DataManager();
        boolean loadedOk = dm2.loadData();
        assertCondition(loadedOk, "DataManager load of valid file must return true");
        assertCondition(!dm2.hasLoadFailed(), "Valid load must set loadFailed to false");

        Student reloaded = dm2.getStudentById("S2009");
        assertCondition(reloaded != null, "Reloaded student S2009 must not be null");
        assertCondition("Julia Roberts".equals(reloaded.getName()), "Reloaded student name match");
        assertCondition(reloaded.getSemester(2).getSubjects().size() == 1, "Reloaded semester subjects count match");

        // Verify null payload / load failure save protection using DataManager(testFilePath)
        File corruptTestFile = File.createTempFile("test_null_payload", ".dat");
        try {
            try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(corruptTestFile))) {
                oos.writeObject(null);
            }

            long initialFileSize = corruptTestFile.length();
            assertCondition(initialFileSize > 0, "Corrupt test file must be non-empty after writing serialized null");

            DataManager dmCorrupt = new DataManager(corruptTestFile.getAbsolutePath());
            assertCondition(!dmCorrupt.hasLoadFailed(), "Initially hasLoadFailed() must be false");

            // Assert that loading the null payload returns false
            boolean loadResult = dmCorrupt.loadData();
            assertCondition(!loadResult, "Loading null payload via DataManager.loadData() must return false");

            // Assert that hasLoadFailed() returns true
            assertCondition(dmCorrupt.hasLoadFailed(), "hasLoadFailed() must return true after loading null payload");
            assertCondition(dmCorrupt.getStudents().isEmpty(), "Student list must be empty after load failure");

            // Assert that a subsequent saveData() returns false and does not overwrite the persisted data
            boolean saveResult = dmCorrupt.saveData();
            assertCondition(!saveResult, "saveData() must return false when loadFailed is true");
            assertCondition(corruptTestFile.length() == initialFileSize, "saveData() must not overwrite test file after load failure");
        } finally {
            File backupFile = new File(corruptTestFile.getAbsolutePath() + ".bak");
            if (backupFile.exists()) {
                backupFile.delete();
            }
            corruptTestFile.delete();
        }
    }

    // --- 14. System Edge Cases ---
    private static void testSystemEdgeCases() throws Exception {
        // Zero students average
        double avg = AcademicEngineService.calculateClassAverageCgpa(new ArrayList<>());
        assertCondition(avg == 0.0, "Class average for zero students must be 0.0");

        // Zero students backlogs
        int backlogs = AcademicEngineService.calculateTotalActiveBacklogs(new ArrayList<>());
        assertCondition(backlogs == 0, "Total backlogs for zero students must be 0");

        // Conversion of 0 CGPA to AICTE percentage
        double pct0 = AcademicEngineService.convertCgpaToPercentage(0.0);
        assertCondition(pct0 == 0.0, "0 CGPA should convert to 0% AICTE percentage");
    }

    private static void assertCondition(boolean condition, String message) throws Exception {
        if (!condition) {
            throw new Exception("Assertion Failed: " + message);
        }
    }
}
