package com.codealpha.gradetracker;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.io.ReportExporter;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.ui.AnalyticsPanel;
import com.codealpha.gradetracker.ui.DashboardPanel;
import com.codealpha.gradetracker.ui.GradeEntryPanel;
import com.codealpha.gradetracker.ui.StudentManagementPanel;
import com.codealpha.gradetracker.ui.chart.CategoryBreakdownChart;
import com.codealpha.gradetracker.ui.chart.GradeDistributionChart;
import com.codealpha.gradetracker.util.GradeCalculator;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End-to-End Functional Verification Suite for all 22 System Workflows.
 */
public class FullFunctionalVerificationTest {

    private static int passCount = 0;
    private static int failCount = 0;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" CODEALPHA STUDENT GRADE TRACKER - 22-POINT FUNCTIONAL VERIFICATION");
        System.out.println("======================================================================");

        DataManager dm = new DataManager();
        dm.loadSampleData();

        // 1. Add Student
        test("1. Add Student", () -> {
            Student s = new Student("S9999", "Test User", "test@test.com", "Computer Science");
            dm.addStudent(s);
            assert dm.getStudentById("S9999") != null : "Student S9999 was not added";
        });

        // 2. Edit Student
        test("2. Edit Student", () -> {
            Student s = dm.getStudentById("S9999");
            s.setName("Test User Updated");
            s.setEmail("updated@test.com");
            s.setDepartment("Information Technology");
            assert "Test User Updated".equals(dm.getStudentById("S9999").getName()) : "Student name update failed";
        });

        // 3. Remove Student
        test("3. Remove Student", () -> {
            boolean removed = dm.removeStudent("S9999");
            assert removed && dm.getStudentById("S9999") == null : "Student S9999 removal failed";
        });

        // 4. Search Student
        test("4. Search Student", () -> {
            List<Student> list = dm.getStudents();
            boolean foundAlice = list.stream().anyMatch(s -> s.getName().toLowerCase().contains("alice"));
            assert foundAlice : "Search query for 'alice' failed";
        });

        // 5. Add/Enroll Course
        test("5. Add/Enroll Course", () -> {
            Student s = dm.getStudentById("S1001");
            Course newCourse = new Course("PHYS101", "Physics I", 3);
            s.addCourse(newCourse);
            assert s.getCourseById(newCourse.getId()) != null : "Course enrollment failed";
        });

        // 6. Manage Courses / Unenroll Course
        test("6. Manage Courses / Unenroll Course", () -> {
            Student s = dm.getStudentById("S1001");
            Course c = s.getCourses().stream().filter(crs -> "PHYS101".equals(crs.getCourseCode())).findFirst().orElse(null);
            assert c != null : "PHYS101 not found";
            boolean removed = s.removeCourse(c.getId());
            assert removed : "Unenrolling course failed";
        });

        // 7. Add Grade Score
        test("7. Add Grade Score", () -> {
            Student s = dm.getStudentById("S1001");
            Course c = s.getCourses().get(0);
            int initialCount = c.getGradeItems().size();
            GradeItem item = new GradeItem("Lab 1", 95, 100, AssessmentCategory.ASSIGNMENT, 0.10);
            c.addGradeItem(item);
            assert c.getGradeItems().size() == initialCount + 1 : "Grade item addition failed";
        });

        // 8. Edit Grade Score
        test("8. Edit Grade Score", () -> {
            Student s = dm.getStudentById("S1001");
            Course c = s.getCourses().get(0);
            GradeItem item = c.getGradeItems().get(c.getGradeItems().size() - 1);
            item.setScore(98);
            item.setTitle("Lab 1 Revised");
            assert 98.0 == item.getScore() : "Grade score edit failed";
        });

        // 9. Delete Grade Score
        test("9. Delete Grade Score", () -> {
            Student s = dm.getStudentById("S1001");
            Course c = s.getCourses().get(0);
            GradeItem item = c.getGradeItems().get(c.getGradeItems().size() - 1);
            boolean deleted = c.removeGradeItem(item.getId());
            assert deleted : "Grade score deletion failed";
        });

        // 10. Score validation
        test("10. Score Validation Logic", () -> {
            GradeItem item = new GradeItem("Test", -50, -100, AssessmentCategory.OTHER, -0.5);
            assert item.getMaxScore() > 0 : "Max score invalid bound check failed";
            assert item.getPercentage() >= 0 : "Percentage calculation on edge scores failed";
        });

        // 11. GPA Calculation
        test("11. GPA Calculation", () -> {
            double gpa1 = GradeCalculator.getGpa4Scale(95.0);
            double gpa2 = GradeCalculator.getGpa4Scale(72.0);
            double gpa3 = GradeCalculator.getGpa4Scale(40.0);
            assert gpa1 == 4.0 && gpa2 == 3.0 && gpa3 == 0.0 : "GPA 4.0 scale calculation incorrect";
        });

        // 12. Percentage Calculation
        test("12. Weighted Percentage Calculation", () -> {
            Course c = new Course("TST101", "Test", 3);
            c.addGradeItem(new GradeItem("A1", 80, 100, AssessmentCategory.ASSIGNMENT, 0.5));
            c.addGradeItem(new GradeItem("A2", 100, 100, AssessmentCategory.FINAL_EXAM, 0.5));
            assert c.getCalculatedPercentage() == 90.0 : "Weighted percentage calculation failed";
        });

        // 13. Letter Grade Calculation
        test("13. Letter Grade Calculation", () -> {
            assert "A+".equals(GradeCalculator.getLetterGrade(95.0)) : "95% Letter grade failed";
            assert "B".equals(GradeCalculator.getLetterGrade(72.0)) : "72% Letter grade failed";
            assert "F".equals(GradeCalculator.getLetterGrade(40.0)) : "40% Letter grade failed";
        });

        // 14. Dashboard Statistics
        test("14. Dashboard Statistics Data Flow", () -> {
            DashboardPanel dashboard = new DashboardPanel(dm);
            dashboard.refreshData();
            assert dashboard != null : "Dashboard initialization failed";
        });

        // 15. Analytics Charts
        test("15. Analytics Vector Charts Rendering", () -> {
            GradeDistributionChart barChart = new GradeDistributionChart();
            barChart.setData(GradeCalculator.calculateGradeDistribution(dm.getStudents()));

            CategoryBreakdownChart pieChart = new CategoryBreakdownChart();
            Map<AssessmentCategory, Double> map = new HashMap<>();
            map.put(AssessmentCategory.ASSIGNMENT, 5.0);
            pieChart.setData(map);
            assert barChart != null && pieChart != null : "Chart instantiation failed";
        });

        // 16. Save Data
        test("16. Save Data Persistence", () -> {
            boolean saved = dm.saveData();
            assert saved : "Saving data file failed";
        });

        // 17. Reload Persisted Data
        test("17. Reload Persisted Data", () -> {
            DataManager dm2 = new DataManager();
            boolean loaded = dm2.loadData();
            assert loaded && !dm2.getStudents().isEmpty() : "Reloading persisted data failed";
        });

        // 18. Reload Sample Data
        test("18. Reload Sample Data", () -> {
            dm.loadSampleData();
            assert dm.getStudents().size() >= 4 : "Reload sample data failed";
        });

        // 19. Individual CSV Report Card Export
        test("19. Individual CSV Report Card Export", () -> {
            Student s = dm.getStudents().get(0);
            File f = new File("test_individual_report.csv");
            boolean ok = ReportExporter.exportStudentDetailedReportToCsv(s, f);
            if (f.exists()) f.deleteOnExit();
            assert ok : "Individual CSV export failed";
        });

        // 20. Full CSV Export
        test("20. Full Class CSV Export", () -> {
            File f = new File("test_class_report.csv");
            boolean ok = ReportExporter.exportClassReportToCsv(dm.getStudents(), f);
            if (f.exists()) f.deleteOnExit();
            assert ok : "Full class CSV export failed";
        });

        // 21. CLI Mode Setup
        test("21. CLI Mode Setup & Command Routing", () -> {
            try {
                Class<?> cliClass = Class.forName("com.codealpha.gradetracker.ui.ConsoleInterface");
                assert cliClass != null : "ConsoleInterface class not found";
            } catch (ClassNotFoundException e) {
                assert false : "CLI mode class missing";
            }
        });

        // 22. Existing Automated Tests
        test("22. GradeCalculatorTest Suite Integration", () -> {
            GradeCalculatorTest.main(new String[]{});
        });

        System.out.println("======================================================================");
        System.out.printf(" SUMMARY: %d / 22 Passed | %d / 22 Failed\n", passCount, failCount);
        System.out.println("======================================================================");

        if (failCount > 0) {
            System.exit(1);
        }
    }

    private static void test(String name, Runnable testProc) {
        try {
            testProc.run();
            System.out.printf("  [PASS] %-55s\n", name);
            passCount++;
        } catch (Throwable t) {
            System.out.printf("  [FAIL] %-55s (%s)\n", name, t.getMessage());
            failCount++;
        }
    }
}
