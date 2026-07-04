@echo off
echo ========================================
echo   Digital Banking Platform - Full Setup
echo ========================================
echo.

echo [1/4] Setting up Backend...
cd backend
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo Backend setup failed!
    exit /b 1
)
cd ..

echo.
echo [2/4] Setting up Frontend...
cd frontend
call npm install
if %errorlevel% neq 0 (
    echo Frontend setup failed!
    exit /b 1
)
cd ..

echo.
echo [3/4] Starting Infrastructure (PostgreSQL, Redis, RabbitMQ)...
docker-compose up -d postgres redis rabbitmq
echo Waiting for PostgreSQL to be ready...
timeout /t 10 /nobreak >nul

echo.
echo [4/4] Running Database Migrations...
cd backend
call mvn flyway:migrate
if %errorlevel% neq 0 (
    echo Migration failed!
    exit /b 1
)
cd ..

echo.
echo ========================================
echo   Setup Complete!
echo ========================================
echo.
echo To start the application:
echo   1. start-backend.bat   - Start Spring Boot API
echo   2. start-frontend.bat  - Start Angular UI
echo.
echo Or use start-all.bat to start everything.
echo ========================================
pause
