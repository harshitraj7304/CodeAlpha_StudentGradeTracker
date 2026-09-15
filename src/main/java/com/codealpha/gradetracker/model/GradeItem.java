package com.codealpha.gradetracker.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

/**
 * Model representing a single grade item or assessment.
 */
public class GradeItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String title;
    private double score;
    private double maxScore;
    private double weightage; // e.g. 0.20 for 20%
    private AssessmentCategory category;
    private LocalDate date;

    public GradeItem(String title, double score, double maxScore, AssessmentCategory category, double weightage) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.score = Math.max(0.0, score);
        this.maxScore = maxScore > 0 ? maxScore : 100.0;
        this.category = category != null ? category : AssessmentCategory.OTHER;
        this.weightage = weightage > 0 ? weightage : this.category.getDefaultWeightage();
        this.date = LocalDate.now();
    }

    public GradeItem(String id, String title, double score, double maxScore, AssessmentCategory category, double weightage, LocalDate date) {
        this.id = id != null ? id : UUID.randomUUID().toString().substring(0, 8);
        this.title = title;
        this.score = Math.max(0.0, score);
        this.maxScore = maxScore > 0 ? maxScore : 100.0;
        this.category = category != null ? category : AssessmentCategory.OTHER;
        this.weightage = weightage;
        this.date = date != null ? date : LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = Math.max(0.0, score);
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore > 0 ? maxScore : 100.0;
    }

    public double getWeightage() {
        return weightage;
    }

    public void setWeightage(double weightage) {
        this.weightage = weightage;
    }

    public AssessmentCategory getCategory() {
        return category;
    }

    public void setCategory(AssessmentCategory category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Percentage score achieved on this item (0 - 100%).
     */
    public double getPercentage() {
        if (maxScore <= 0) return 0.0;
        return (score / maxScore) * 100.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GradeItem gradeItem = (GradeItem) o;
        return Objects.equals(id, gradeItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %.1f/%.1f (%.1f%%)", title, category.getDisplayName(), score, maxScore, getPercentage());
    }
}
