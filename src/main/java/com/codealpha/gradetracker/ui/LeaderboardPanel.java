package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.ui.theme.ThemeManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Class Rank & Leaderboard Panel highlighting student academic standings ordered by CGPA.
 */
public class LeaderboardPanel extends JPanel {

    private final DataManager dataManager;
    private JTable leaderboardTable;
    private DefaultTableModel tableModel;

    private JLabel topRankerVal;
    private JLabel classAvgCgpaVal;
    private JLabel totalBacklogsVal;

    public LeaderboardPanel(DataManager dataManager) {
        this.dataManager = dataManager;

        setLayout(new BorderLayout(15, 15));
        updateThemeColors();

        initUI();
        refreshLeaderboard();
    }

    public void updateThemeColors() {
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initUI() {
        // KPI Summary Header
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        kpiPanel.setOpaque(false);

        topRankerVal = new JLabel("N/A", SwingConstants.CENTER);
        classAvgCgpaVal = new JLabel("0.00", SwingConstants.CENTER);
        totalBacklogsVal = new JLabel("0", SwingConstants.CENTER);

        kpiPanel.add(createKpiCard("🏆 Top Ranker", topRankerVal, ThemeManager.PRIMARY_ACCENT));
        kpiPanel.add(createKpiCard("📈 Class Avg CGPA", classAvgCgpaVal, ThemeManager.SUCCESS_ACCENT));
        kpiPanel.add(createKpiCard("⚠️ Active Backlogs", totalBacklogsVal, ThemeManager.DANGER_ACCENT));

        add(kpiPanel, BorderLayout.NORTH);

        // Leaderboard Table
        String[] columns = {"Rank", "Student ID", "Full Name", "Branch", "Current Sem", "Earned Credits", "Backlogs", "CGPA (10.0)", "AICTE %", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(34);
        leaderboardTable.setFont(ThemeManager.FONT_BODY);
        leaderboardTable.getTableHeader().setFont(ThemeManager.FONT_SUBTITLE);
        leaderboardTable.getTableHeader().setBackground(new Color(230, 235, 242));
        leaderboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        leaderboardTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);
        leaderboardTable.getColumnModel().getColumn(9).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()));

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(ThemeManager.getCardColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.getCardBorderColor(), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_SUBTITLE);
        titleLabel.setForeground(ThemeManager.getMutedTextColor());

        valueLabel.setFont(ThemeManager.FONT_VALUE);
        valueLabel.setForeground(ThemeManager.getMainTextColor());

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refreshLeaderboard() {
        tableModel.setRowCount(0);
        List<Student> ranked = AcademicEngineService.getRankedStudents(dataManager.getStudents());

        int rank = 1;
        for (Student s : ranked) {
            Object[] row = {
                    "#" + (rank++),
                    s.getStudentId(),
                    s.getName(),
                    s.getDepartment(),
                    "Sem " + s.getCurrentSemester(),
                    s.getEarnedCredits() + " / " + s.getTotalCredits(),
                    s.getBacklogCount() > 0 ? "⚠️ " + s.getBacklogCount() : "None",
                    String.format("%.2f", s.getCgpa()),
                    String.format("%.1f%%", s.getEquivalentPercentage()),
                    AcademicEngineService.getLetterGradeForCgpa(s.getCgpa())
            };
            tableModel.addRow(row);
        }

        if (!ranked.isEmpty()) {
            Student top = ranked.get(0);
            topRankerVal.setText(top.getName() + " (" + String.format("%.2f", top.getCgpa()) + ")");
        } else {
            topRankerVal.setText("N/A");
        }

        double avgCgpa = AcademicEngineService.calculateClassAverageCgpa(ranked);
        classAvgCgpaVal.setText(String.format("%.2f / 10.0", avgCgpa));

        int totalBacklogs = AcademicEngineService.calculateTotalActiveBacklogs(ranked);
        totalBacklogsVal.setText(String.valueOf(totalBacklogs));
    }
}
