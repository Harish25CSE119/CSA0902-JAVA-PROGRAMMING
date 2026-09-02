@echo off
title Campus Lost-and-Found Management System
echo ============================================================
echo  Compiling Campus Lost-and-Found Management System (v2.0)...
echo ============================================================

if not exist bin mkdir bin

javac -cp "lib/mysql-connector-j-8.3.0.jar;lib/flatlaf-3.7.2.jar" -d bin src/database/*.java src/model/*.java src/dao/*.java src/gui/*.java src/Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation Failed! Please check Java error messages above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo Compilation Successful! Launching Desktop Application v2.0...
echo ============================================================

java -cp "bin;lib/mysql-connector-j-8.3.0.jar;lib/flatlaf-3.7.2.jar" Main
pause
