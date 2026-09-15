# Roadmap: CodeAlpha Student Grade Tracker

## Milestones & Phases

### Phase 1: Domain Architecture & Calculation Engine
**Goal**: Build the robust Java OOP data models and statistical grade calculation engine.
- [x] Maven project setup (`pom.xml`, package structure `com.codealpha.gradetracker`).
- [x] Create core model classes (`Student`, `Course`, `GradeItem`, `AssessmentCategory`, `GradeSummary`).
- [x] Implement `GradeCalculator` utility for weighted averages, GPA (4.0 & 10.0 scale), letter grades, and statistics (mean, min, max).
- [x] Add verification test suite `GradeCalculatorTest` (5/5 tests passed).

### Phase 2: Swing GUI Application & Gradebook Interface
**Goal**: Create a modern desktop GUI with tabbed navigation and interactive grade tables.
- [x] Set up main Swing application frame with Nimbus/System Look-and-Feel styling (`MainFrame`).
- [x] Dashboard Tab (Quick stats cards: Total Students, Class Average, Top Student, Total Enrolled Courses).
- [x] Student & Course Management Tab (`StudentManagementPanel` with student table & course enrollment dialogs).
- [x] Grade Entry Tab (`GradeEntryPanel` with interactive score entry form, category weightage, and instant score evaluation).

### Phase 3: Visual Analytics & Custom Charts
**Goal**: Develop rich visual graphs and chart components for grade distributions and category breakdowns.
- [x] Implement `GradeDistributionChart` (Custom Swing `JComponent` with Graphics2D bar rendering).
- [x] Implement `CategoryBreakdownChart` (Pie / Donut chart for assessment category weightages).
- [x] Interactive filtering & auto-updating analytics in `AnalyticsPanel`.

### Phase 4: Persistence, Export & Final Polish
**Goal**: Enable saving/loading data, exporting CSV report cards, and launcher scripts.
- [x] Implement `DataManager` with data file serialization and sample data loader.
- [x] Export grade summary reports to CSV format via `ReportExporter`.
- [x] Pre-populate sample demo dataset for instant preview.
- [x] Build & verification scripts (`build_and_run.ps1` & `build_and_run.bat`).
