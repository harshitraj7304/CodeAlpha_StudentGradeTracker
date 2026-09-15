@echo off
echo ==================================================
echo  Compiling CodeAlpha Student Grade Tracker...
echo ==================================================
if not exist bin mkdir bin

javac -d bin -sourcepath src/main/java src/main/java/com/codealpha/gradetracker/Main.java src/main/java/com/codealpha/gradetracker/model/*.java src/main/java/com/codealpha/gradetracker/util/*.java src/main/java/com/codealpha/gradetracker/io/*.java src/main/java/com/codealpha/gradetracker/ui/*.java src/main/java/com/codealpha/gradetracker/ui/chart/*.java

if %ERRORLEVEL% EQU 0 (
    echo ==================================================
    echo  Compilation Successful! Launching GUI Application...
    echo ==================================================
    java -cp bin com.codealpha.gradetracker.Main
) else (
    echo Compilation failed with error code %ERRORLEVEL%
    pause
)
