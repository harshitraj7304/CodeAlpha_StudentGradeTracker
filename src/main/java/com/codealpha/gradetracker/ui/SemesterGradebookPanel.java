package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.ui.theme.ThemeManager;
import com.codealpha.gradetracker.ui.theme.UIFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Multi-Semester Gradebook Panel with auto-populating subject dropdown and explicit empty state handling.
 */
public class SemesterGradebookPanel extends JPanel {

    private final DataManager dataManager;
    private final Runnable onDataChanged;

    private JComboBox<StudentItem> studentCombo;
    private JComboBox<Integer> semesterCombo;
    private JComboBox<CourseItem> subjectCombo;

    private JTable assessmentTable;
    private DefaultTableModel tableModel;
    private JLabel sgpaSummaryLabel;

    private boolean isUpdatingCombos = false;

    public SemesterGradebookPanel(DataManager dataManager, Runnable onDataChanged) {
        this.dataManager = dataManager;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        updateThemeColors();

        initUI();
        refreshStudentList();
    }

    public void updateThemeColors() {
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initUI() {
        // Top Selection Toolbar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topBar.setBackground(ThemeManager.getCardColor());
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        studentCombo = new JComboBox<>();
        studentCombo.setFont(ThemeManager.FONT_BODY);
        studentCombo.addActionListener(e -> {
            if (!isUpdatingCombos) onStudentSelected();
        });

        semesterCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        semesterCombo.setFont(ThemeManager.FONT_BODY);
        semesterCombo.addActionListener(e -> {
            if (!isUpdatingCombos) onSemesterSelected();
        });

        subjectCombo = new JComboBox<>();
        subjectCombo.setFont(ThemeManager.FONT_BODY);
        subjectCombo.addActionListener(e -> {
            if (!isUpdatingCombos) onSubjectSelected();
        });

        topBar.add(new JLabel("Select Student:"));
        topBar.add(studentCombo);
        topBar.add(new JLabel("Semester:"));
        topBar.add(semesterCombo);
        topBar.add(new JLabel("Subject:"));
        topBar.add(subjectCombo);

        JButton addScoreBtn = UIFactory.createButton("+ Add Assessment", ThemeManager.SUCCESS_ACCENT, e -> showAddAssessmentDialog());
        JButton editScoreBtn = UIFactory.createButton("Edit Score", ThemeManager.PRIMARY_ACCENT, e -> showEditAssessmentDialog());
        JButton deleteScoreBtn = UIFactory.createButton("Delete Score", ThemeManager.DANGER_ACCENT, e -> deleteSelectedAssessment());

        topBar.add(addScoreBtn);
        topBar.add(editScoreBtn);
        topBar.add(deleteScoreBtn);

        add(topBar, BorderLayout.NORTH);

        // Assessment Table
        String[] columns = {"Assessment Title", "Category", "Score", "Max Score", "Percentage", "Weightage"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        assessmentTable = new JTable(tableModel);
        assessmentTable.setRowHeight(30);
        assessmentTable.setFont(ThemeManager.FONT_BODY);
        assessmentTable.getTableHeader().setFont(ThemeManager.FONT_SUBTITLE);
        assessmentTable.getTableHeader().setBackground(new Color(230, 235, 242));
        assessmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i <= 5; i++) {
            assessmentTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(assessmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()));

        add(scrollPane, BorderLayout.CENTER);

        // Footer Summary Label
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(ThemeManager.getCardColor());
        footer.setBorder(BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()));

        sgpaSummaryLabel = new JLabel("Semester SGPA: N/A");
        sgpaSummaryLabel.setFont(ThemeManager.FONT_SUBTITLE);
        sgpaSummaryLabel.setForeground(ThemeManager.getMainTextColor());

        footer.add(sgpaSummaryLabel);
        add(footer, BorderLayout.SOUTH);
    }

    public void refreshStudentList() {
        isUpdatingCombos = true;
        studentCombo.removeAllItems();
        List<Student> students = dataManager.getStudents();
        for (Student s : students) {
            studentCombo.addItem(new StudentItem(s));
        }
        isUpdatingCombos = false;
        onStudentSelected();
    }

    private void onStudentSelected() {
        StudentItem sItem = (StudentItem) studentCombo.getSelectedItem();
        if (sItem != null && sItem.student != null) {
            isUpdatingCombos = true;
            semesterCombo.setSelectedItem(sItem.student.getCurrentSemester());
            isUpdatingCombos = false;
        }
        onSemesterSelected();
    }

    private void onSemesterSelected() {
        isUpdatingCombos = true;
        subjectCombo.removeAllItems();
        StudentItem sItem = (StudentItem) studentCombo.getSelectedItem();
        Integer semNum = (Integer) semesterCombo.getSelectedItem();

        if (sItem != null && sItem.student != null && semNum != null) {
            Semester sem = sItem.student.getSemester(semNum);
            for (Course sub : sem.getSubjects()) {
                subjectCombo.addItem(new CourseItem(sub));
            }
        }
        isUpdatingCombos = false;
        onSubjectSelected();
    }

    private void onSubjectSelected() {
        tableModel.setRowCount(0);
        StudentItem sItem = (StudentItem) studentCombo.getSelectedItem();
        Integer semNum = (Integer) semesterCombo.getSelectedItem();
        CourseItem cItem = (CourseItem) subjectCombo.getSelectedItem();

        if (sItem != null && sItem.student != null && semNum != null) {
            Semester sem = sItem.student.getSemester(semNum);

            if (sem.getSubjects().isEmpty()) {
                sgpaSummaryLabel.setText(String.format("No subjects registered for Semester %d  |  SGPA: N/A  |  CGPA: %.2f",
                        semNum, sItem.student.getCgpa()));
                return;
            }

            if (cItem != null && cItem.course != null) {
                Course sub = cItem.course;
                for (GradeItem g : sub.getGradeItems()) {
                    Object[] row = {
                            g.getTitle(),
                            g.getCategory().getDisplayName(),
                            String.format("%.1f", g.getScore()),
                            String.format("%.1f", g.getMaxScore()),
                            String.format("%.1f%%", g.getPercentage()),
                            String.format("%.0f%%", g.getWeightage() * 100)
                    };
                    tableModel.addRow(row);
                }
            }

            double sgpa = sem.getSgpa();
            String sgpaStr = sem.getSubjects().stream().anyMatch(s -> !s.getGradeItems().isEmpty()) ? String.format("%.2f / 10.0", sgpa) : "N/A";
            sgpaSummaryLabel.setText(String.format("Semester %d SGPA: %s  |  Cumulative CGPA: %.2f",
                    semNum, sgpaStr, sItem.student.getCgpa()));
        } else {
            sgpaSummaryLabel.setText("Semester SGPA: N/A");
        }
    }

    private Window getParentWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private void showAddAssessmentDialog() {
        CourseItem cItem = (CourseItem) subjectCombo.getSelectedItem();
        if (cItem == null || cItem.course == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid subject first!", "Select Subject", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course subject = cItem.course;

        JDialog dialog = new JDialog(getParentWindow(), "Add Assessment for " + subject.getCourseCode(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField titleField = new JTextField("End-Sem Examination");
        JComboBox<AssessmentCategory> categoryCombo = new JComboBox<>(AssessmentCategory.values());
        categoryCombo.setSelectedItem(AssessmentCategory.END_SEM);
        JTextField scoreField = new JTextField("85");
        JTextField maxScoreField = new JTextField("100");
        JTextField weightField = new JTextField("50");

        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Category:"));
        form.add(categoryCombo);
        form.add(new JLabel("Achieved Score:"));
        form.add(scoreField);
        form.add(new JLabel("Max Score:"));
        form.add(maxScoreField);
        form.add(new JLabel("Weightage (%):"));
        form.add(weightField);

        JButton saveBtn = new JButton("Save Assessment");
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            double score, maxScore, weightage;

            try {
                score = Double.parseDouble(scoreField.getText().trim());
                maxScore = Double.parseDouble(maxScoreField.getText().trim());
                weightage = Double.parseDouble(weightField.getText().trim()) / 100.0;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid numerical inputs!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (title.isEmpty() || score < 0 || maxScore <= 0 || weightage < 0 || weightage > 1.0) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid title, positive scores (0-max), and weightage (0-100%)!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            GradeItem item = new GradeItem(title, score, maxScore, (AssessmentCategory) categoryCombo.getSelectedItem(), weightage);
            subject.addGradeItem(item);

            onSubjectSelected();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditAssessmentDialog() {
        int selectedRow = assessmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an assessment entry to edit!", "Select Assessment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseItem cItem = (CourseItem) subjectCombo.getSelectedItem();
        if (cItem == null || cItem.course == null) return;

        Course subject = cItem.course;
        GradeItem item = subject.getGradeItems().get(selectedRow);

        JDialog dialog = new JDialog(getParentWindow(), "Edit Assessment - " + item.getTitle(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField titleField = new JTextField(item.getTitle());
        JComboBox<AssessmentCategory> categoryCombo = new JComboBox<>(AssessmentCategory.values());
        categoryCombo.setSelectedItem(item.getCategory());
        JTextField scoreField = new JTextField(String.valueOf(item.getScore()));
        JTextField maxScoreField = new JTextField(String.valueOf(item.getMaxScore()));
        JTextField weightField = new JTextField(String.valueOf((int) (item.getWeightage() * 100)));

        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Category:"));
        form.add(categoryCombo);
        form.add(new JLabel("Achieved Score:"));
        form.add(scoreField);
        form.add(new JLabel("Max Score:"));
        form.add(maxScoreField);
        form.add(new JLabel("Weightage (%):"));
        form.add(weightField);

        JButton saveBtn = new JButton("Update Assessment");
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            double score, maxScore, weightage;

            try {
                score = Double.parseDouble(scoreField.getText().trim());
                maxScore = Double.parseDouble(maxScoreField.getText().trim());
                weightage = Double.parseDouble(weightField.getText().trim()) / 100.0;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid numerical inputs!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (title.isEmpty() || score < 0 || maxScore <= 0 || weightage < 0 || weightage > 1.0) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid title, positive scores (0-max), and weightage (0-100%)!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            item.setTitle(title);
            item.setCategory((AssessmentCategory) categoryCombo.getSelectedItem());
            item.setScore(score);
            item.setMaxScore(maxScore);
            item.setWeightage(weightage);

            onSubjectSelected();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedAssessment() {
        int selectedRow = assessmentTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an assessment entry to delete!", "Select Assessment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseItem cItem = (CourseItem) subjectCombo.getSelectedItem();
        if (cItem == null || cItem.course == null) return;

        Course subject = cItem.course;
        GradeItem item = subject.getGradeItems().get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete assessment '" + item.getTitle() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            subject.removeGradeItem(item.getId());
            onSubjectSelected();
            if (onDataChanged != null) onDataChanged.run();
        }
    }

    private static class StudentItem {
        final Student student;
        StudentItem(Student student) { this.student = student; }
        @Override public String toString() { return student != null ? student.getStudentId() + " - " + student.getName() : ""; }
    }

    private static class CourseItem {
        final Course course;
        CourseItem(Course course) { this.course = course; }
        @Override public String toString() { return course != null ? course.getCourseCode() + ": " + course.getCourseName() : ""; }
    }
}
