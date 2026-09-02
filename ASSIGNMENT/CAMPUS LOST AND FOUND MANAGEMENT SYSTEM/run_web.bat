@echo off
title Campus Lost-and-Found Management System - Web Server
echo ============================================================
echo  Starting Campus Lost-and-Found Web Application on Localhost
echo ============================================================

if not exist node_modules (
    echo.
    echo Installing node dependencies...
    call npm install
)

echo.
echo Launching Server on http://localhost:5000...
echo.

start "" "http://localhost:5000"

node server.js
pause
