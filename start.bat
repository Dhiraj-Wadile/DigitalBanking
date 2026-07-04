@echo off
title Digital Banking - Starting...
echo ============================================
echo   Digital Banking Platform
echo ============================================
echo.

cd /d "%~dp0"

echo [1/2] Building backend...
call backend\mvnw.cmd clean package -DskipTests -q
if %ERRORLEVEL% neq 0 (
    echo BUILD FAILED! Check errors above.
    pause
    exit /b 1
)
echo      Build SUCCESS
echo.

echo [2/2] Starting server on port 8080...
echo.
echo ============================================
echo   Login Credentials:
echo     admin    / admin123   (Super Admin)
echo     employee / employee123 (Employee)
echo     customer / customer123 (Customer)
echo     priya    / customer123 (Customer)
echo ============================================
echo.
echo   H2 Console: http://localhost:8080/h2-console
echo   Press Ctrl+C to stop the server
echo ============================================
echo.

java -jar backend\target\digital-banking-api-1.0.0.jar --spring.profiles.active=local
