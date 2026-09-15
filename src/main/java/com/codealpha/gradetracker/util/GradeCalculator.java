package com.codealpha.gradetracker.util;

import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class providing mathematical formulas for GPA, weighted averages, and statistics.
 * Updated to delegate to AcademicEngineService for B.Tech multi-semester support.
 */
public class GradeCalculator {

    public static String getLetterGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 85.0) return "A";
        if (percentage >= 80.0) return "A-";
        if (percentage >= 75.0) return "B+";
        if (percentage >= 70.0) return "B";
        if (percentage >= 65.0) return "B-";
        if (percentage >= 60.0) return "C+";
        if (percentage >= 55.0) return "C";
        if (percentage >= 50.0) return "D";
        return "F";
    }

    public static double getGpa4Scale(double percentage) {
        if (percentage >= 90.0) return 4.0;
        if (percentage >= 85.0) return 3.8;
        if (percentage >= 80.0) return 3.6;
        if (percentage >= 75.0) return 3.3;
        if (percentage >= 70.0) return 3.0;
        if (percentage >= 65.0) return 2.7;
        if (percentage >= 60.0) return 2.3;
        if (percentage >= 55.0) return 2.0;
        if (percentage >= 50.0) return 1.0;
        return 0.0;
    }

    public static double getGpa10Scale(double percentage) {
        if (percentage >= 90.0) return 10.0;
        if (percentage >= 85.0) return 9.0;
        if (percentage >= 80.0) return 8.5;
        if (percentage >= 75.0) return 8.0;
        if (percentage >= 70.0) return 7.5;
        if (percentage >= 65.0) return 7.0;
        if (percentage >= 60.0) return 6.5;
        if (percentage >= 55.0) return 6.0;
        if (percentage >= 50.0) return 5.0;
        return 0.0;
    }

    public static GradeSummary calculateCourseSummary(Course course) {
        if (course == null || course.getGradeItems().isEmpty()) {
            return new GradeSummary(0.0, 0.0, 0.0, "N/A", course != null ? course.getCredits() : 0, 0.0, 0.0);
        }

        double percentage = course.getCalculatedPercentage();
        double highest = 0.0;
        double lowest = 100.0;

        for (GradeItem item : course.getGradeItems()) {
            double p = item.getPercentage();
            if (p > highest) highest = p;
            if (p < lowest) lowest = p;
        }

        return new GradeSummary(
                percentage,
                getGpa4Scale(percentage),
                course.getGradePoint(),
                course.getLetterGrade(),
                course.getCredits(),
                highest,
                lowest
        );
    }

    public static GradeSummary calculateStudentSummary(Student student) {
        return AcademicEngineService.calculateStudentSummary(student);
    }

    public static Map<String, Integer> calculateGradeDistribution(List<Student> students) {
        Map<String, Integer> distribution = new HashMap<>();
        String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F"};
        for (String g : grades) {
            distribution.put(g, 0);
        }

        if (students == null) return distribution;

        for (Student s : students) {
            GradeSummary summary = calculateStudentSummary(s);
            if (!"N/A".equals(summary.getLetterGrade())) {
                String letter = summary.getLetterGrade();
                distribution.put(letter, distribution.getOrDefault(letter, 0) + 1);
            }
        }

        return distribution;
    }

    public static double calculateClassAverage(List<Student> students) {
        if (students == null || students.isEmpty()) return 0.0;
        double totalPct = 0.0;
        int count = 0;

        for (Student s : students) {
            GradeSummary summary = calculateStudentSummary(s);
            if (!"N/A".equals(summary.getLetterGrade())) {
                totalPct += summary.getOverallPercentage();
                count++;
            }
        }

        return count > 0 ? totalPct / count : 0.0;
    }
}
