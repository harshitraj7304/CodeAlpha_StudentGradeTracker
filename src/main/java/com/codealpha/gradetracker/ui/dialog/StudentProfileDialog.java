package com.codealpha.gradetracker.ui.dialog;

import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.ui.theme.ThemeManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

/**
 * Detailed Student Profile View showing Academic Summary, Semester SGPA Timeline,
 * and Subject Gradebook Breakdown.
 */
public class StudentProfileDialog extends JDialog {

    private final Student student;
    private final List<Student> allStudents;

    public StudentProfileDialog(Window owner, Student student, List<Student> allStudents) {
        super(owner, "Student Profile & Academic Transcript - " + student.getName(), ModalityType.APPLICATION_MODAL);
        this.student = student;
        this.allStudents = allStudents;

        setSize(850, 680);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(15, 15));

        initUI();
    }

    private void initUI() {
        JPanel container = new JPanel(new BorderLayout(15, 15));
        container.setBackground(ThemeManager.getBackgroundColor());
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Student Banner Card
        JPanel headerCard = new JPanel(new BorderLayout(15, 10));
        headerCard.setBackground(ThemeManager.getCardColor());
        headerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 6, 0, 0, ThemeManager.PRIMARY_ACCENT),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.getCardBorderColor(), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        JPanel nameDeptPanel = new JPanel(new GridLayout(3, 1, 4, 4));
        nameDeptPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(student.getName() + " (" + student.getStudentId() + ")");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setForeground(ThemeManager.getMainTextColor());

        JLabel branchLabel = new JLabel("Branch: " + student.getDepartment() + " | Current Semester: Sem " + student.getCurrentSemester());
        branchLabel.setFont(ThemeManager.FONT_BODY);
        branchLabel.setForeground(ThemeManager.getMutedTextColor());

        JLabel emailLabel = new JLabel("Email: " + student.getEmail());
        emailLabel.setFont(ThemeManager.FONT_BODY);
        emailLabel.setForeground(ThemeManager.getMutedTextColor());

        nameDeptPanel.add(nameLabel);
        nameDeptPanel.add(branchLabel);
        nameDeptPanel.add(emailLabel);

        headerCard.add(nameDeptPanel, BorderLayout.WEST);

        // Academic Summary Badges (CGPA, AICTE %, Earned Credits, Backlogs, Rank)
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setOpaque(false);

        List<Student> ranked = AcademicEngineService.getRankedStudents(allStudents);
        int rank = 1;
        for (Student s : ranked) {
            if (s.getStudentId().equals(student.getStudentId())) break;
            rank++;
        }

        statsPanel.add(createBadge("Cumulative CGPA", String.format("%.2f / 10.0", student.getCgpa()), ThemeManager.PRIMARY_ACCENT));
        statsPanel.add(createBadge("AICTE Percentage", String.format("%.1f%%", student.getEquivalentPercentage()), ThemeManager.SUCCESS_ACCENT));
        statsPanel.add(createBadge("Class Rank", "#" + rank + " of " + allStudents.size(), ThemeManager.PURPLE_ACCENT));
        statsPanel.add(createBadge("Earned Credits", student.getEarnedCredits() + " / " + student.getTotalCredits(), ThemeManager.PRIMARY_ACCENT));
        statsPanel.add(createBadge("Active Backlogs", student.getBacklogCount() > 0 ? "⚠️ " + student.getBacklogCount() : "None (Pass)", student.getBacklogCount() > 0 ? ThemeManager.DANGER_ACCENT : ThemeManager.SUCCESS_ACCENT));
        statsPanel.add(createBadge("Academic Status", student.getBacklogCount() == 0 ? "PASSED" : "PROMOTED", student.getBacklogCount() == 0 ? ThemeManager.SUCCESS_ACCENT : ThemeManager.WARNING_ACCENT));

        headerCard.add(statsPanel, BorderLayout.EAST);

        container.add(headerCard, BorderLayout.NORTH);

        // Subject Gradebook Table Across Semesters
        String[] columns = {"Semester", "Subject Code", "Subject Name", "Credits", "Percentage", "Grade Point", "Letter Grade", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (int semNum = 1; semNum <= 8; semNum++) {
            Semester sem = student.getSemester(semNum);
            for (Course sub : sem.getSubjects()) {
                Object[] row = {
                        "Semester " + semNum,
                        sub.getCourseCode(),
                        sub.getCourseName(),
                        sub.getCredits(),
                        sub.getGradeItems().isEmpty() ? "N/A" : String.format("%.1f%%", sub.getCalculatedPercentage()),
                        sub.getGradeItems().isEmpty() ? "N/A" : String.format("%.1f", sub.getGradePoint()),
                        sub.getLetterGrade(),
                        sub.getGradeItems().isEmpty() ? "ENROLLED" : (sub.isPassed() ? "PASS" : "BACKLOG")
                };
                tableModel.addRow(row);
            }
        }

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(ThemeManager.FONT_BODY);
        table.getTableHeader().setFont(ThemeManager.FONT_SUBTITLE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()),
                " Complete Semester-wise Gradebook Transcript ",
                0, 0, ThemeManager.FONT_SUBTITLE, ThemeManager.getMainTextColor()
        ));

        container.add(scrollPane, BorderLayout.CENTER);

        // Footer Action Panel
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);

        JButton closeBtn = new JButton("Close Profile");
        closeBtn.setFont(ThemeManager.FONT_BOLD_BODY);
        closeBtn.setBackground(ThemeManager.PRIMARY_ACCENT);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());

        footerPanel.add(closeBtn, BorderLayout.EAST);
        container.add(footerPanel, BorderLayout.SOUTH);

        add(container);
    }

    private JPanel createBadge(String title, String value, Color color) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 2, 2));
        panel.setOpaque(false);

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        tLabel.setForeground(ThemeManager.getMutedTextColor());

        JLabel vLabel = new JLabel(value);
        vLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        vLabel.setForeground(color);

        panel.add(tLabel);
        panel.add(vLabel);
        return panel;
    }
}
