package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core business engine providing B.Tech academic formulas, SGPA/CGPA calculations,
 * backlog detection, and student class rank sorting.
 */
public class AcademicEngineService {

    /**
     * AICTE Standard Formula for converting 10.0 scale CGPA to Percentage.
     * Percentage = (CGPA - 0.75) * 10
     */
    public static double convertCgpaToPercentage(double cgpa) {
        if (cgpa <= 0) return 0.0;
        return Math.max(0.0, (cgpa - 0.75) * 10.0);
    }

    /**
     * Converts CGPA to 4.0 scale GPA equivalent.
     */
    public static double convert10ScaleTo4Scale(double gpa10) {
        if (gpa10 >= 9.0) return 4.0;
        if (gpa10 >= 8.0) return 3.6;
        if (gpa10 >= 7.0) return 3.2;
        if (gpa10 >= 6.0) return 2.8;
        if (gpa10 >= 5.0) return 2.0;
        return 0.0;
    }

    /**
     * Calculates cumulative grade summary for a student.
     */
    public static GradeSummary calculateStudentSummary(Student student) {
        if (student == null) {
            return new GradeSummary(0.0, 0.0, 0.0, "N/A", 0, 0.0, 0.0);
        }

        double totalWeightedPct = 0.0;
        double totalWeightedGpa4 = 0.0;
        int totalCredits = 0;

        double highest = 0.0;
        double lowest = 100.0;
        boolean hasScores = false;

        for (Semester sem : student.getSemesters().values()) {
            for (Course sub : sem.getSubjects()) {
                if (!sub.getGradeItems().isEmpty()) {
                    hasScores = true;
                    double subPct = sub.getCalculatedPercentage();
                    int credits = sub.getCredits();

                    totalWeightedPct += subPct * credits;
                    totalWeightedGpa4 += com.codealpha.gradetracker.util.GradeCalculator.getGpa4Scale(subPct) * credits;
                    totalCredits += credits;

                    if (subPct > highest) highest = subPct;
                    if (subPct < lowest) lowest = subPct;
                }
            }
        }

        if (!hasScores || totalCredits <= 0) {
            return new GradeSummary(0.0, 0.0, 0.0, "N/A", 0, 0.0, 0.0);
        }

        double overallPct = totalWeightedPct / totalCredits;
        double gpa4 = totalWeightedGpa4 / totalCredits;
        double cgpa = student.getCgpa();
        String letterGrade = com.codealpha.gradetracker.util.GradeCalculator.getLetterGrade(overallPct);

        return new GradeSummary(
                overallPct,
                gpa4,
                cgpa,
                letterGrade,
                student.getEarnedCredits(),
                highest,
                lowest
        );
    }

    /**
     * Letter grade mapping based on CGPA.
     */
    public static String getLetterGradeForCgpa(double cgpa) {
        if (cgpa >= 9.0) return "O";
        if (cgpa >= 8.0) return "A+";
        if (cgpa >= 7.0) return "A";
        if (cgpa >= 6.0) return "B+";
        if (cgpa >= 5.0) return "B";
        if (cgpa >= 4.0) return "C";
        return "F";
    }

    /**
     * Returns a list of students sorted by CGPA in descending order (Class Leaderboard).
     */
    public static List<Student> getRankedStudents(List<Student> students) {
        if (students == null) return Collections.emptyList();
        List<Student> sortedList = new ArrayList<>(students);
        sortedList.sort(Comparator.comparingDouble(Student::getCgpa).reversed()
                .thenComparing(Student::getName));
        return sortedList;
    }

    /**
     * Calculates class average CGPA across all students.
     */
    public static double calculateClassAverageCgpa(List<Student> students) {
        if (students == null || students.isEmpty()) return 0.0;
        double sum = 0.0;
        int count = 0;
        for (Student s : students) {
            if (s.getCgpa() > 0) {
                sum += s.getCgpa();
                count++;
            }
        }
        return count > 0 ? sum / count : 0.0;
    }

    /**
     * Counts overall active backlogs across all students.
     */
    public static int calculateTotalActiveBacklogs(List<Student> students) {
        if (students == null) return 0;
        int backlogs = 0;
        for (Student s : students) {
            backlogs += s.getBacklogCount();
        }
        return backlogs;
    }

    /**
     * Grade distribution breakdown across all students based on CGPA.
     */
    public static Map<String, Integer> calculateCgpaDistribution(List<Student> students) {
        Map<String, Integer> dist = new HashMap<>();
        String[] grades = {"O", "A+", "A", "B+", "B", "C", "F"};
        for (String g : grades) dist.put(g, 0);

        if (students == null) return dist;

        for (Student s : students) {
            if (s.getCgpa() > 0) {
                String letter = getLetterGradeForCgpa(s.getCgpa());
                dist.put(letter, dist.getOrDefault(letter, 0) + 1);
            }
        }
        return dist;
    }
}
