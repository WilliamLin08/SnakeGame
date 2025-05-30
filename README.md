# Snake Game - Java Edition

A modern, feature-rich Snake game implementation in Java with multiple game modes, AI opponents, and customizable themes.

## Features

### Game Modes
- **Classic Mode**: Traditional single-player Snake game
- **Duo Mode**: Two-player local multiplayer
- **Timed Mode**: Race against the clock
- **VS AI Mode**: Challenge an intelligent AI opponent

### Key Features
- 🎨 **Multiple Themes**: Choose from various visual themes or enable random themes
- 🔊 **Sound Effects**: Immersive audio experience with toggle option
- 🤖 **Smart AI**: Advanced AI opponent with strategic pathfinding
- 🎮 **Smooth Animations**: Fluid gameplay with custom animation system
- ⚙️ **Customizable Settings**: Adjust themes, sound, and game preferences
- 🗺️ **Multiple Maps**: Various map layouts with obstacles and challenges
- 📊 **Score Tracking**: Keep track of your best performances

## Screenshots

*Add screenshots of your game here*

## Getting Started

### Prerequisites
- Java 8 or higher
- Java Development Kit (JDK)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/snake-game-java.git
cd snake-game-java
```

2. Compile the project:
```bash
javac -cp "lib/*:src" src/*.java
```

3. Run the game:
```bash
java -cp "lib/*:src" SnakeGame
```

### Alternative: Using an IDE

1. Open the project in your favorite Java IDE (IntelliJ IDEA, Eclipse, etc.)
2. Add the `lib/flatlaf-3.4.jar` to your project dependencies
3. Run the `SnakeGame.java` file

## How to Play

### Controls
- **Arrow Keys**: Move the snake (Up, Down, Left, Right)
- **WASD**: Alternative movement controls
- **Space**: Pause/Resume game
- **Esc**: Return to main menu

### Duo Mode Controls
- **Player 1**: Arrow keys
- **Player 2**: WASD keys

### Game Rules
- Eat food to grow your snake and increase your score
- Avoid hitting walls, obstacles, or yourself
- In duo mode, avoid colliding with the other player
- In timed mode, achieve the highest score before time runs out
- In VS AI mode, try to outlast the computer opponent

## Project Structure

```
src/
├── SnakeGame.java          # Main game class with menu system and inner classes:
│                           #   - GamePanel (classic mode)
│                           #   - MapSelectPanel (map selection UI)
├── DuoGamePanel.java       # Two-player game mode
├── TimedGamePanel.java     # Timed game mode
├── VSAIGamePanel.java      # VS AI game mode
├── AISnake.java            # AI opponent logic
├── AnimationManager.java   # Animation system
├── SoundManager.java       # Audio management
├── Theme.java              # Theme definitions
├── Settings.java           # Game settings management
├── SettingsPanel.java      # Settings UI
├── LoadingPanel.java       # Loading screen
└── FoodType.java           # Food type definitions
```

## Dependencies

- **FlatLaf 3.4**: Modern look and feel for Java Swing applications
  - Provides a clean, modern UI appearance
  - Located in `lib/flatlaf-3.4.jar`

## Configuration

Game settings are automatically saved to `game_settings.properties`. You can modify:
- Selected theme
- Random theme setting
- Sound enabled/disabled
- Other game preferences

## Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java naming conventions
- Add comments for complex logic
- Test your changes thoroughly
- Update documentation as needed

## Future Enhancements

- [ ] Online multiplayer support
- [ ] More AI difficulty levels
- [ ] Additional themes and customization options
- [ ] High score leaderboards
- [ ] Power-ups and special items
- [ ] Mobile version
- [ ] Tournament mode

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- FlatLaf library for the modern UI components
- Java Swing for the GUI framework
- Contributors and testers

## Contact

If you have any questions, suggestions, or issues, please feel free to:
- Open an issue on GitHub
- Contact the maintainers

---

**Enjoy playing Snake! 🐍**