# Build and Run Script for CodeAlpha Student Grade Tracker
Write-Host "==================================================" -ForegroundColor Cyan
Write-Host " Compiling CodeAlpha Student Grade Tracker..." -ForegroundColor Cyan
Write-Host "==================================================" -ForegroundColor Cyan

if (-not (Test-Path bin)) {
    New-Item -ItemType Directory -Path bin | Out-Null
}

javac -d bin -sourcepath "src/main/java" (Get-ChildItem -Path "src/main/java" -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)

if ($LASTEXITCODE -eq 0) {
    Write-Host "==================================================" -ForegroundColor Green
    Write-Host " Compilation Successful! Launching GUI..." -ForegroundColor Green
    Write-Host "==================================================" -ForegroundColor Green
    java -cp bin com.codealpha.gradetracker.Main
} else {
    Write-Host "Compilation failed with error code $LASTEXITCODE" -ForegroundColor Red
}
