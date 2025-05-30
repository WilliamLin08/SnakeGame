#!/bin/bash

# Snake Game Java Edition - Build Script
# This script compiles and runs the Snake game

set -e  # Exit on any error

echo "🐍 Snake Game Java Edition - Build Script"
echo "=========================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Error: Java is not installed or not in PATH"
    echo "Please install Java 8 or higher and try again."
    exit 1
fi

if ! command -v javac &> /dev/null; then
    echo "❌ Error: Java compiler (javac) is not installed or not in PATH"
    echo "Please install JDK and try again."
    exit 1
fi

# Display Java version
echo "☕ Java version:"
java -version
echo ""

# Create output directory if it doesn't exist
if [ ! -d "out" ]; then
    mkdir out
    echo "📁 Created output directory: out/"
fi

# Compile the project
echo "🔨 Compiling Java source files..."
if javac -cp "lib/*" -d out src/*.java; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed!"
    exit 1
fi

# Copy resources if any
if [ -f "src/game_settings.properties" ]; then
    cp src/game_settings.properties out/
    echo "📋 Copied game settings"
fi

# Check if user wants to run the game
if [ "$1" = "run" ] || [ "$1" = "-r" ] || [ "$1" = "--run" ]; then
    echo ""
    echo "🚀 Starting Snake Game..."
    echo "Press Ctrl+C to stop the game"
    echo ""
    java -cp "lib/*:out" SnakeGame
else
    echo ""
    echo "🎮 To run the game, use one of these commands:"
    echo "   ./build.sh run"
    echo "   java -cp \"lib/*:out\" SnakeGame"
    echo ""
    echo "📝 Build completed successfully!"
fi