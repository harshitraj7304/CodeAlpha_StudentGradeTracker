package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.ui.chart.CategoryBreakdownChart;
import com.codealpha.gradetracker.ui.chart.GradeDistributionChart;
import com.codealpha.gradetracker.ui.theme.ThemeManager;
import com.codealpha.gradetracker.ui.theme.UIFactory;
import com.codealpha.gradetracker.util.GradeCalculator;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Overview Dashboard featuring B.Tech KPI metric cards and visual analytics.
 */
public class DashboardPanel extends JPanel {

    private final DataManager dataManager;
    private JLabel totalStudentsVal;
    private JLabel classAvgVal;
    private JLabel topStudentVal;
    private JLabel activeBacklogsVal;

    private GradeDistributionChart distributionChart;
    private CategoryBreakdownChart categoryChart;

    public DashboardPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(15, 15));
        updateThemeColors();

        initUI();
        refreshData();
    }

    public void updateThemeColors() {
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initUI() {
        // KPI Header Panel
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        kpiPanel.setOpaque(false);

        totalStudentsVal = new JLabel("0", SwingConstants.CENTER);
        classAvgVal = new JLabel("0.00", SwingConstants.CENTER);
        topStudentVal = new JLabel("N/A", SwingConstants.CENTER);
        activeBacklogsVal = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(UIFactory.createKpiCard("Total Students", totalStudentsVal, "Active Enrolled", ThemeManager.PRIMARY_ACCENT));
        kpiPanel.add(UIFactory.createKpiCard("Class Avg CGPA", classAvgVal, "Cumulative", ThemeManager.SUCCESS_ACCENT));
        kpiPanel.add(UIFactory.createKpiCard("Top Ranker", topStudentVal, "Highest Academic Standing", ThemeManager.PURPLE_ACCENT));
        kpiPanel.add(UIFactory.createKpiCard("Active Backlogs", activeBacklogsVal, "Requires Attention", ThemeManager.DANGER_ACCENT));

        add(kpiPanel, BorderLayout.NORTH);

        // Charts Section
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartsPanel.setOpaque(false);

        distributionChart = new GradeDistributionChart();
        categoryChart = new CategoryBreakdownChart();

        chartsPanel.add(distributionChart);
        chartsPanel.add(categoryChart);

        add(chartsPanel, BorderLayout.CENTER);
    }

    public void refreshData() {
        List<Student> students = dataManager.getStudents();
        totalStudentsVal.setText(String.valueOf(students.size()));

        double avgCgpa = AcademicEngineService.calculateClassAverageCgpa(students);
        double avgPct = AcademicEngineService.convertCgpaToPercentage(avgCgpa);
        classAvgVal.setText(String.format("%.2f (%.1f%%)", avgCgpa, avgPct));

        List<Student> ranked = AcademicEngineService.getRankedStudents(students);
        if (!ranked.isEmpty()) {
            Student top = ranked.get(0);
            topStudentVal.setText(top.getName());
        } else {
            topStudentVal.setText("N/A");
        }

        int backlogs = AcademicEngineService.calculateTotalActiveBacklogs(students);
        activeBacklogsVal.setText(String.valueOf(backlogs));

        Map<AssessmentCategory, Double> categoryCounts = new HashMap<>();
        for (Student s : students) {
            for (Semester sem : s.getSemesters().values()) {
                for (Course c : sem.getSubjects()) {
                    for (GradeItem item : c.getGradeItems()) {
                        categoryCounts.put(item.getCategory(), categoryCounts.getOrDefault(item.getCategory(), 0.0) + 1.0);
                    }
                }
            }
        }

        distributionChart.setData(GradeCalculator.calculateGradeDistribution(students));
        categoryChart.setData(categoryCounts);
    }
}
