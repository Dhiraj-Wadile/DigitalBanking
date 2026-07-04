@echo off
echo ========================================
echo   Starting All Services
echo ========================================
echo.
echo [1/4] Starting Infrastructure...
docker-compose up -d postgres redis rabbitmq
echo Waiting for infrastructure...
timeout /t 10 /nobreak >nul
echo.
echo [2/4] Starting Backend API Server...
start "Backend" cmd /c "cd backend && mvn spring-boot:run"
echo Waiting for backend to start...
timeout /t 15 /nobreak >nul
echo.
echo [3/4] Starting Frontend Server...
start "Frontend" cmd /c "cd frontend && npm start"
echo.
echo [4/4] All services started!
echo.
echo ========================================
echo   Service URLs:
echo ========================================
echo   Backend API  : http://localhost:8080
echo   Frontend     : http://localhost:4200
echo   PostgreSQL   : localhost:5432
echo   Redis        : localhost:6379
echo   RabbitMQ UI  : http://localhost:15672
echo ========================================
pause
