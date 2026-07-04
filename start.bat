@echo off
title Digital Banking - Backend + Frontend

echo ============================================
echo   Digital Banking Platform - Starting All
echo ============================================

:: Kill anything on port 8080
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080"') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":4200"') do taskkill /PID %%a /F >nul 2>&1
timeout /t 2 /nobreak >nul

echo [1/2] Starting Backend on port 8080...
start "Backend" cmd /k "cd /d "%~dp0backend" && "C:\tools\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run -Dspring-boot.run.profiles=local"

echo [2/2] Waiting for backend to compile and start...
timeout /t 90 /nobreak >nul

echo Starting Frontend on port 4200...
start "Frontend" cmd /k "cd /d "%~dp0frontend" && npx ng serve --host 0.0.0.0 --port 4200"

echo.
echo ============================================
echo   Backend:  http://localhost:8080/api
echo   Frontend: http://localhost:4200
echo   H2 Console: http://localhost:8080/h2-console
echo ============================================
echo.
echo TEST CREDENTIALS:
echo   Customer: customer / customer123
echo   Admin:    admin / admin123
echo   Employee: employee / employee123
echo ============================================
pause
