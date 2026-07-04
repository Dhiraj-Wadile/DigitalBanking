@echo off
echo Starting Infrastructure Services...
echo.
echo Starting PostgreSQL, Redis, and RabbitMQ...
docker-compose up -d postgres redis rabbitmq
echo.
echo Waiting for services to be ready...
timeout /t 10 /nobreak >nul
echo.
echo Infrastructure is running!
echo.
echo Services:
echo   PostgreSQL : localhost:5432
echo   Redis      : localhost:6379
echo   RabbitMQ   : localhost:5672 (Management: localhost:15672)
echo.
pause
