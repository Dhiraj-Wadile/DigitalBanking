@echo off
echo Starting all services with Docker Compose...
echo.
docker-compose up -d
echo.
echo All services are running!
echo.
echo Service URLs:
echo   Backend API  : http://localhost:8080
echo   Frontend     : http://localhost:4200
echo   PostgreSQL   : localhost:5432
echo   Redis        : localhost:6379
echo   RabbitMQ UI  : http://localhost:15672
echo.
pause
