package com.codealpha.gradetracker.ui;

import com.codealpha.gradetracker.io.DataManager;
import com.codealpha.gradetracker.io.ReportExporter;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.service.AcademicEngineService;
import com.codealpha.gradetracker.ui.dialog.StudentProfileDialog;
import com.codealpha.gradetracker.ui.theme.ThemeManager;

import com.codealpha.gradetracker.ui.theme.UIFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.File;
import java.util.List;

/**
 * B.Tech Student Directory & Management Panel with 3-row layout, search bar, profile viewer, and transcript exporter.
 */
public class StudentManagementPanel extends JPanel {

    private final DataManager dataManager;
    private final Runnable onDataChanged;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> branchFilterCombo;

    public StudentManagementPanel(DataManager dataManager, Runnable onDataChanged) {
        this.dataManager = dataManager;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));
        updateThemeColors();

        initUI();
        refreshTable();
    }

    public void updateThemeColors() {
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void initUI() {
        // --- 3-Row Header Container ---
        JPanel headerContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        headerContainer.setOpaque(false);

        // Row 1: Search Bar & Branch Filter
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row1.setOpaque(false);

        JLabel searchLabel = new JLabel("🔍 Search Student:");
        searchLabel.setFont(ThemeManager.FONT_BOLD_BODY);
        searchLabel.setForeground(ThemeManager.getMainTextColor());

        searchField = new JTextField(18);
        searchField.setFont(ThemeManager.FONT_BODY);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshTable(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshTable(); }
        });

        JLabel filterLabel = new JLabel("Filter Branch:");
        filterLabel.setFont(ThemeManager.FONT_BOLD_BODY);
        filterLabel.setForeground(ThemeManager.getMainTextColor());

        branchFilterCombo = new JComboBox<>(new String[]{"All Branches", "Computer Science & Engg", "Information Technology", "Electronics & Comm Engg", "Mechanical Engg"});
        branchFilterCombo.setFont(ThemeManager.FONT_BODY);
        branchFilterCombo.addActionListener(e -> refreshTable());

        row1.add(searchLabel);
        row1.add(searchField);
        row1.add(filterLabel);
        row1.add(branchFilterCombo);

        // Row 2: Action Buttons Row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row2.setOpaque(false);

        JButton addStudentBtn = UIFactory.createButton("+ Add Student", ThemeManager.PRIMARY_ACCENT, e -> showAddStudentDialog());
        JButton editStudentBtn = UIFactory.createButton("Edit Student", ThemeManager.PRIMARY_ACCENT, e -> showEditStudentDialog());
        JButton viewProfileBtn = UIFactory.createButton("📋 View Profile", ThemeManager.PURPLE_ACCENT, e -> showStudentProfile());
        JButton addCourseBtn = UIFactory.createButton("+ Enroll Course", ThemeManager.SUCCESS_ACCENT, e -> showEnrollCourseDialog());
        JButton manageCoursesBtn = UIFactory.createButton("Manage Subjects", ThemeManager.PURPLE_ACCENT, e -> showManageCoursesDialog());
        JButton exportCardBtn = UIFactory.createButton("Export Report", ThemeManager.WARNING_ACCENT, e -> exportSelectedStudentReportCard());
        JButton deleteStudentBtn = UIFactory.createButton("Remove Student", ThemeManager.DANGER_ACCENT, e -> deleteSelectedStudent());

        row2.add(addStudentBtn);
        row2.add(editStudentBtn);
        row2.add(viewProfileBtn);
        row2.add(addCourseBtn);
        row2.add(manageCoursesBtn);
        row2.add(exportCardBtn);
        row2.add(deleteStudentBtn);

        headerContainer.add(row1);
        headerContainer.add(row2);

        add(headerContainer, BorderLayout.NORTH);

        // --- Row 3: B.Tech Student Directory Table ---
        String[] columns = {"Rank", "Student ID", "Full Name", "Branch", "Current Sem", "CGPA (10.0)", "AICTE %", "Earned Credits", "Backlogs", "Academic Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(34);
        studentTable.setFont(ThemeManager.FONT_BODY);
        studentTable.getTableHeader().setFont(ThemeManager.FONT_SUBTITLE);
        studentTable.getTableHeader().setBackground(new Color(230, 235, 242));
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(8).setCellRenderer(UIFactory.getStatusBadgeRenderer());
        studentTable.getColumnModel().getColumn(9).setCellRenderer(UIFactory.getStatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getCardBorderColor()));

        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Student> students = AcademicEngineService.getRankedStudents(dataManager.getStudents());
        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String selectedBranch = branchFilterCombo != null ? (String) branchFilterCombo.getSelectedItem() : "All Branches";

        int rank = 1;
        for (Student s : students) {
            boolean matchesSearch = query.isEmpty() ||
                    s.getStudentId().toLowerCase().contains(query) ||
                    s.getName().toLowerCase().contains(query) ||
                    s.getDepartment().toLowerCase().contains(query) ||
                    s.getEmail().toLowerCase().contains(query);

            boolean matchesBranch = "All Branches".equalsIgnoreCase(selectedBranch) ||
                    s.getDepartment().equalsIgnoreCase(selectedBranch);

            if (matchesSearch && matchesBranch) {
                String statusStr;
                if (s.getBacklogCount() > 0) {
                    statusStr = "BACKLOG (" + s.getBacklogCount() + ")";
                } else if (s.getCgpa() >= 8.5) {
                    statusStr = "EXCELLENT (PASS)";
                } else if (s.getCgpa() >= 6.5) {
                    statusStr = "GOOD (PASS)";
                } else {
                    statusStr = "AT RISK (PASS)";
                }

                Object[] row = {
                        "#" + rank,
                        s.getStudentId(),
                        s.getName(),
                        s.getDepartment(),
                        "Sem " + s.getCurrentSemester(),
                        String.format("%.2f", s.getCgpa()),
                        String.format("%.1f%%", s.getEquivalentPercentage()),
                        s.getEarnedCredits() + " / " + s.getTotalCredits(),
                        s.getBacklogCount() > 0 ? "BACKLOG (" + s.getBacklogCount() + ")" : "PASS",
                        statusStr
                };
                tableModel.addRow(row);
            }
            rank++;
        }
    }

    private Window getParentWindow() {
        return SwingUtilities.getWindowAncestor(this);
    }

    private Student getSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) return null;
        String studentId = (String) tableModel.getValueAt(row, 1);
        return dataManager.getStudentById(studentId);
    }

    private void showStudentProfile() {
        Student s = getSelectedStudent();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Please select a student from the directory table first!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }
        StudentProfileDialog dialog = new StudentProfileDialog(getParentWindow(), s, dataManager.getStudents());
        dialog.setVisible(true);
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(getParentWindow(), "Add New B.Tech Student", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField idField = new JTextField("S1005");
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"Computer Science & Engg", "Information Technology", "Electronics & Comm Engg", "Mechanical Engg"});
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Branch:"));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel("Current Semester:"));
        formPanel.add(semCombo);

        JButton saveBtn = new JButton("Save Student");
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dept = (String) deptCombo.getSelectedItem();
            int sem = (Integer) semCombo.getSelectedItem();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student ID and Name are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dataManager.getStudentById(id) != null) {
                JOptionPane.showMessageDialog(dialog, "Student ID already exists!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student newStudent = new Student(id, name, email, dept, sem);
            dataManager.addStudent(newStudent);
            refreshTable();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditStudentDialog() {
        Student student = getSelectedStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(getParentWindow(), "Edit Student Details - " + student.getStudentId(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField idField = new JTextField(student.getStudentId());
        idField.setEditable(false);
        JTextField nameField = new JTextField(student.getName());
        JTextField emailField = new JTextField(student.getEmail());
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"Computer Science & Engg", "Information Technology", "Electronics & Comm Engg", "Mechanical Engg"});
        deptCombo.setSelectedItem(student.getDepartment());
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        semCombo.setSelectedItem(student.getCurrentSemester());

        formPanel.add(new JLabel("Student ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Full Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Branch:"));
        formPanel.add(deptCombo);
        formPanel.add(new JLabel("Current Semester:"));
        formPanel.add(semCombo);

        JButton saveBtn = new JButton("Update Details");
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dept = (String) deptCombo.getSelectedItem();
            int sem = (Integer) semCombo.getSelectedItem();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Student Name is required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            student.setName(name);
            student.setEmail(email);
            student.setDepartment(dept);
            student.setCurrentSemester(sem);

            refreshTable();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEnrollCourseDialog() {
        Student student = getSelectedStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student from the directory table first!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(getParentWindow(), "Enroll Course for " + student.getName(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        semCombo.setSelectedItem(student.getCurrentSemester());
        JTextField codeField = new JTextField("CS-301");
        JTextField nameField = new JTextField("Compiler Design");
        JTextField creditsField = new JTextField("4");

        formPanel.add(new JLabel("Target Semester:"));
        formPanel.add(semCombo);
        formPanel.add(new JLabel("Subject Code:"));
        formPanel.add(codeField);
        formPanel.add(new JLabel("Subject Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Credits (2-5):"));
        formPanel.add(creditsField);

        JButton saveBtn = new JButton("Enroll Subject");
        saveBtn.addActionListener(e -> {
            int semNum = (Integer) semCombo.getSelectedItem();
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            int credits = 3;
            try {
                credits = Integer.parseInt(creditsField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid credits value!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (code.isEmpty() || name.isEmpty() || credits < 1 || credits > 10) {
                JOptionPane.showMessageDialog(dialog, "Subject Code, Name, and valid Credits (1-10) are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Course course = new Course(code, name, credits);
            student.getSemester(semNum).addSubject(course);
            refreshTable();
            if (onDataChanged != null) onDataChanged.run();
            dialog.dispose();
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showManageCoursesDialog() {
        Student student = getSelectedStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student first!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(getParentWindow(), "Manage Enrolled Subjects - " + student.getName(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 320);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"Semester", "Subject Code", "Subject Name", "Credits", "Assessments"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (int semNum = 1; semNum <= 8; semNum++) {
            for (Course c : student.getSemester(semNum).getSubjects()) {
                model.addRow(new Object[]{"Sem " + semNum, c.getCourseCode(), c.getCourseName(), c.getCredits(), c.getGradeItems().size()});
            }
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);

        JButton removeBtn = new JButton("Unenroll Selected Subject");
        removeBtn.setBackground(ThemeManager.DANGER_ACCENT);
        removeBtn.setForeground(Color.WHITE);
        removeBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String code = (String) model.getValueAt(row, 1);
                Course course = student.getCourseById(code);
                if (course != null) {
                    int confirm = JOptionPane.showConfirmDialog(dialog, "Unenroll " + course.getCourseCode() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        student.removeCourse(course.getId());
                        model.removeRow(row);
                        refreshTable();
                        if (onDataChanged != null) onDataChanged.run();
                    }
                }
            }
        });

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.add(removeBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void exportSelectedStudentReportCard() {
        Student student = getSelectedStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to export report card for!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Transcript Report Card for " + student.getName());
        chooser.setSelectedFile(new File(student.getStudentId() + "_BTech_Transcript.csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            boolean ok = ReportExporter.exportStudentDetailedReportToCsv(student, file);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Transcript exported successfully to:\n" + file.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to export transcript file.", "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedStudent() {
        Student student = getSelectedStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to remove!", "Select Student", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove student " + student.getName() + " (" + student.getStudentId() + ")?", "Confirm Removal", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.removeStudent(student.getStudentId());
            refreshTable();
            if (onDataChanged != null) onDataChanged.run();
        }
    }
}
