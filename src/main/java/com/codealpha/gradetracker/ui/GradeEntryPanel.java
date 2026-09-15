package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.util.GradeCalculator;

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
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.List;

/**
 * Grade Entry and Gradebook Evaluation Panel with Score Editing & Input Validation.
 */
public class GradeEntryPanel extends JPanel {

    private final DataManager dataManager;
    private final Runnable onDataChanged;

    private JComboBox<StudentItem> studentCombo;
    private JComboBox<CourseItem> courseCombo;
    private JLabel courseSummaryLabel;

    private JTable gradeTable;
    private DefaultTableModel tableModel;

    public GradeEntryPanel(DataManager dataManager, Runnable onDataChanged) {
        this.dataManager = dataManager;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initUI();
        refreshStudentList();
    }

    private void initUI() {
        // --- Top Selection Bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        studentCombo = new JComboBox<>();
        studentCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        studentCombo.addActionListener(e -> onStudentSelected());

        courseCombo = new JComboBox<>();
        courseCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        courseCombo.addActionListener(e -> onCourseSelected());

        topBar.add(new JLabel("Select Student:"));
        topBar.add(studentCombo);
        topBar.add(new JLabel("Select Course:"));
        topBar.add(courseCombo);

        JButton addGradeBtn = new JButton("+ Add Grade Score");
        addGradeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addGradeBtn.setBackground(new Color(39, 174, 96));
        addGradeBtn.setForeground(Color.WHITE);
        addGradeBtn.addActionListener(e -> showAddGradeDialog());

        JButton editGradeBtn = new JButton("Edit Score");
        editGradeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        editGradeBtn.setBackground(new Color(52, 152, 219));
        editGradeBtn.setForeground(Color.WHITE);
        editGradeBtn.addActionListener(e -> showEditGradeDialog());

        JButton deleteGradeBtn = new JButton("Delete Score");
        deleteGradeBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        deleteGradeBtn.setBackground(new Color(231, 76, 60));
        deleteGradeBtn.setForeground(Color.WHITE);
        deleteGradeBtn.addActionListener(e -> deleteSelectedGrade());

        topBar.add(addGradeBtn);
        topBar.add(editGradeBtn);
        topBar.add(deleteGradeBtn);

        add(topBar, BorderLayout.NORTH);

        // --- Center Table & Summary Footer ---
        String[] columns = {"Assessment Title", "Category", "Score", "Max Score", "Percentage", "Weightage"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        gradeTable = new JTable(tableModel);
        gradeTable.setRowHeight(30);
        gradeTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gradeTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        gradeTable.getTableHeader().setBackground(new Color(230, 235, 242));
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        gradeTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        gradeTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        gradeTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        gradeTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        gradeTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        add(scrollPane, BorderLayout.CENTER);

        // Footer Summary Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        courseSummaryLabel = new JLabel("Course Performance: N/A");
        courseSummaryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        courseSummaryLabel.setForeground(new Color(44, 62, 80));

        footerPanel.add(courseSummaryLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private Window getParentWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    public void refreshStudentList() {
        studentCombo.removeAllItems();
        List<Student> students = dataManager.getStudents();
        for (Student s : students) {
            studentCombo.addItem(new StudentItem(s));
        }
        onStudentSelected();
    }

    private void onStudentSelected() {
        courseCombo.removeAllItems();
        StudentItem item = (StudentItem) studentCombo.getSelectedItem();
        if (item != null && item.student != null) {
            for (Course c : item.student.getCourses()) {
                courseCombo.addItem(new CourseItem(c));
            }
        }
        onCourseSelected();
    }

    private void onCourseSelected() {
        tableModel.setRowCount(0);
        CourseItem item = (CourseItem) courseCombo.getSelectedItem();
        if (item != null && item.course != null) {
            Course c = item.course;
            for (GradeItem g : c.getGradeItems()) {
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
            double pct = c.getCalculatedPercentage();
            String letter = GradeCalculator.getLetterGrade(pct);
            courseSummaryLabel.setText(String.format("Calculated Course Score: %.1f%% (%s Grade)", pct, letter));
        } else {
            courseSummaryLabel.setText("Course Performance: No Course Selected");
        }
    }

    private void showAddGradeDialog() {
        CourseItem courseItem = (CourseItem) courseCombo.getSelectedItem();
        if (courseItem == null || courseItem.course == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid course first!", "Select Course", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course course = courseItem.course;

        JDialog dialog = new JDialog(getParentWindow(), "Add Assessment Score for " + course.getCourseCode(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField titleField = new JTextField("Quiz 1");
        JComboBox<AssessmentCategory> categoryCombo = new JComboBox<>(AssessmentCategory.values());
        JTextField scoreField = new JTextField("85");
        JTextField maxScoreField = new JTextField("100");
        JTextField weightField = new JTextField("20");

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Achieved Score:"));
        formPanel.add(scoreField);
        formPanel.add(new JLabel("Max Score:"));
        formPanel.add(maxScoreField);
        formPanel.add(new JLabel("Weightage (%):"));
        formPanel.add(weightField);

        JButton saveBtn = new JButton("Save Score");
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            double score, maxScore, weightage;

            try {
                score = Double.parseDouble(scoreField.getText().trim());
                maxScore = Double.parseDouble(maxScoreField.getText().trim());
                weightage = Double.parseDouble(weightField.getText().trim()) / 100.0;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for scores and weightage!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Assessment title is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (score < 0 || maxScore <= 0) {
                JOptionPane.showMessageDialog(dialog, "Achieved score cannot be negative and Max Score must be greater than zero!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (weightage < 0 || weightage > 1.0) {
                JOptionPane.showMessageDialog(dialog, "Weightage percentage must be between 0% and 100%!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AssessmentCategory category = (AssessmentCategory) categoryCombo.getSelectedItem();
            GradeItem item = new GradeItem(title, score, maxScore, category, weightage);
            course.addGradeItem(item);

            onCourseSelected();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditGradeDialog() {
        int selectedRow = gradeTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a grade entry to edit!", "Select Grade", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseItem courseItem = (CourseItem) courseCombo.getSelectedItem();
        if (courseItem == null || courseItem.course == null) return;

        Course course = courseItem.course;
        GradeItem targetItem = course.getGradeItems().get(selectedRow);

        JDialog dialog = new JDialog(getParentWindow(), "Edit Assessment Score - " + targetItem.getTitle(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField titleField = new JTextField(targetItem.getTitle());
        JComboBox<AssessmentCategory> categoryCombo = new JComboBox<>(AssessmentCategory.values());
        categoryCombo.setSelectedItem(targetItem.getCategory());
        JTextField scoreField = new JTextField(String.valueOf(targetItem.getScore()));
        JTextField maxScoreField = new JTextField(String.valueOf(targetItem.getMaxScore()));
        JTextField weightField = new JTextField(String.valueOf((int) (targetItem.getWeightage() * 100)));

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryCombo);
        formPanel.add(new JLabel("Achieved Score:"));
        formPanel.add(scoreField);
        formPanel.add(new JLabel("Max Score:"));
        formPanel.add(maxScoreField);
        formPanel.add(new JLabel("Weightage (%):"));
        formPanel.add(weightField);

        JButton saveBtn = new JButton("Update Score");
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            double score, maxScore, weightage;

            try {
                score = Double.parseDouble(scoreField.getText().trim());
                maxScore = Double.parseDouble(maxScoreField.getText().trim());
                weightage = Double.parseDouble(weightField.getText().trim()) / 100.0;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for scores and weightage!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Assessment title is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (score < 0 || maxScore <= 0) {
                JOptionPane.showMessageDialog(dialog, "Achieved score cannot be negative and Max Score must be greater than zero!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (weightage < 0 || weightage > 1.0) {
                JOptionPane.showMessageDialog(dialog, "Weightage percentage must be between 0% and 100%!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            targetItem.setTitle(title);
            targetItem.setCategory((AssessmentCategory) categoryCombo.getSelectedItem());
            targetItem.setScore(score);
            targetItem.setMaxScore(maxScore);
            targetItem.setWeightage(weightage);

            onCourseSelected();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void deleteSelectedGrade() {
        int row = gradeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a grade entry to delete!", "Select Grade", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CourseItem courseItem = (CourseItem) courseCombo.getSelectedItem();
        if (courseItem == null || courseItem.course == null) return;

        Course course = courseItem.course;
        GradeItem itemToRemove = course.getGradeItems().get(row);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete assessment '" + itemToRemove.getTitle() + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            course.removeGradeItem(itemToRemove.getId());
            onCourseSelected();
            if (onDataChanged != null) onDataChanged.run();
        }
    }

    private static class StudentItem {
        final Student student;
        StudentItem(Student student) { this.student = student; }
        @Override
        public String toString() { return student != null ? student.getStudentId() + " - " + student.getName() : ""; }
    }

    private static class CourseItem {
        final Course course;
        CourseItem(Course course) { this.course = course; }
        @Override
        public String toString() { return course != null ? course.getCourseCode() + ": " + course.getCourseName() : ""; }
    }
}
