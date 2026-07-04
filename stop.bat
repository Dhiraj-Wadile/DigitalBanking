@echo off
title Digital Banking - Stopping...
echo Stopping Digital Banking server...

taskkill /F /IM java.exe /FI "WINDOWTITLE eq Digital Banking*" >nul 2>&1
taskkill /F /IM java.exe >nul 2>&1

echo Server stopped.
timeout /t 2 >nul
