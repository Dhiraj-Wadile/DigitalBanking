@echo off
echo ========================================
echo   Digital Banking Platform
echo ========================================
echo.
echo Starting Frontend Server on port 4200...
echo.
cd /d "%~dp0frontend"
npx ng serve --host 0.0.0.0 --port 4200 --open
