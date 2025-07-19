# Foyer Management System - Windows PowerShell Build Script
# Run this script with: powershell -ExecutionPolicy Bypass -File build-windows.ps1

param(
    [switch]$SkipTests,
    [switch]$SkipCoverage,
    [switch]$Verbose
)

$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Foyer Management System - Windows Build" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Maven wrapper exists
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "ERROR: Maven wrapper (mvnw.cmd) not found!" -ForegroundColor Red
    Write-Host "Please ensure you're running this script from the project root directory." -ForegroundColor Red
    exit 1
}

# Check if Java is available
try {
    $javaVersion = & java -version 2>&1
    Write-Host "Java version detected:" -ForegroundColor Green
    $javaVersion | Select-Object -First 1
} catch {
    Write-Host "ERROR: Java not found in PATH!" -ForegroundColor Red
    Write-Host "Please ensure JAVA_HOME is set and java is in your PATH." -ForegroundColor Red
    exit 1
}

Write-Host ""

# Function to run Maven command with error handling
function Invoke-Maven {
    param(
        [string]$Command,
        [string]$Description
    )
    
    Write-Host "[$Description] Running: mvnw.cmd $Command" -ForegroundColor Yellow
    if ($Verbose) {
        Write-Host "Command: .\mvnw.cmd $Command" -ForegroundColor Gray
    }
    
    $result = & .\mvnw.cmd $Command.Split(' ')
    $exitCode = $LASTEXITCODE
    
    if ($exitCode -eq 0) {
        Write-Host "[$Description] Completed successfully" -ForegroundColor Green
    } else {
        Write-Host "[$Description] Failed with exit code: $exitCode" -ForegroundColor Red
        throw "Maven command failed: $Command"
    }
    
    return $result
}

try {
    # Step 1: Clean
    Write-Host "[1/6] Cleaning previous build..." -ForegroundColor Blue
    Invoke-Maven "clean" "Clean"
    
    # Step 2: Run tests (unless skipped)
    if (-not $SkipTests) {
        Write-Host "[2/6] Running tests..." -ForegroundColor Blue
        Invoke-Maven "test" "Tests"
    } else {
        Write-Host "[2/6] Skipping tests (--SkipTests flag used)" -ForegroundColor Yellow
    }
    
    # Step 3: Build package
    Write-Host "[3/6] Building package..." -ForegroundColor Blue
    if ($SkipTests) {
        Invoke-Maven "package -DskipTests" "Package Build"
    } else {
        Invoke-Maven "package" "Package Build"
    }
    
    # Step 4: Generate code coverage (unless skipped)
    if (-not $SkipCoverage -and -not $SkipTests) {
        Write-Host "[4/6] Generating code coverage report..." -ForegroundColor Blue
        try {
            Invoke-Maven "jacoco:report" "Code Coverage"
        } catch {
            Write-Host "WARNING: Code coverage report generation failed!" -ForegroundColor Yellow
        }
    } else {
        Write-Host "[4/6] Skipping code coverage (--SkipCoverage flag used or tests skipped)" -ForegroundColor Yellow
    }
    
    # Step 5: Run integration tests
    Write-Host "[5/6] Running integration tests..." -ForegroundColor Blue
    try {
        Invoke-Maven "verify" "Integration Tests"
    } catch {
        Write-Host "WARNING: Integration tests failed!" -ForegroundColor Yellow
    }
    
    # Step 6: Build completed
    Write-Host "[6/6] Build completed successfully!" -ForegroundColor Green
    Write-Host ""
    
    # Show build artifacts
    Write-Host "Build artifacts:" -ForegroundColor Cyan
    $jarFiles = Get-ChildItem "target\*.jar" -ErrorAction SilentlyContinue
    if ($jarFiles) {
        Write-Host "- JAR files:" -ForegroundColor White
        $jarFiles | ForEach-Object { Write-Host "  $($_.Name)" -ForegroundColor Gray }
    }
    
    if (Test-Path "target\surefire-reports") {
        Write-Host "- Test results: target\surefire-reports\" -ForegroundColor Gray
    }
    
    if (Test-Path "target\site\jacoco") {
        Write-Host "- Coverage report: target\site\jacoco\index.html" -ForegroundColor Gray
    }
    
    Write-Host ""
    Write-Host "Build completed successfully!" -ForegroundColor Green
    
} catch {
    Write-Host ""
    Write-Host "ERROR: Build failed!" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown") 