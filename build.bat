@echo off
REM Snake Game Java Edition - Build Script for Windows
REM This script compiles and runs the Snake game

echo 🐍 Snake Game Java Edition - Build Script
echo ==========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java is not installed or not in PATH
    echo Please install Java 8 or higher and try again.
    pause
    exit /b 1
)

javac -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: Java compiler (javac) is not installed or not in PATH
    echo Please install JDK and try again.
    pause
    exit /b 1
)

REM Display Java version
echo ☕ Java version:
java -version
echo.

REM Create output directory if it doesn't exist
if not exist "out" (
    mkdir out
    echo 📁 Created output directory: out\
)

REM Compile the project
echo 🔨 Compiling Java source files...
javac -cp "lib\*" -d out src\*.java
if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)
echo ✅ Compilation successful!

REM Copy resources if any
if exist "src\game_settings.properties" (
    copy "src\game_settings.properties" "out\" >nul
    echo 📋 Copied game settings
)

REM Check if user wants to run the game
if "%1"=="run" goto :run
if "%1"=="-r" goto :run
if "%1"=="--run" goto :run

echo.
echo 🎮 To run the game, use one of these commands:
echo    build.bat run
echo    java -cp "lib\*;out" SnakeGame
echo.
echo 📝 Build completed successfully!
pause
exit /b 0

:run
echo.
echo 🚀 Starting Snake Game...
echo Press Ctrl+C to stop the game
echo.
java -cp "lib\*;out" SnakeGame
pause