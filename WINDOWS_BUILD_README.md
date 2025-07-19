# Windows Build Guide

## Problem Description

The original Jenkins pipeline was designed for Unix/Linux systems and used `sh` (shell) commands. When running on Windows Jenkins agents, these commands fail because Windows doesn't have a native `sh` command.

## Solutions

### 1. Updated Jenkinsfile (Cross-Platform)

The main `Jenkinsfile` has been updated to be cross-platform compatible. It now uses:
- `sh` commands for Unix/Linux systems
- `bat` commands for Windows systems
- `isUnix()` function to detect the operating system

### 2. Windows-Specific Jenkinsfile

A Windows-specific version is available as `Jenkinsfile.windows` that uses only `bat` commands.

### 3. Alternative Build Scripts

#### Batch Script (`build-windows.bat`)
Simple batch script for Windows:
```cmd
build-windows.bat
```

#### PowerShell Script (`build-windows.ps1`)
More advanced PowerShell script with better error handling:
```powershell
# Run with execution policy bypass
powershell -ExecutionPolicy Bypass -File build-windows.ps1

# Or with parameters
powershell -ExecutionPolicy Bypass -File build-windows.ps1 -SkipTests -Verbose
```

## Running Tests on Windows

### Using Maven Wrapper
```cmd
# Run all tests
mvnw.cmd test

# Run specific test class
mvnw.cmd test -Dtest=BlocServiceTest

# Run test suite
mvnw.cmd test -Dtest=TestSuite

# Run with coverage
mvnw.cmd jacoco:report
```

### Using Maven (if installed)
```cmd
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ChambreServiceTest

# Run with coverage
mvn jacoco:report
```

## Jenkins Configuration

### For Windows Jenkins Agents

1. **Use the updated Jenkinsfile**: The main `Jenkinsfile` now supports both Unix and Windows
2. **Or use the Windows-specific version**: Rename `Jenkinsfile.windows` to `Jenkinsfile`
3. **Configure Jenkins agent**: Ensure the Windows agent has:
   - Java JDK installed
   - Maven installed (or use Maven wrapper)
   - Docker installed (if using Docker stages)

### Jenkins Agent Requirements

```groovy
// In Jenkins agent configuration
agent {
    label 'windows'  // Specify Windows agent
}
```

## Troubleshooting

### Common Issues

1. **"Cannot run program 'sh'"**
   - **Solution**: Use the updated Jenkinsfile or Windows-specific version

2. **Maven not found**
   - **Solution**: Use `mvnw.cmd` instead of `mvn`
   - **Alternative**: Install Maven and add to PATH

3. **Java not found**
   - **Solution**: Set JAVA_HOME environment variable
   - **Verify**: Run `java -version` in command prompt

4. **PowerShell execution policy**
   - **Solution**: Run with `-ExecutionPolicy Bypass`
   - **Alternative**: Use the batch script instead

### Environment Variables

Ensure these are set on your Windows system:
```cmd
JAVA_HOME=C:\Program Files\Java\jdk-17
PATH=%JAVA_HOME%\bin;%PATH%
```

### Testing the Setup

1. **Test Java installation**:
   ```cmd
   java -version
   ```

2. **Test Maven wrapper**:
   ```cmd
   mvnw.cmd --version
   ```

3. **Test basic build**:
   ```cmd
   mvnw.cmd clean compile
   ```

4. **Test unit tests**:
   ```cmd
   mvnw.cmd test
   ```

## Build Artifacts

After successful build, you'll find:
- **JAR file**: `target\foyer-*.jar`
- **Test results**: `target\surefire-reports\`
- **Coverage report**: `target\site\jacoco\index.html`

## Continuous Integration

### GitHub Actions (Alternative to Jenkins)

Create `.github/workflows/build.yml`:
```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: windows-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvnw.cmd clean package
    
    - name: Run tests
      run: mvnw.cmd test
    
    - name: Generate coverage report
      run: mvnw.cmd jacoco:report
```

## Summary

The Windows build issue has been resolved by:
1. ✅ Making the Jenkinsfile cross-platform compatible
2. ✅ Providing Windows-specific alternatives
3. ✅ Creating standalone build scripts for Windows
4. ✅ Adding comprehensive documentation

Choose the solution that best fits your environment and requirements. 