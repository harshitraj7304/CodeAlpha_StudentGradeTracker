package com.codealpha.gradetracker.io;

import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Exporter utility for generating B.Tech Class Leaderboards, Transcripts, and CSV Report Cards.
 */
public class ReportExporter {

    public static boolean exportClassReportToCsv(List<Student> students, File targetFile) {
        if (students == null || targetFile == null) return false;

        try (PrintWriter writer = new PrintWriter(targetFile)) {
            writer.println("Rank,Student ID,Name,Email,Branch,Current Semester,Total Credits,Earned Credits,Backlogs,CGPA,Percentage,Grade");

            List<Student> ranked = AcademicEngineService.getRankedStudents(students);
            int rank = 1;

            for (Student student : ranked) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",Sem %d,%d,%d,%d,%.2f,%.2f%%,\"%s\"\n",
                        rank++,
                        escapeCsv(student.getStudentId()),
                        escapeCsv(student.getName()),
                        escapeCsv(student.getEmail()),
                        escapeCsv(student.getDepartment()),
                        student.getCurrentSemester(),
                        student.getTotalCredits(),
                        student.getEarnedCredits(),
                        student.getBacklogCount(),
                        student.getCgpa(),
                        student.getEquivalentPercentage(),
                        AcademicEngineService.getLetterGradeForCgpa(student.getCgpa())
                );
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to export B.Tech class report: " + e.getMessage());
            return false;
        }
    }

    public static boolean exportStudentDetailedReportToCsv(Student student, File targetFile) {
        if (student == null || targetFile == null) return false;

        try (PrintWriter writer = new PrintWriter(targetFile)) {
            writer.println("OFFICIAL B.TECH ACADEMIC TRANSCRIPT REPORT");
            writer.println("Student ID," + escapeCsv(student.getStudentId()));
            writer.println("Student Name," + escapeCsv(student.getName()));
            writer.println("Branch / Department," + escapeCsv(student.getDepartment()));
            writer.println("Current Semester,Semester " + student.getCurrentSemester());
            writer.printf("Cumulative CGPA,%.2f / 10.0\n", student.getCgpa());
            writer.printf("AICTE Percentage Equivalent,%.2f%%\n", student.getEquivalentPercentage());
            writer.printf("Total Earned Credits,%d / %d\n", student.getEarnedCredits(), student.getTotalCredits());
            writer.printf("Active Backlogs Count,%d\n\n", student.getBacklogCount());

            writer.println("Semester,Subject Code,Subject Name,Credits,Assessment Title,Category,Score,Max Score,Percentage,Grade Point,Letter Grade,Status");

            for (int semNum = 1; semNum <= 8; semNum++) {
                Semester sem = student.getSemester(semNum);
                if (sem.getSubjects().isEmpty()) continue;

                for (Course subject : sem.getSubjects()) {
                    for (GradeItem item : subject.getGradeItems()) {
                        writer.printf("Semester %d,\"%s\",\"%s\",%d,\"%s\",\"%s\",%.2f,%.2f,%.2f%%,%.1f,\"%s\",\"%s\"\n",
                                semNum,
                                escapeCsv(subject.getCourseCode()),
                                escapeCsv(subject.getCourseName()),
                                subject.getCredits(),
                                escapeCsv(item.getTitle()),
                                escapeCsv(item.getCategory().getDisplayName()),
                                item.getScore(),
                                item.getMaxScore(),
                                item.getPercentage(),
                                subject.getGradePoint(),
                                subject.getLetterGrade(),
                                subject.isPassed() ? "PASS" : "BACKLOG"
                        );
                    }
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to export detailed B.Tech transcript: " + e.getMessage());
            return false;
        }
    }

    private static String escapeCsv(String input) {
        if (input == null) return "";
        return input.replace("\"", "\"\"");
    }
}
