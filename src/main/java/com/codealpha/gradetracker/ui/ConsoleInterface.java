package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.io.ReportExporter;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.util.GradeCalculator;

import java.io.File;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console / Terminal Interface for CodeAlpha Student Grade Tracker.
 */
public class ConsoleInterface {

    private final DataManager dataManager;
    private final Scanner scanner;

    public ConsoleInterface() {
        this.dataManager = new DataManager();
        this.dataManager.loadData();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("==================================================");
        System.out.println(" CODEALPHA STUDENT GRADE TRACKER - CONSOLE MODE");
        System.out.println("==================================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (1-9): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    listAllStudents();
                    break;
                case "2":
                    addNewStudent();
                    break;
                case "3":
                    enrollStudentInCourse();
                    break;
                case "4":
                    enterGradeForStudent();
                    break;
                case "5":
                    displayClassStatistics();
                    break;
                case "6":
                    exportClassCsvReport();
                    break;
                case "7":
                    saveDataToDisk();
                    break;
                case "8":
                    dataManager.loadSampleData();
                    System.out.println("[INFO] Sample dataset reloaded successfully.");
                    break;
                case "9":
                    dataManager.saveData();
                    System.out.println("Thank you for using CodeAlpha Student Grade Tracker. Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("[ERROR] Invalid choice. Please select a number between 1 and 9.");
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. List All Students & Summary Grades");
        System.out.println("2. Add New Student");
        System.out.println("3. Enroll Student in Course");
        System.out.println("4. Add Grade Score for Course");
        System.out.println("5. View Class Performance Statistics (Mean, Highest, Lowest)");
        System.out.println("6. Export Summary Report to CSV");
        System.out.println("7. Save Data File");
        System.out.println("8. Reload Sample Data");
        System.out.println("9. Save & Exit");
    }

    private void listAllStudents() {
        List<Student> students = dataManager.getStudents();
        if (students.isEmpty()) {
            System.out.println("[INFO] No students currently registered in the database.");
            return;
        }

        System.out.println("\n-----------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-20s | %-20s | %-8s | %-8s | %-6s\n", "ID", "Name", "Department", "Courses", "Overall %", "Grade");
        System.out.println("-----------------------------------------------------------------------------------");

        for (Student s : students) {
            GradeSummary summary = GradeCalculator.calculateStudentSummary(s);
            System.out.printf("%-10s | %-20s | %-20s | %-8d | %-8.1f | %-6s\n",
                    s.getStudentId(),
                    s.getName(),
                    s.getDepartment(),
                    s.getCourses().size(),
                    summary.getOverallPercentage(),
                    summary.getLetterGrade());
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private void addNewStudent() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("[ERROR] Student ID cannot be empty.");
            return;
        }

        if (dataManager.getStudentById(id) != null) {
            System.out.println("[ERROR] Student ID already exists!");
            return;
        }

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Department: ");
        String dept = scanner.nextLine().trim();

        Student student = new Student(id, name, email, dept.isEmpty() ? "General" : dept);
        dataManager.addStudent(student);
        System.out.println("[SUCCESS] Student added successfully!");
    }

    private void enrollStudentInCourse() {
        System.out.print("Enter Student ID to enroll course for: ");
        String id = scanner.nextLine().trim();
        Student student = dataManager.getStudentById(id);

        if (student == null) {
            System.out.println("[ERROR] Student not found.");
            return;
        }

        System.out.print("Enter Course Code (e.g. CS101): ");
        String code = scanner.nextLine().trim();
        System.out.print("Enter Course Name (e.g. Data Structures): ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Course Credits (e.g. 4): ");
        int credits = 3;
        try {
            credits = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[WARNING] Invalid credits number, defaulting to 3.");
        }

        Course course = new Course(code, name, credits);
        student.addCourse(course);
        System.out.println("[SUCCESS] Course enrolled successfully for " + student.getName() + "!");
    }

    private void enterGradeForStudent() {
        System.out.print("Enter Student ID: ");
        String id = scanner.nextLine().trim();
        Student student = dataManager.getStudentById(id);

        if (student == null) {
            System.out.println("[ERROR] Student not found.");
            return;
        }

        if (student.getCourses().isEmpty()) {
            System.out.println("[ERROR] Student has no enrolled courses. Please enroll a course first.");
            return;
        }

        System.out.println("Enrolled Courses:");
        for (int i = 0; i < student.getCourses().size(); i++) {
            Course c = student.getCourses().get(i);
            System.out.printf("  %d. %s: %s\n", (i + 1), c.getCourseCode(), c.getCourseName());
        }

        System.out.print("Select Course Number (1-" + student.getCourses().size() + "): ");
        int courseIdx = -1;
        try {
            courseIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException ignored) {
        }

        if (courseIdx < 0 || courseIdx >= student.getCourses().size()) {
            System.out.println("[ERROR] Invalid course selection.");
            return;
        }

        Course course = student.getCourses().get(courseIdx);

        System.out.print("Enter Assessment Title (e.g. Assignment 1 / Final Exam): ");
        String title = scanner.nextLine().trim();

        System.out.println("Select Category:");
        AssessmentCategory[] categories = AssessmentCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s\n", (i + 1), categories[i].getDisplayName());
        }
        System.out.print("Select Category Number (1-" + categories.length + "): ");
        int catIdx = 0;
        try {
            catIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException ignored) {
        }
        if (catIdx < 0 || catIdx >= categories.length) catIdx = 0;

        System.out.print("Enter Achieved Score: ");
        double score = 0;
        try {
            score = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid score entered.");
            return;
        }

        System.out.print("Enter Max Score (default 100): ");
        double maxScore = 100;
        String maxStr = scanner.nextLine().trim();
        if (!maxStr.isEmpty()) {
            try {
                maxScore = Double.parseDouble(maxStr);
            } catch (NumberFormatException ignored) {
            }
        }

        System.out.print("Enter Weightage Percentage (e.g. 20 for 20%): ");
        double weightage = 0.20;
        String wStr = scanner.nextLine().trim();
        if (!wStr.isEmpty()) {
            try {
                weightage = Double.parseDouble(wStr) / 100.0;
            } catch (NumberFormatException ignored) {
            }
        }

        GradeItem item = new GradeItem(title, score, maxScore, categories[catIdx], weightage);
        course.addGradeItem(item);
        System.out.printf("[SUCCESS] Grade score added! Course %s updated percentage: %.1f%%\n",
                course.getCourseCode(), course.getCalculatedPercentage());
    }

    private void displayClassStatistics() {
        List<Student> students = dataManager.getStudents();
        if (students.isEmpty()) {
            System.out.println("[INFO] No student data available.");
            return;
        }

        double classAvg = GradeCalculator.calculateClassAverage(students);
        double maxScore = 0;
        double minScore = 100;
        String topStudent = "N/A";
        boolean hasGrades = false;

        for (Student s : students) {
            GradeSummary summary = GradeCalculator.calculateStudentSummary(s);
            if (!"N/A".equals(summary.getLetterGrade())) {
                hasGrades = true;
                double pct = summary.getOverallPercentage();
                if (pct > maxScore) {
                    maxScore = pct;
                    topStudent = s.getName() + " (" + s.getStudentId() + ")";
                }
                if (pct < minScore) {
                    minScore = pct;
                }
            }
        }

        System.out.println("\n--------------------------------------------------");
        System.out.println(" CLASS PERFORMANCE SUMMARY");
        System.out.println("--------------------------------------------------");
        System.out.printf(" Total Registered Students : %d\n", students.size());
        System.out.printf(" Class Mean Average Score  : %.2f%% (GPA: %.2f)\n", classAvg, GradeCalculator.getGpa4Scale(classAvg));
        System.out.printf(" Highest Student Percentage: %s\n", hasGrades ? String.format("%.1f%% [%s]", maxScore, topStudent) : "N/A");
        System.out.printf(" Lowest Student Percentage : %s\n", hasGrades ? String.format("%.1f%%", minScore) : "N/A");
        System.out.println("--------------------------------------------------");
    }

    private void exportClassCsvReport() {
        File file = new File("Class_Grade_Summary_Report.csv");
        boolean ok = ReportExporter.exportClassReportToCsv(dataManager.getStudents(), file);
        if (ok) {
            System.out.println("[SUCCESS] CSV report exported successfully to: " + file.getAbsolutePath());
        } else {
            System.out.println("[ERROR] Failed to export CSV report.");
        }
    }

    private void saveDataToDisk() {
        boolean ok = dataManager.saveData();
        if (ok) {
            System.out.println("[SUCCESS] Application state saved to disk.");
        } else {
            System.out.println("[ERROR] Failed to save data.");
        }
    }
}
