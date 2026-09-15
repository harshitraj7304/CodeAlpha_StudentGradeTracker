package com.codealpha.gradetracker.model;

import java.io.Serializable;

/**
 * Encapsulates computed GPA, percentage, letter grade, and grade statistics.
 */
public class GradeSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double overallPercentage;
    private final double gpa4Scale;      // 0.0 - 4.0 scale
    private final double gpa10Scale;     // 0.0 - 10.0 scale
    private final String letterGrade;
    private final int totalCredits;
    private final double highestScore;
    private final double lowestScore;

    public GradeSummary(double overallPercentage, double gpa4Scale, double gpa10Scale, String letterGrade, int totalCredits, double highestScore, double lowestScore) {
        this.overallPercentage = overallPercentage;
        this.gpa4Scale = gpa4Scale;
        this.gpa10Scale = gpa10Scale;
        this.letterGrade = letterGrade;
        this.totalCredits = totalCredits;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
    }

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public double getGpa4Scale() {
        return gpa4Scale;
    }

    public double getGpa10Scale() {
        return gpa10Scale;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public double getLowestScore() {
        return lowestScore;
    }

    @Override
    public String toString() {
        return String.format("Summary: %.1f%% | Letter: %s | GPA (4.0): %.2f | GPA (10.0): %.2f",
                overallPercentage, letterGrade, gpa4Scale, gpa10Scale);
    }
}
