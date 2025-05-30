# Setup Guide - Snake Game Java Edition

This guide will help you set up and run the Snake Game on your system.

## Prerequisites

### System Requirements
- **Operating System**: Windows, macOS, or Linux
- **Java**: Version 8 or higher
- **Memory**: At least 512 MB RAM
- **Storage**: 50 MB free space

### Installing Java

#### Windows
1. Download Java from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
2. Run the installer and follow the setup wizard
3. Verify installation by opening Command Prompt and typing: `java -version`

#### macOS
1. Install via Homebrew: `brew install openjdk`
2. Or download from [Oracle](https://www.oracle.com/java/technologies/downloads/)
3. Verify installation in Terminal: `java -version`

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

## Installation Methods

### Method 1: Using Build Scripts (Recommended)

#### For macOS/Linux:
```bash
# Clone the repository
git clone https://github.com/yourusername/snake-game-java.git
cd snake-game-java

# Make build script executable
chmod +x build.sh

# Compile and run
./build.sh run
```

#### For Windows:
```cmd
REM Clone the repository
git clone https://github.com/yourusername/snake-game-java.git
cd snake-game-java

REM Compile and run
build.bat run
```

### Method 2: Manual Compilation

1. **Clone the repository**:
```bash
git clone https://github.com/yourusername/snake-game-java.git
cd snake-game-java
```

2. **Compile the source code**:
```bash
# Create output directory
mkdir out

# Compile with dependencies
javac -cp "lib/*" -d out src/*.java
```

3. **Run the game**:
```bash
# macOS/Linux
java -cp "lib/*:out" SnakeGame

# Windows
java -cp "lib/*;out" SnakeGame
```

### Method 3: Using an IDE

#### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" and choose the project folder
3. Add the FlatLaf library:
   - Go to File → Project Structure → Libraries
   - Click "+" and add `lib/flatlaf-3.4.jar`
4. Run the `SnakeGame.java` file

#### Eclipse
1. Open Eclipse
2. Import the project: File → Import → Existing Projects into Workspace
3. Add the FlatLaf library:
   - Right-click project → Properties → Java Build Path → Libraries
   - Add External JARs and select `lib/flatlaf-3.4.jar`
4. Run the `SnakeGame.java` file

#### VS Code
1. Install the Java Extension Pack
2. Open the project folder
3. The extension should automatically detect the Java project
4. Add the library to `.vscode/settings.json`:
```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar"
    ]
}
```
5. Run the `SnakeGame.java` file

## Troubleshooting

### Common Issues

#### "Java not found" Error
- **Solution**: Make sure Java is installed and added to your system PATH
- **Check**: Run `java -version` in terminal/command prompt

#### "Class not found" Error
- **Solution**: Ensure you're running from the correct directory with proper classpath
- **Check**: Verify the `out` directory contains compiled `.class` files

#### "Cannot find FlatLaf" Error
- **Solution**: Make sure `lib/flatlaf-3.4.jar` exists and is included in classpath
- **Check**: Verify the `lib` directory contains the JAR file

#### Game Window Doesn't Appear
- **Solution**: Check if your system supports Java Swing GUI
- **Try**: Run with `-Djava.awt.headless=false` flag

#### Performance Issues
- **Solution**: Increase Java heap size: `-Xmx1g`
- **Try**: Close other applications to free up memory

### Platform-Specific Issues

#### macOS
- If you get security warnings, go to System Preferences → Security & Privacy and allow the application
- For M1 Macs, ensure you're using ARM-compatible Java

#### Windows
- If antivirus software blocks execution, add the project folder to exclusions
- Ensure Windows Defender isn't quarantining the JAR files

#### Linux
- Install required GUI libraries: `sudo apt install openjfx`
- For Wayland users, try: `export GDK_BACKEND=x11`

## Configuration

### Game Settings
Settings are automatically saved to `game_settings.properties`. You can manually edit:
- `selectedTheme`: Choose your preferred theme
- `randomTheme`: Enable/disable random theme selection
- `soundEnabled`: Toggle sound effects

### Performance Tuning
For better performance, you can:
- Increase heap size: `java -Xmx1g -cp "lib/*:out" SnakeGame`
- Enable hardware acceleration: `-Dsun.java2d.opengl=true`
- Reduce animation quality in settings

## Development Setup

If you want to contribute or modify the code:

1. **Fork the repository** on GitHub
2. **Clone your fork** locally
3. **Set up your IDE** with the project
4. **Install development tools**:
   - Git for version control
   - Java formatter/linter
   - Code analysis tools

5. **Run tests** (if available):
```bash
./build.sh test
```

6. **Make your changes** and test thoroughly
7. **Submit a pull request** with your improvements

## Getting Help

If you encounter issues:
1. Check this setup guide
2. Look at the [troubleshooting section](#troubleshooting)
3. Search existing [GitHub issues](https://github.com/yourusername/snake-game-java/issues)
4. Create a new issue with:
   - Your operating system
   - Java version
   - Error messages
   - Steps to reproduce

## Next Steps

Once you have the game running:
- Read the [README.md](../README.md) for gameplay instructions
- Check out [CONTRIBUTING.md](../CONTRIBUTING.md) if you want to contribute
- Explore the different game modes and settings

Enjoy playing Snake! 🐍