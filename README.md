# CodeAlpha Student Grade Tracker
### B.Tech Student Academic Performance Management System v3.0

> A modern, professional desktop application engineered in Java for comprehensive academic tracking, multi-semester gradebook calculations, backlog management, class leaderboards, and visual analytics.

---

## 📌 Project Overview

The **CodeAlpha Student Grade Tracker** has been developed into a B.Tech Student Academic Performance Management System. Designed for university administrators, department heads, and faculty members, it automates the management of B.Tech students across 8 academic semesters.

The application calculates semester **SGPA**, cumulative **CGPA**, equivalent **AICTE Percentage**, earned credits, and active backlog failures while offering visual analytics charts, rank-based leaderboards, CSV transcript exports, and dual Light/Dark theme support.

---

## 🏢 CodeAlpha Internship Information

| Property | Details |
| :--- | :--- |
| **Organization** | CodeAlpha Internship Program |
| **Domain** | Java Programming |
| **Task Assigned** | Task 1 — Student Grade Tracker |
| **Project Name** | CodeAlpha Student Grade Tracker |
| **Application Type** | Desktop GUI & Terminal CLI Application |
| **Target Framework** | Java Swing & AWT (JDK 17) |
| **Build System** | Apache Maven & Native Scripts (`.bat` / `.ps1`) |
| **Data Storage** | Java Object Serialization (`student_grades.dat`) & CSV Exports |

---

## 🎯 Project Objective

The primary objective of this project is to provide a robust, user-friendly desktop application that simplifies academic tracking for engineering programs:
- **Streamline Student Records**: Maintain structured profile information including Student ID, Name, Branch, Semester, and Contact Email.
- **Support Multi-Semester Academic Structure**: Track course registration, credits, and grades across Semesters 1 through 8.
- **Automate Academic Calculations**: Calculate weighted course percentages, letter grades, grade points, SGPA, CGPA, AICTE percentage, and active backlogs.
- **Deliver Visual Insights**: Render vector-based grade distribution and category breakdown analytics charts.
- **Ensure Operational Reliability**: Maintain data integrity through local file persistence, export options, and 100% automated test coverage.

---

## ✨ Key Features

- **Student Directory & Profile Drawer**: Add, edit, search, and view detailed academic profiles for students across engineering branches.
- **Multi-Semester Gradebook**: Enroll courses (1–6 credits) into specific semesters (1–8) with subject classification (Theory vs. Lab).
- **Weighted Assessment Engine**: Record weighted scores across assignments, quizzes, midterms, projects, and end-sem examinations.
- **Academic Performance Metric Engine**:
  - Semester SGPA calculation based on credit-weighted grade points.
  - Cumulative CGPA tracking across all completed semesters.
  - Standard AICTE Percentage conversion: $\text{Percentage} = (\text{CGPA} - 0.5) \times 10$.
  - Earned credits tracking vs. total attempted credits.
  - Automatic backlog detection for course scores $< 40\%$.
- **Class Leaderboard**: Real-time class rank sorting based on CGPA, tie-breaking logic, and backlog indicators.
- **Academic Analytics Visualizer**: Custom vector charts built with `Graphics2D` rendering grade distribution bar charts and assessment category donut breakdowns.
- **Reports & Data Persistence**: Export class summary CSV files, individual student transcript report cards, and automatically serialize state to `student_grades.dat`.
- **Dual Light/Dark Theme**: Integrated theme manager enabling seamless instant switching between Light and Dark visual modes.
- **Terminal CLI Mode**: Interactive console interface accessible via `--cli` argument for headless terminal operation.

---

## 👤 Student Management

The **Student Directory** screen serves as the primary administrative dashboard for managing student enrollments:
- **Search & Branch Filtering**: Filter students by department (*Computer Science & Engg, Information Technology, Electronics & Comm Engg, Mechanical Engg*) or search dynamically by Name, ID, or Email.
- **Academic Status Badges**: Table rows showcase status badges (*EXCELLENT*, *GOOD*, *AT RISK*, *BACKLOG*) derived from live academic data.
- **Student Profile View**: A dedicated modal drawer displaying complete profile attributes, summary metrics, class rank, and an 8-semester gradebook transcript.

---

## 📚 Semester & Course Management

The system enforces a structured B.Tech academic model:
```
Student
 └── Semester (Semesters 1 to 8)
      └── Course / Subject (Theory or Lab, 1 to 6 Credits)
           └── Assessment (Assignment, Quiz, Midterm, End-Sem, Project)
                └── Grade Item (Achieved Score, Max Score, Weightage)
                     └── SGPA (Semester Grade Point Average)
                          └── CGPA (Cumulative Grade Point Average)
```
- **Course Enrollment**: Add subjects with course codes (e.g., `CS301`), titles (e.g., `Data Structures`), and credit values.
- **Semester Isolation**: Subjects and assessments are isolated per semester to ensure accurate SGPA evaluation without cross-semester leaks.

---

## 📝 Grade & Assessment Management

Each course supports multiple assessment items mapped to standard categories:
- **Assignments** (Default Weightage: 10%)
- **Quizzes** (Default Weightage: 10%)
- **Midterm Examinations** (Default Weightage: 30%)
- **End-Semester Examinations** (Default Weightage: 50%)
- **Projects & Practical Labs** (Default Weightage: 20%)

The system validates input scores ($0.0 \le \text{Score} \le \text{MaxScore}$) and computes weighted percentage achievements:
$$\text{Course Percentage} = \frac{\sum (\text{Score}_i / \text{MaxScore}_i \times \text{Weight}_i)}{\sum \text{Weight}_i} \times 100$$

---

## 🧮 SGPA / CGPA / Academic Calculations

### 1. Letter Grade & Grade Point Scale
Course percentages map to standard B.Tech 10-point grade scales:

| Percentage Range | Letter Grade | Grade Point | Performance Status |
| :---: | :---: | :---: | :---: |
| $\ge 90\%$ | **A+** | **10.0** | Outstanding |
| $80\% - 89\%$ | **A** | **9.0** | Excellent |
| $70\% - 79\%$ | **B** | **8.0** | Very Good |
| $60\% - 69\%$ | **C** | **7.0** | Good |
| $50\% - 59\%$ | **D** | **6.0** | Satisfactory |
| $40\% - 49\%$ | **P** | **5.0** | Pass |
| $< 40\%$ | **F** | **0.0** | Fail / Backlog |

### 2. SGPA & CGPA Formulas
- **Semester SGPA**:
  $$\text{SGPA} = \frac{\sum (\text{Grade Point}_i \times \text{Credits}_i)}{\sum \text{Credits}_i}$$
- **Cumulative CGPA**:
  $$\text{CGPA} = \frac{\sum (\text{SGPA}_j \times \text{Semester Credits}_j)}{\sum \text{Semester Credits}_j}$$
- **Backlog Behavior**: Failing a course ($<40\%$) withholds the course credits from `Earned Credits` and increments the student's `Active Backlogs` counter. Upon successful remediation, passing grades restore earned credits and clear backlog status.

---

## 🏆 Class Leaderboard

The **Leaderboard** panel provides ranked visibility into class academic standings:
- Sorts students strictly by Cumulative CGPA in descending order.
- Displays class rank (`#1`, `#2`, etc.), total earned credits, active backlog counts, and overall academic letter grades.
- Calculates class-wide average CGPA and highlights top rankers.

---

## 📊 Academic Analytics

The **Analytics** view leverages custom vector graphics to render real-time statistical distributions:
- **Grade Distribution Chart**: Bar chart illustrating the count of students achieving each letter grade ($A+$ through $F$).
- **Category Breakdown Chart**: Donut chart visualizing assessment composition across assignments, quizzes, exams, and projects.
- **Statistical Summary**: Displays class mean score, highest score, lowest score, and total assessment entries.

---

## 📁 Reports & Data Persistence

1. **Local State Persistence**: The application uses Java Object Serialization to save and restore all student, course, semester, and grade data to `student_grades.dat` automatically.
2. **CSV Class Report Export**: Generates comma-separated summary reports containing class rankings, CGPA, percentage, earned credits, and backlogs.
3. **Individual Student Report Card Export**: Exports detailed semester-by-semester subject transcripts for individual students.

---

## 🎨 User Interface & Theme System

The application layout uses a responsive **Sidebar + Header + Main Content Container** architecture:
- **Navigation Sidebar**: Provides 1-click navigation between *Dashboard Overview*, *Student Directory*, *Semester Gradebook*, *Class Leaderboard*, and *Academic Analytics*.
- **Theme Manager (`ThemeManager.java`)**: Manages tailored Light and Dark mode color palettes, font hierarchies, and contrast tokens.
- **UI Component Factory (`UIFactory.java`)**: Provides uniform button dimensions, status badge cell renderers, and KPI metric containers.

---

## 💻 Technology Stack

| Layer / Aspect | Technology |
| :--- | :--- |
| **Language** | Java 17 (JDK 17) |
| **UI Framework** | Java Swing & AWT |
| **Custom Graphics** | Java Vector `Graphics2D` (Custom Charts) |
| **Build System** | Apache Maven 3.x / Native Scripts (`.bat`, `.ps1`) |
| **Data Persistence** | Java Object Serialization (`student_grades.dat`) |
| **Export Format** | CSV (Comma-Separated Values) |
| **Testing Framework** | JUnit / Automated Java Test Runners |
| **Version Control** | Git & GitHub |

---

## 🏗️ Project Architecture

The application is structured into a clean **Layered Desktop Architecture**:

```
+-----------------------------------------------------------------+
|                   Presentation Layer (Swing UI)                 |
|  MainFrame | DashboardPanel | StudentManagementPanel | ...      |
+-----------------------------------------------------------------+
                                │
                                ▼
+-----------------------------------------------------------------+
|                    Service & Utility Layer                      |
|      AcademicEngineService      |       GradeCalculator         |
+-----------------------------------------------------------------+
                                │
                                ▼
+-----------------------------------------------------------------+
|                 Data & I/O Persistence Layer                    |
|          DataManager            |        ReportExporter         |
+-----------------------------------------------------------------+
                                │
                                ▼
+-----------------------------------------------------------------+
|                          Domain Model                           |
|  Student | Semester | Course | GradeItem | AssessmentCategory   |
+-----------------------------------------------------------------+
```

### Package Responsibilities
- `com.codealpha.gradetracker.model`: Domain entities (`Student`, `Semester`, `Course`, `GradeItem`, `AssessmentCategory`, `SubjectType`).
- `com.codealpha.gradetracker.service`: Academic engine calculations, rank sorting, and backlog statistics (`AcademicEngineService`).
- `com.codealpha.gradetracker.io`: File serialization and CSV export management (`DataManager`, `ReportExporter`).
- `com.codealpha.gradetracker.util`: Mathematical utility functions for grade distributions and averages (`GradeCalculator`).
- `com.codealpha.gradetracker.ui`: Swing application window panels, dialogs, charts, navigation, and theme system.

---

## 📂 Project Folder Structure

```
CodeAlpha_StudentGradeTracker/
├── .gitignore                              # Git exclusion rules
├── build_and_run.bat                       # Windows Batch build script
├── build_and_run.ps1                       # Windows PowerShell build script
├── pom.xml                                 # Maven project metadata
├── README.md                               # Project documentation
├── Screenshots/                            # Application screenshot gallery
│   ├── Academic Analytics.png
│   ├── Add New B.Tech Student.png
│   ├── Class Leaderboard.png
│   ├── Dashboard Overview.png
│   ├── Enroll Course.png
│   ├── Semester Gradebook.png
│   ├── Student Directory.png
│   └── Student Profile & Academic Transcript.png
└── src/
    ├── main/
    │   └── java/
    │       └── com/codealpha/gradetracker/
    │           ├── io/
    │           │   ├── DataManager.java
    │           │   └── ReportExporter.java
    │           ├── model/
    │           │   ├── AssessmentCategory.java
    │           │   ├── Course.java
    │           │   ├── GradeItem.java
    │           │   ├── GradeSummary.java
    │           │   ├── Semester.java
    │           │   ├── Student.java
    │           │   └── SubjectType.java
    │           ├── service/
    │           │   └── AcademicEngineService.java
    │           ├── ui/
    │           │   ├── chart/
    │           │   │   ├── CategoryBreakdownChart.java
    │           │   │   └── GradeDistributionChart.java
    │           │   ├── components/
    │           │   │   └── SidebarNav.java
    │           │   ├── dialog/
    │           │   │   └── StudentProfileDialog.java
    │           │   ├── theme/
    │           │   │   ├── ThemeManager.java
    │           │   │   └── UIFactory.java
    │           │   ├── AnalyticsPanel.java
    │           │   ├── ConsoleInterface.java
    │           │   ├── DashboardPanel.java
    │           │   ├── GradeEntryPanel.java
    │           │   ├── LeaderboardPanel.java
    │           │   ├── MainFrame.java
    │           │   ├── SemesterGradebookPanel.java
    │           │   └── StudentManagementPanel.java
    │           ├── util/
    │           │   └── GradeCalculator.java
    │           └── Main.java
    └── test/
        └── java/
            └── com/codealpha/gradetracker/
                ├── BTechAcademicEngineTest.java
                ├── EndToEndWorkflowAuditTest.java
                ├── FullFunctionalVerificationTest.java
                ├── GradeCalculatorTest.java
                └── GuiSmokeTest.java
```

---

## 🖼️ Screenshots

### Dashboard Overview
![Dashboard Overview](Screenshots/Dashboard%20Overview.png)

### Student Directory
![Student Directory](Screenshots/Student%20Directory.png)

### Add New B.Tech Student
![Add New B.Tech Student](Screenshots/Add%20New%20B.Tech%20Student.png)

### Student Profile & Academic Transcript
![Student Profile & Academic Transcript](Screenshots/Student%20Profile%20%26%20Academic%20Transcript.png)

### Enroll Course
![Enroll Course](Screenshots/Enroll%20Course.png)

### Semester Gradebook
![Semester Gradebook](Screenshots/Semester%20Gradebook.png)

### Class Leaderboard
![Class Leaderboard](Screenshots/Class%20Leaderboard.png)

### Academic Analytics
![Academic Analytics](Screenshots/Academic%20Analytics.png)

---

## 🧪 Testing & Verification

The project includes 4 automated verification test suites validating business logic, UI workflow, and data quality:

| Test Suite | Coverage Scope | Verified Tests | Status |
| :--- | :--- | :---: | :---: |
| **`BTechAcademicEngineTest`** | SGPA, CGPA, Backlogs, AICTE %, Leaderboards | 5 / 5 | **PASS** |
| **`FullFunctionalVerificationTest`** | 22-Point functional workflow verification | 22 / 22 | **PASS** |
| **`GuiSmokeTest`** | 30-Step GUI workflow & persistence smoke test | 30 / 30 | **PASS** |
| **`EndToEndWorkflowAuditTest`** | 14 Real-world workflow & data quality audit checkpoints | 14 / 14 | **PASS** |
| **TOTAL VERIFICATION** | **Full System Functional Verification** | **71 / 71** | **PASS (100%)** |

---

## ⚙️ Installation & Running Instructions

### Prerequisites
- **Java Development Kit (JDK 17 or higher)**
- **Apache Maven 3.6+** (Optional for Maven builds)
- **Git**

### 1. Clone Repository
```bash
git clone https://github.com/harshitraj7304/CodeAlpha_StudentGradeTracker.git
cd CodeAlpha_StudentGradeTracker
```

### 2. Build Project

#### Option A: Using Windows Batch Script
```cmd
build_and_run.bat
```

#### Option B: Using Windows PowerShell Script
```powershell
.\build_and_run.ps1
```

#### Option C: Manual Compilation
```bash
mkdir bin
javac -d bin -sourcepath "src/main/java;src/test/java" src/main/java/com/codealpha/gradetracker/Main.java src/test/java/com/codealpha/gradetracker/*.java
```

### 3. Run Automated Tests
```bash
# Run B.Tech Academic Engine Test Suite
java -cp bin com.codealpha.gradetracker.BTechAcademicEngineTest

# Run Full 22-Point Functional Verification
java -cp bin com.codealpha.gradetracker.FullFunctionalVerificationTest

# Run 30-Point GUI Smoke Test
java -cp bin com.codealpha.gradetracker.GuiSmokeTest

# Run 14-Checkpoint End-to-End Workflow Audit
java -cp bin com.codealpha.gradetracker.EndToEndWorkflowAuditTest
```

### 4. Run Application

#### GUI Application Mode (Default)
```bash
java -cp bin com.codealpha.gradetracker.Main
```

#### Terminal CLI Mode
```bash
java -cp bin com.codealpha.gradetracker.Main --cli
```

---

## 🔑 Important Source Files

- [`Main.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/Main.java): Application launcher supporting GUI initialization and `--cli` mode routing.
- [`AcademicEngineService.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/service/AcademicEngineService.java): Core service handling SGPA/CGPA calculations, backlog tracking, and leaderboard sorting.
- [`GradeCalculator.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/util/GradeCalculator.java): Mathematical utility for percentage-to-letter grade mapping and grade distribution aggregations.
- [`DataManager.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/io/DataManager.java): Manages local object serialization (`student_grades.dat`) and sample dataset reloads.
- [`ReportExporter.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/io/ReportExporter.java): Handles CSV report generation for class summaries and student transcripts.
- [`ThemeManager.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/ui/theme/ThemeManager.java): Centralized design system manager for Light and Dark mode visual themes.
- [`UIFactory.java`](file:///c:/Users/harsh/OneDrive/Desktop/CodeAlpha_StudentGradeTracker/src/main/java/com/codealpha/gradetracker/ui/theme/UIFactory.java): Component factory providing buttons, status badges, and KPI metric containers.

---

## 💡 Learning Outcomes

- **Object-Oriented Desktop Architecture**: Implemented multi-tier layered software design separating model entities, service logic, data persistence, and Swing UI.
- **Custom Vector Graphics in Swing**: Developed custom visual charts (`GradeDistributionChart`, `CategoryBreakdownChart`) using Java `Graphics2D` rendering.
- **B.Tech Academic Grading Logic**: Modeled complex academic credit systems, weighted assessment categories, SGPA/CGPA formulas, and backlog state lifecycles.
- **Data Persistence & File I/O**: Implemented object serialization alongside structured CSV file exports.
- **Automated Verification**: Engineered multi-tiered test suites covering unit calculations, functional workflows, GUI smoke testing, and real-world data audits.

---

## 🔮 Future Possibilities

*(The following items represent potential future expansions and are not currently implemented)*
- **Relational Database Integration**: Migration from binary serialization to MySQL or PostgreSQL storage.
- **Role-Based Authentication**: Admin, Faculty, and Student login portals with access control.
- **PDF Report Generation**: Exporting official PDF transcript certificates.
- **RESTful API & Web Edition**: Exposing backend logic via Spring Boot for web and mobile frontends.

---

## 👤 Author

**Harshit Raj**  
*B.Tech in Computer Science & Engineering*  
- **GitHub**: [github.com/harshitraj7304](https://github.com/harshitraj7304)  
- **Repository**: [CodeAlpha_StudentGradeTracker](https://github.com/harshitraj7304/CodeAlpha_StudentGradeTracker)

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).

---

## 🌟 Final Project Highlights

- **100% Functionally Verified**: 71 out of 71 automated test checkpoints passed.
- **Modern Desktop Presentation**: Sleek Swing interface featuring responsive navigation, custom vector charts, and Light/Dark theme switching.
- **Real-World Ready**: Designed specifically for B.Tech engineering curriculum rules, credit requirements, and backlog tracking.
