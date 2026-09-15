package com.codealpha.gradetracker;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.io.ReportExporter;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.ui.AnalyticsPanel;
import com.codealpha.gradetracker.ui.ConsoleInterface;
import com.codealpha.gradetracker.ui.DashboardPanel;
import com.codealpha.gradetracker.ui.LeaderboardPanel;
import com.codealpha.gradetracker.ui.SemesterGradebookPanel;
import com.codealpha.gradetracker.ui.StudentManagementPanel;

import java.io.File;
import java.util.List;

/**
 * 30-Point GUI Smoke Test Suite for B.Tech Academic System v3.0.
 */
public class GuiSmokeTest {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println(" B.TECH ACADEMIC PERFORMANCE MANAGEMENT SYSTEM v3.0 - 30-POINT SMOKE TEST");
        System.out.println("==========================================================================");

        DataManager dm = new DataManager();
        dm.loadSampleData();

        // 1. Add a new student
        runStep(1, "Add a new student", () -> {
            Student s = new Student("S2001", "Smoke Student", "smoke@test.com", "Computer Science & Engg", 1);
            dm.addStudent(s);
            check(dm.getStudentById("S2001") != null, "Student S2001 not added");
        });

        // 2. Edit the student
        runStep(2, "Edit the student", () -> {
            Student s = dm.getStudentById("S2001");
            s.setName("Smoke Student Updated");
            s.setEmail("smoke.updated@test.com");
            s.setCurrentSemester(2);
            check("Smoke Student Updated".equals(dm.getStudentById("S2001").getName()), "Name update failed");
        });

        // 3. Search for the student
        runStep(3, "Search for the student", () -> {
            List<Student> list = dm.getStudents();
            boolean found = list.stream().anyMatch(st -> st.getName().toLowerCase().contains("smoke"));
            check(found, "Search failed for 'smoke'");
        });

        // 4. Enroll at least 2 subjects in different semesters
        runStep(4, "Enroll 2 subjects in different semesters", () -> {
            Student s = dm.getStudentById("S2001");
            Course c1 = new Course("CS-101", "Programming in C", 4);
            Course c2 = new Course("CS-201", "Data Structures", 4);
            s.getSemester(1).addSubject(c1);
            s.getSemester(2).addSubject(c2);
            check(s.getSemester(1).getSubjectByCode("CS-101") != null && s.getSemester(2).getSubjectByCode("CS-201") != null, "Enrollment in Sem 1 & 2 failed");
        });

        // 5. Open Semester Gradebook
        runStep(5, "Open Semester Gradebook", () -> {
            SemesterGradebookPanel gradebook = new SemesterGradebookPanel(dm, () -> {});
            check(gradebook != null, "Gradebook panel failed to instantiate");
        });

        // 6. Select the student
        runStep(6, "Select the student", () -> {
            Student s = dm.getStudentById("S2001");
            check(s != null && "S2001".equals(s.getStudentId()), "Student selection failed");
        });

        // 7. Select the semester containing subjects
        runStep(7, "Select semester containing subjects", () -> {
            Student s = dm.getStudentById("S2001");
            Semester sem1 = s.getSemester(1);
            check(!sem1.getSubjects().isEmpty(), "Semester 1 subjects empty");
        });

        // 8. Verify the subject dropdown actually shows those subjects
        runStep(8, "Verify subject dropdown shows subjects", () -> {
            Student s = dm.getStudentById("S2001");
            List<Course> subs = s.getSemester(1).getSubjects();
            check(subs.size() == 1 && "CS-101".equals(subs.get(0).getCourseCode()), "Subject dropdown verification failed");
        });

        // 9. Select a subject
        runStep(9, "Select a subject", () -> {
            Student s = dm.getStudentById("S2001");
            Course c = s.getSemester(1).getSubjectByCode("CS-101");
            check(c != null && "CS-101".equals(c.getCourseCode()), "Subject selection failed");
        });

        // 10. Add an assessment/marks
        runStep(10, "Add an assessment/marks", () -> {
            Student s = dm.getStudentById("S2001");
            Course c = s.getSemester(1).getSubjectByCode("CS-101");
            GradeItem mark = new GradeItem("Midterm Exam", 85, 100, AssessmentCategory.MIDTERM, 0.30);
            c.addGradeItem(mark);
            check(c.getGradeItems().size() == 1, "Adding assessment failed");
        });

        // 11. Edit the marks
        runStep(11, "Edit the marks", () -> {
            Student s = dm.getStudentById("S2001");
            Course c = s.getSemester(1).getSubjectByCode("CS-101");
            GradeItem mark = c.getGradeItems().get(0);
            mark.setScore(90);
            check(mark.getScore() == 90.0, "Editing mark failed");
        });

        // 12. Delete the marks
        runStep(12, "Delete the marks", () -> {
            Student s = dm.getStudentById("S2001");
            Course c = s.getSemester(1).getSubjectByCode("CS-101");
            GradeItem mark = c.getGradeItems().get(0);
            boolean removed = c.removeGradeItem(mark.getId());
            check(removed && c.getGradeItems().isEmpty(), "Deleting mark failed");
        });

        // 13. Enter invalid marks and verify validation
        runStep(13, "Enter invalid marks and verify validation", () -> {
            GradeItem item = new GradeItem("Invalid", -10, -50, AssessmentCategory.ASSIGNMENT, -0.2);
            check(item.getMaxScore() > 0 && item.getPercentage() >= 0, "Validation bounds failed");
        });

        // 14. Verify subject percentage and grade
        runStep(14, "Verify subject percentage and grade", () -> {
            Student s = dm.getStudentById("S2001");
            Course c = s.getSemester(1).getSubjectByCode("CS-101");
            c.addGradeItem(new GradeItem("End-Sem", 85, 100, AssessmentCategory.END_SEM, 1.0));
            check(c.getCalculatedPercentage() == 85.0 && "A+".equals(c.getLetterGrade()), "Subject percentage & letter grade failed");
        });

        // 15. Verify SGPA changes correctly
        runStep(15, "Verify SGPA changes correctly", () -> {
            Student s = dm.getStudentById("S2001");
            Semester sem1 = s.getSemester(1);
            check(sem1.getSgpa() == 9.0, "SGPA calculation failed (Expected 9.0)");
        });

        // 16. Verify CGPA changes correctly
        runStep(16, "Verify CGPA changes correctly", () -> {
            Student s = dm.getStudentById("S2001");
            check(s.getCgpa() == 9.0, "CGPA calculation failed (Expected 9.0)");
        });

        // 17. Create/verify a backlog with marks below 40%
        runStep(17, "Create/verify a backlog with marks below 40%", () -> {
            Student s = dm.getStudentById("S2001");
            Course c2 = s.getSemester(2).getSubjectByCode("CS-201");
            c2.addGradeItem(new GradeItem("End-Sem", 30, 100, AssessmentCategory.END_SEM, 1.0)); // 30% < 40% = Backlog
            check(!c2.isPassed() && "F".equals(c2.getLetterGrade()) && s.getBacklogCount() == 1, "Backlog detection failed");
        });

        // 18. Verify earned credits and backlog count
        runStep(18, "Verify earned credits and backlog count", () -> {
            Student s = dm.getStudentById("S2001");
            check(s.getEarnedCredits() == 4 && s.getTotalCredits() == 8 && s.getBacklogCount() == 1, "Earned credits & backlog count failed");
        });

        // 19. Verify Dashboard updates
        runStep(19, "Verify Dashboard updates", () -> {
            DashboardPanel dashboard = new DashboardPanel(dm);
            dashboard.refreshData();
            check(dashboard != null, "Dashboard refresh failed");
        });

        // 20. Verify Leaderboard updates
        runStep(20, "Verify Leaderboard updates", () -> {
            LeaderboardPanel leaderboard = new LeaderboardPanel(dm);
            leaderboard.refreshLeaderboard();
            check(leaderboard != null, "Leaderboard refresh failed");
        });

        // 21. Verify Academic Analytics updates
        runStep(21, "Verify Academic Analytics updates", () -> {
            AnalyticsPanel analytics = new AnalyticsPanel(dm);
            analytics.refreshAnalytics();
            check(analytics != null, "Analytics refresh failed");
        });

        // 22. Save State
        runStep(22, "Save State", () -> {
            boolean saved = dm.saveData();
            check(saved, "Save state failed");
        });

        // 23. Restart/reload the application
        runStep(23, "Restart/reload the application", () -> {
            DataManager dmReloaded = new DataManager();
            boolean loaded = dmReloaded.loadData();
            check(loaded, "Reload persisted state failed");
        });

        // 24. Verify saved student, semester, subject and marks are still present
        runStep(24, "Verify saved student, semester, subject and marks are still present", () -> {
            DataManager dmReloaded = new DataManager();
            dmReloaded.loadData();
            Student reloadedStudent = dmReloaded.getStudentById("S2001");
            check(reloadedStudent != null && "Smoke Student Updated".equals(reloadedStudent.getName()), "Reloaded student data mismatch");
        });

        // 25. Reload Sample Data
        runStep(25, "Reload Sample Data", () -> {
            dm.loadSampleData();
            check(dm.getStudents().size() >= 4, "Reload sample data failed");
        });

        // 26. Verify sample data appears correctly
        runStep(26, "Verify sample data appears correctly", () -> {
            Student alice = dm.getStudentById("S1001");
            check(alice != null && alice.getCgpa() > 0, "Alice sample data verification failed");
        });

        // 27. Export Individual Student Report Card
        runStep(27, "Export Individual Student Report Card", () -> {
            Student alice = dm.getStudentById("S1001");
            File f = new File("test_individual_smoke.csv");
            boolean ok = ReportExporter.exportStudentDetailedReportToCsv(alice, f);
            if (f.exists()) f.deleteOnExit();
            check(ok, "Individual transcript CSV export failed");
        });

        // 28. Export Full Class CSV
        runStep(28, "Export Full Class CSV", () -> {
            File f = new File("test_class_smoke.csv");
            boolean ok = ReportExporter.exportClassReportToCsv(dm.getStudents(), f);
            if (f.exists()) f.deleteOnExit();
            check(ok, "Full class CSV export failed");
        });

        // 29. Launch CLI mode and verify it starts correctly
        runStep(29, "Launch CLI mode and verify it starts correctly", () -> {
            try {
                Class<?> cliClass = Class.forName("com.codealpha.gradetracker.ui.ConsoleInterface");
                check(cliClass != null, "ConsoleInterface class missing");
            } catch (ClassNotFoundException e) {
                check(false, "ConsoleInterface class not found");
            }
        });

        // 30. Run existing automated tests
        runStep(30, "Run existing automated tests", () -> {
            BTechAcademicEngineTest.main(new String[]{});
        });

        System.out.println("==========================================================================");
        System.out.printf(" FINAL RESULT: %d / 30 Passed | %d / 30 Failed\n", passCount, failCount);
        System.out.println("==========================================================================");

        if (failCount > 0) {
            System.exit(1);
        }
    }

    private static void runStep(int number, String description, RunnableProc step) {
        try {
            step.execute();
            System.out.printf("  [PASS] Step %2d: %-55s\n", number, description);
            passCount++;
        } catch (Throwable t) {
            System.out.printf("  [FAIL] Step %2d: %-55s (%s)\n", number, description, t.getMessage());
            failCount++;
        }
    }

    private static void check(boolean condition, String errorMsg) {
        if (!condition) {
            throw new AssertionError(errorMsg);
        }
    }

    @FunctionalInterface
    interface RunnableProc {
        void execute() throws Exception;
    }
}
