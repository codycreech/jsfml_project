@echo off
setlocal
cd /d "%~dp0"
if not exist "JSFML Project\build\orbit-run.jar" call build.bat
if errorlevel 1 exit /b 1
java -jar "JSFML Project\build\orbit-run.jar" %*
