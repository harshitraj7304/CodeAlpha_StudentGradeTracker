package com.codealpha.gradetracker.io;

import com.codealpha.gradetracker.model.AssessmentCategory;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.GradeItem;
import com.codealpha.gradetracker.model.Semester;
import com.codealpha.gradetracker.model.Student;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository and Data Manager for loading, saving, and initializing B.Tech student datasets.
 */
public class DataManager {

    private static final String DATA_FILE = "student_grades.dat";
    private final List<Student> students;

    public DataManager() {
        this.students = new ArrayList<>();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        if (student != null && !students.contains(student)) {
            students.add(student);
        }
    }

    public boolean removeStudent(String studentId) {
        return students.removeIf(s -> s.getStudentId().equalsIgnoreCase(studentId));
    }

    public Student getStudentById(String studentId) {
        for (Student s : students) {
            if (s.getStudentId().equalsIgnoreCase(studentId)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Pre-populates sample B.Tech CSE (Computer Science & Engineering) dataset across Semesters 1 - 4.
     */
    public void loadSampleData() {
        students.clear();

        // --- Student 1: Alice Johnson (CSE, Sem 4, Top Performer) ---
        Student alice = new Student("S1001", "Alice Johnson", "alice.j@btech.edu", "Computer Science & Engg", 4);

        // Sem 1
        Semester sem1_a = alice.getSemester(1);
        Course m1 = new Course("MA-101", "Mathematics-I", 4);
        m1.addGradeItem(new GradeItem("Midterm Exam", 90, 100, AssessmentCategory.MIDTERM, 0.30));
        m1.addGradeItem(new GradeItem("End-Sem Exam", 94, 100, AssessmentCategory.END_SEM, 0.50));
        m1.addGradeItem(new GradeItem("Assignments", 95, 100, AssessmentCategory.ASSIGNMENT, 0.20));
        sem1_a.addSubject(m1);

        Course c_prog = new Course("CS-101", "Programming in C", 4);
        c_prog.addGradeItem(new GradeItem("Midterm Exam", 92, 100, AssessmentCategory.MIDTERM, 0.30));
        c_prog.addGradeItem(new GradeItem("End-Sem Exam", 96, 100, AssessmentCategory.END_SEM, 0.50));
        c_prog.addGradeItem(new GradeItem("C Lab Practical", 98, 100, AssessmentCategory.LAB_PRACTICAL, 0.20));
        sem1_a.addSubject(c_prog);

        // Sem 2
        Semester sem2_a = alice.getSemester(2);
        Course dsa = new Course("CS-201", "Data Structures & Algorithms", 4);
        dsa.addGradeItem(new GradeItem("Midterm Exam", 88, 100, AssessmentCategory.MIDTERM, 0.30));
        dsa.addGradeItem(new GradeItem("End-Sem Exam", 92, 100, AssessmentCategory.END_SEM, 0.50));
        dsa.addGradeItem(new GradeItem("DSA Lab Practical", 95, 100, AssessmentCategory.LAB_PRACTICAL, 0.20));
        sem2_a.addSubject(dsa);

        Course oop = new Course("CS-202", "Object Oriented Prog (Java)", 4);
        oop.addGradeItem(new GradeItem("Midterm Exam", 95, 100, AssessmentCategory.MIDTERM, 0.30));
        oop.addGradeItem(new GradeItem("End-Sem Exam", 94, 100, AssessmentCategory.END_SEM, 0.50));
        oop.addGradeItem(new GradeItem("Java Mini Project", 98, 100, AssessmentCategory.PROJECT, 0.20));
        sem2_a.addSubject(oop);

        // Sem 3
        Semester sem3_a = alice.getSemester(3);
        Course dbms = new Course("CS-301", "Database Management Systems", 4);
        dbms.addGradeItem(new GradeItem("SQL Midterm", 85, 100, AssessmentCategory.MIDTERM, 0.30));
        dbms.addGradeItem(new GradeItem("End-Sem Exam", 90, 100, AssessmentCategory.END_SEM, 0.50));
        dbms.addGradeItem(new GradeItem("DBMS Lab", 95, 100, AssessmentCategory.LAB_PRACTICAL, 0.20));
        sem3_a.addSubject(dbms);

        Course os = new Course("CS-302", "Operating Systems", 4);
        os.addGradeItem(new GradeItem("Midterm Exam", 89, 100, AssessmentCategory.MIDTERM, 0.30));
        os.addGradeItem(new GradeItem("End-Sem Exam", 91, 100, AssessmentCategory.END_SEM, 0.50));
        os.addGradeItem(new GradeItem("OS Kernel Lab", 93, 100, AssessmentCategory.LAB_PRACTICAL, 0.20));
        sem3_a.addSubject(os);

        // --- Student 2: Bob Smith (CSE, Sem 3, Good Average) ---
        Student bob = new Student("S1002", "Bob Smith", "bob.s@btech.edu", "Computer Science & Engg", 3);
        Semester sem1_b = bob.getSemester(1);
        Course m1_b = new Course("MA-101", "Mathematics-I", 4);
        m1_b.addGradeItem(new GradeItem("Midterm Exam", 75, 100, AssessmentCategory.MIDTERM, 0.30));
        m1_b.addGradeItem(new GradeItem("End-Sem Exam", 78, 100, AssessmentCategory.END_SEM, 0.70));
        sem1_b.addSubject(m1_b);

        Semester sem2_b = bob.getSemester(2);
        Course dsa_b = new Course("CS-201", "Data Structures & Algorithms", 4);
        dsa_b.addGradeItem(new GradeItem("Midterm Exam", 82, 100, AssessmentCategory.MIDTERM, 0.30));
        dsa_b.addGradeItem(new GradeItem("End-Sem Exam", 85, 100, AssessmentCategory.END_SEM, 0.70));
        sem2_b.addSubject(dsa_b);

        // --- Student 3: Charlie Davis (IT, Sem 2, Student with Backlog) ---
        Student charlie = new Student("S1003", "Charlie Davis", "charlie.d@btech.edu", "Information Technology", 2);
        Semester sem1_c = charlie.getSemester(1);
        Course m1_c = new Course("MA-101", "Mathematics-I", 4);
        m1_c.addGradeItem(new GradeItem("Midterm Exam", 32, 100, AssessmentCategory.MIDTERM, 0.30));
        m1_c.addGradeItem(new GradeItem("End-Sem Exam", 35, 100, AssessmentCategory.END_SEM, 0.70)); // < 40% = Backlog
        sem1_c.addSubject(m1_c);

        Course c_prog_c = new Course("CS-101", "Programming in C", 4);
        c_prog_c.addGradeItem(new GradeItem("Midterm Exam", 65, 100, AssessmentCategory.MIDTERM, 0.30));
        c_prog_c.addGradeItem(new GradeItem("End-Sem Exam", 68, 100, AssessmentCategory.END_SEM, 0.70));
        sem1_c.addSubject(c_prog_c);

        // --- Student 4: Diana Prince (ECE, Sem 4, High Performer) ---
        Student diana = new Student("S1004", "Diana Prince", "diana.p@btech.edu", "Electronics & Comm Engg", 4);
        Semester sem1_d = diana.getSemester(1);
        Course phy = new Course("PH-101", "Engineering Physics", 4);
        phy.addGradeItem(new GradeItem("Midterm Exam", 95, 100, AssessmentCategory.MIDTERM, 0.30));
        phy.addGradeItem(new GradeItem("End-Sem Exam", 96, 100, AssessmentCategory.END_SEM, 0.70));
        sem1_d.addSubject(phy);

        students.add(alice);
        students.add(bob);
        students.add(charlie);
        students.add(diana);
    }

    public boolean saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            loadSampleData();
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<Student> loaded = (List<Student>) ois.readObject();
            students.clear();
            if (loaded != null) {
                students.addAll(loaded);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error loading data file: " + e.getMessage());
            createBackupCopy(file);
            students.clear();
            return false;
        }
    }

    private void createBackupCopy(File sourceFile) {
        if (sourceFile != null && sourceFile.exists()) {
            File backupFile = new File(sourceFile.getAbsolutePath() + ".bak");
            try (FileInputStream in = new FileInputStream(sourceFile);
                 FileOutputStream out = new FileOutputStream(backupFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                System.out.println("Backed up original data file to: " + backupFile.getName());
            } catch (Exception ex) {
                System.err.println("Failed to create backup copy: " + ex.getMessage());
            }
        }
    }
}
