# Requirements: CodeAlpha Student Grade Tracker

## User Stories
- **As a Teacher / Administrator**, I want to add students and enter their grades for assignments, exams, and projects so that I can keep an organized digital gradebook.
- **As a Student / User**, I want to view my overall GPA, percentage, and letter grade breakdown so that I understand my academic standing.
- **As an Educator / Student**, I want to view visual charts of grade distribution and assessment breakdowns so that I can visually identify strengths and areas for improvement.
- **As a User**, I want my data saved automatically so that I can resume tracking next time without losing entries.

## Functional Requirements

### 1. Data Model & Business Logic (CORE-01)
- **REQ-1.1**: Define immutable/mutable models for `Student`, `Course`, `AssessmentCategory` (Assignment, Quiz, Exam, Project), `GradeItem`, and `GradeSummary`.
- **REQ-1.2**: Implement calculation engine for Weighted Average Percentage, Letter Grade mapping (A+ = 90-100%, A = 80-89%, etc.), and GPA calculation on a standard 4.0 and 10.0 scale.
- **REQ-1.3**: Provide statistical aggregations (Class Mean, Highest Grade, Lowest Grade, Standard Deviation, Grade Counts).

### 2. Desktop GUI Interface (GUI-02)
- **REQ-2.1**: Main Window with clean tabbed navigation:
  - Dashboard / Overview
  - Student Directory & Course Enrollment
  - Grade Entry & Evaluation
  - Performance Analytics & Charts
  - Settings & Data Management
- **REQ-2.2**: Responsive tables, input validation (preventing invalid scores like negative numbers or values exceeding max score), and modal dialogs for adding/editing records.

### 3. Visual Analytics Engine (CHART-03)
- **REQ-3.1**: Custom Swing chart components to display:
  - Grade Distribution Bar Chart (A, B, C, D, F counts).
  - Category Breakdown Pie Chart / Progress Rings (Assignments vs Midterms vs Final Exams).
  - Student Comparison / Rank Chart.

### 4. Data Persistence & Export (DATA-04)
- **REQ-4.1**: Persist all data to JSON or structured file storage on application close or explicit save.
- **REQ-4.2**: Support loading sample data for quick demonstration.
- **REQ-4.3**: Export grade summaries to CSV format.

## Non-Functional Requirements
- **NFR-1 (UI/UX)**: Clean modern dark/light dashboard interface using Swing.
- **NFR-2 (Reliability)**: Robust exception handling for invalid numeric inputs or corrupted data files.
- **NFR-3 (Portability)**: Cross-platform Java application compatible with Java 17+.
