package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.ui.components.SidebarNav;
import com.codealpha.gradetracker.ui.theme.ThemeManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

/**
 * Main Window for B.Tech Student Academic Performance Management System.
 * Features modern Sidebar Navigation, Light/Dark Theme Switcher, and Multi-Semester Management.
 */
public class MainFrame extends JFrame {

    private final DataManager dataManager;

    private SidebarNav sidebarNav;
    private JPanel contentContainer;
    private CardLayout cardLayout;

    private DashboardPanel dashboardPanel;
    private StudentManagementPanel studentPanel;
    private SemesterGradebookPanel gradebookPanel;
    private LeaderboardPanel leaderboardPanel;
    private AnalyticsPanel analyticsPanel;
    private JLabel statusLabel;

    public MainFrame() {
        super("CodeAlpha - B.Tech Academic Performance Management System");
        this.dataManager = new DataManager();
        dataManager.loadData();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 740));
        setPreferredSize(new Dimension(1250, 780));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 32, 48));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("B.TECH STUDENT ACADEMIC PERFORMANCE MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);

        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);

        JButton themeToggleBtn = new JButton("🌓 Toggle Theme");
        themeToggleBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        themeToggleBtn.setBackground(new Color(139, 92, 246));
        themeToggleBtn.setForeground(Color.WHITE);
        themeToggleBtn.addActionListener(e -> toggleTheme());

        JButton saveBtn = new JButton("💾 Save State");
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        saveBtn.setBackground(new Color(16, 185, 129));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            boolean saved = dataManager.saveData();
            if (saved) {
                setStatus("Data saved successfully!");
                JOptionPane.showMessageDialog(this, "Academic records saved to disk.", "Save Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JButton resetBtn = new JButton("🔄 Sample Data");
        resetBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        resetBtn.setBackground(new Color(245, 158, 11));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Reload B.Tech CSE sample dataset?", "Reload Sample Data", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataManager.loadSampleData();
                refreshAllPanels();
                setStatus("B.Tech CSE sample dataset reloaded.");
            }
        });

        headerActions.add(themeToggleBtn);
        headerActions.add(saveBtn);
        headerActions.add(resetBtn);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(headerActions, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Sidebar & Content Layout
        Runnable notifyDataChanged = () -> {
            refreshAllPanels();
            setStatus("Academic records updated.");
        };

        dashboardPanel = new DashboardPanel(dataManager);
        studentPanel = new StudentManagementPanel(dataManager, notifyDataChanged);
        gradebookPanel = new SemesterGradebookPanel(dataManager, notifyDataChanged);
        leaderboardPanel = new LeaderboardPanel(dataManager);
        analyticsPanel = new AnalyticsPanel(dataManager);

        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);

        contentContainer.add(dashboardPanel, "0");
        contentContainer.add(studentPanel, "1");
        contentContainer.add(gradebookPanel, "2");
        contentContainer.add(leaderboardPanel, "3");
        contentContainer.add(analyticsPanel, "4");

        sidebarNav = new SidebarNav(tabIndex -> {
            cardLayout.show(contentContainer, String.valueOf(tabIndex));
            refreshAllPanels();
        });

        add(sidebarNav, BorderLayout.WEST);
        add(contentContainer, BorderLayout.CENTER);

        // Status Footer
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(24, 32, 48));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        statusLabel = new JLabel("System Ready | B.Tech Academic Management OS v2.0");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(148, 163, 184));

        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        updateThemeColors();
    }

    private void toggleTheme() {
        ThemeManager.toggleTheme();
        updateThemeColors();
    }

    private void updateThemeColors() {
        if (sidebarNav != null) sidebarNav.updateThemeColors();
        if (dashboardPanel != null) dashboardPanel.updateThemeColors();
        if (studentPanel != null) studentPanel.updateThemeColors();
        if (leaderboardPanel != null) leaderboardPanel.updateThemeColors();
        if (gradebookPanel != null) gradebookPanel.updateThemeColors();
        repaint();
    }

    private void refreshAllPanels() {
        if (dashboardPanel != null) dashboardPanel.refreshData();
        if (studentPanel != null) studentPanel.refreshTable();
        if (gradebookPanel != null) gradebookPanel.refreshStudentList();
        if (leaderboardPanel != null) leaderboardPanel.refreshLeaderboard();
        if (analyticsPanel != null) analyticsPanel.refreshAnalytics();
    }

    private void setStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message + " | B.Tech Academic Management OS v2.0");
        }
    }
}
