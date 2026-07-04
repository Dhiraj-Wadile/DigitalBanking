@echo off
echo Stopping All Services...
echo.

echo Stopping Docker containers...
docker-compose down

echo.
echo Stopping Java processes (Backend)...
taskkill /F /IM java.exe 2>nul

echo.
echo Stopping Angular processes...
taskkill /F /IM node.exe /FI "WINDOWTITLE eq Frontend*" 2>nul

echo.
echo All services stopped.
pause
