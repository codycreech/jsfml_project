@echo off
setlocal
cd /d "%~dp0JSFML Project"
if exist build\classes rmdir /s /q build\classes
mkdir build\classes
if exist build\test-classes rmdir /s /q build\test-classes
mkdir build\test-classes
javac --release 8 -cp jsfml.jar -d build\classes src\jsfmlproject\*.java
if errorlevel 1 exit /b 1
javac --release 8 -cp build\classes -d build\test-classes test\jsfmlproject\*.java
if errorlevel 1 exit /b 1
java -cp "build\classes;build\test-classes" jsfmlproject.GameModelTest
if errorlevel 1 exit /b 1
(echo Manifest-Version: 1.0& echo Main-Class: jsfmlproject.Main& echo Class-Path: ../jsfml.jar& echo.) > build\MANIFEST.MF
jar cfm build\orbit-run.jar build\MANIFEST.MF -C build\classes .
if errorlevel 1 exit /b 1
echo Built JSFML Project\build\orbit-run.jar
