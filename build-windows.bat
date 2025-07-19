@echo off
echo ========================================
echo Foyer Management System - Windows Build
echo ========================================

echo.
echo [1/6] Cleaning previous build...
call mvnw.cmd clean
if %ERRORLEVEL% neq 0 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo [2/6] Running tests...
call mvnw.cmd test
if %ERRORLEVEL% neq 0 (
    echo ERROR: Tests failed!
    pause
    exit /b 1
)

echo.
echo [3/6] Building package...
call mvnw.cmd package
if %ERRORLEVEL% neq 0 (
    echo ERROR: Package build failed!
    pause
    exit /b 1
)

echo.
echo [4/6] Generating code coverage report...
call mvnw.cmd jacoco:report
if %ERRORLEVEL% neq 0 (
    echo WARNING: Code coverage report generation failed!
)

echo.
echo [5/6] Running integration tests...
call mvnw.cmd verify
if %ERRORLEVEL% neq 0 (
    echo WARNING: Integration tests failed!
)

echo.
echo [6/6] Build completed successfully!
echo.
echo Build artifacts:
echo - JAR file: target\foyer-*.jar
echo - Test results: target\surefire-reports\
echo - Coverage report: target\site\jacoco\
echo.
pause 