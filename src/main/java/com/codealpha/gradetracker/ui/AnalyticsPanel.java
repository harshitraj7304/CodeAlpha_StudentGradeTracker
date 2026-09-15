package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.GradeSummary;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.ui.chart.CategoryBreakdownChart;
import com.codealpha.gradetracker.ui.chart.GradeDistributionChart;
import com.codealpha.gradetracker.util.GradeCalculator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dedicated Analytics Panel for statistical visualizations and grade reports.
 */
public class AnalyticsPanel extends JPanel {

    private final DataManager dataManager;

    private GradeDistributionChart distributionChart;
    private CategoryBreakdownChart categoryChart;

    private JLabel meanPctLabel;
    private JLabel maxPctLabel;
    private JLabel minPctLabel;
    private JLabel totalAssessedLabel;

    public AnalyticsPanel(DataManager dataManager) {
        this.dataManager = dataManager;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        refreshAnalytics();
    }

    private void initUI() {
        // Top Toolbar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        topBar.setOpaque(false);

        JButton exportBtn = new JButton("Export Report (CSV)");
        exportBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportBtn.setBackground(new Color(41, 128, 185));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.addActionListener(e -> exportCsvReport());

        topBar.add(exportBtn);
        add(topBar, BorderLayout.NORTH);

        // Stats Cards Row
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        meanPctLabel = new JLabel("0.0%", SwingConstants.CENTER);
        maxPctLabel = new JLabel("0.0%", SwingConstants.CENTER);
        minPctLabel = new JLabel("0.0%", SwingConstants.CENTER);
        totalAssessedLabel = new JLabel("0", SwingConstants.CENTER);

        statsPanel.add(createStatCard("Class Mean Score", meanPctLabel, new Color(41, 128, 185)));
        statsPanel.add(createStatCard("Highest Grade", maxPctLabel, new Color(39, 174, 96)));
        statsPanel.add(createStatCard("Lowest Grade", minPctLabel, new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Total Assessments", totalAssessedLabel, new Color(142, 68, 173)));

        // Charts Row
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);

        distributionChart = new GradeDistributionChart();
        categoryChart = new CategoryBreakdownChart();

        chartsPanel.add(distributionChart);
        chartsPanel.add(categoryChart);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(chartsPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        titleLabel.setForeground(new Color(120, 130, 140));

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(new Color(44, 62, 80));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refreshAnalytics() {
        List<Student> students = dataManager.getStudents();

        double classAvgPct = GradeCalculator.calculateClassAverage(students);
        meanPctLabel.setText(String.format("%.1f%%", classAvgPct));

        double maxPct = 0.0;
        double minPct = 100.0;
        int totalAssessments = 0;
        boolean hasScores = false;

        Map<AssessmentCategory, Double> categoryCounts = new HashMap<>();

        for (Student s : students) {
            for (Course c : s.getCourses()) {
                for (GradeItem item : c.getGradeItems()) {
                    hasScores = true;
                    totalAssessments++;
                    double pct = item.getPercentage();
                    if (pct > maxPct) maxPct = pct;
                    if (pct < minPct) minPct = pct;

                    categoryCounts.put(item.getCategory(), categoryCounts.getOrDefault(item.getCategory(), 0.0) + 1.0);
                }
            }
        }

        maxPctLabel.setText(hasScores ? String.format("%.1f%%", maxPct) : "N/A");
        minPctLabel.setText(hasScores ? String.format("%.1f%%", minPct) : "N/A");
        totalAssessedLabel.setText(String.valueOf(totalAssessments));

        distributionChart.setData(GradeCalculator.calculateGradeDistribution(students));
        categoryChart.setData(categoryCounts);
    }

    private void exportCsvReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Class Report as CSV");
        fileChooser.setSelectedFile(new File("Class_Grade_Summary_Report.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            boolean success = com.codealpha.gradetracker.io.ReportExporter.exportClassReportToCsv(dataManager.getStudents(), selectedFile);
            if (success) {
                JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + selectedFile.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export CSV report file.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
