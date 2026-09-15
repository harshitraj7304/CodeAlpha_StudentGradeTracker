# Project: CodeAlpha Student Grade Tracker

## Overview
CodeAlpha Student Grade Tracker is a feature-packed Java application designed for managing student grades, tracking course performance, computing GPA and letter grades, and displaying visual analytics charts. Built with modern Java standards and a desktop GUI interface (Java Swing), it provides an intuitive experience for both individual grade tracking and class performance analysis.

## Core Features
1. **Student & Course Management**:
   - Add, edit, and manage student profiles and enrolled courses/subjects.
   - Support multiple weightage types (Assignments, Midterms, Final Exams, Projects).

2. **Grade Entry & Gradebook**:
   - Record score inputs with customizable weightages.
   - Automated calculations for:
     - Numerical Percentage (0 - 100%)
     - Letter Grades (A+, A, B, C, D, F)
     - Grade Point Average (GPA on 4.0 & 10.0 scale)
     - Highest, Lowest, and Average class/course metrics

3. **Visual Performance Charts & Analytics**:
   - Custom Java Graphics2D / Swing bar charts and grade distribution visualizers.
   - Performance breakdown by assessment category (Assignments vs Exams).
   - Trend lines showing progress across multiple terms or assessments.

4. **Data Persistence & Export**:
   - Save and load student gradebook data (JSON / File Persistence).
   - Export grade summary reports to CSV or formatted text format.

## Technology Stack
- **Language**: Java (JDK 17+)
- **UI Framework**: Java Swing with FlatLaf / Custom Graphics2D rendering
- **Architecture**: MVC (Model-View-Controller) Pattern
- **Persistence**: Jackson / JSON / File Storage
- **Build Tool**: Maven (standard directory structure)

## Success Criteria
- [ ] Complete object model for Student, Course, Assessment, and Gradebook.
- [ ] Interactive desktop GUI with navigation tabs (Dashboard, Students, Grade Entry, Analytics, Reports).
- [ ] Real-time GPA and statistical calculations (Mean, Min, Max, Median, Grade Distribution).
- [ ] Visual charts rendered cleanly within Swing UI.
- [ ] Full persistence (saving/loading grade data across application sessions).
