# Contributing to Snake Game Java Edition

Thank you for your interest in contributing to this project! We welcome contributions from everyone.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- A clear description of the problem
- Steps to reproduce the issue
- Expected vs actual behavior
- Your system information (OS, Java version)
- Screenshots if applicable

### Suggesting Features

We welcome feature suggestions! Please:
- Check if the feature has already been suggested
- Provide a clear description of the feature
- Explain why it would be useful
- Consider the implementation complexity

### Code Contributions

#### Getting Started

1. Fork the repository
2. Clone your fork locally
3. Create a new branch for your feature/fix
4. Make your changes
5. Test thoroughly
6. Submit a pull request

#### Development Setup

1. Ensure you have Java 8+ installed
2. Import the project into your IDE
3. Add the FlatLaf library to your classpath
4. Run the main class to test

#### Code Style Guidelines

- **Naming Conventions**: Follow Java naming conventions
  - Classes: PascalCase (e.g., `GamePanel`)
  - Methods/Variables: camelCase (e.g., `updateScore`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_SCORE`)

- **Documentation**: 
  - Add Javadoc comments for public methods and classes
  - Use inline comments for complex logic
  - Keep comments up-to-date with code changes

- **Code Organization**:
  - Keep methods focused and concise
  - Use meaningful variable and method names
  - Organize imports properly
  - Remove unused imports and variables

- **Error Handling**:
  - Handle exceptions appropriately
  - Provide meaningful error messages
  - Don't suppress exceptions without good reason

#### Testing

- Test your changes thoroughly before submitting
- Verify all game modes work correctly
- Check that settings are saved/loaded properly
- Test on different screen sizes if UI changes are made
- Ensure no performance regressions

#### Pull Request Guidelines

- **Title**: Use a clear, descriptive title
- **Description**: Explain what your PR does and why
- **Changes**: List the main changes made
- **Testing**: Describe how you tested your changes
- **Screenshots**: Include screenshots for UI changes

#### Example PR Description

```
## Description
Added new rainbow theme with animated colors

## Changes
- Added RainbowTheme class
- Updated Theme enum
- Modified theme selection UI
- Added color animation logic

## Testing
- Tested theme switching
- Verified animations work smoothly
- Checked performance impact
- Tested on macOS and Windows

## Screenshots
[Include screenshots here]
```

### Areas for Contribution

We especially welcome contributions in these areas:

#### New Features
- Additional game modes
- New themes and visual effects
- Power-ups and special items
- Improved AI algorithms
- Sound effects and music
- Accessibility features

#### Bug Fixes
- Performance optimizations
- UI/UX improvements
- Cross-platform compatibility
- Memory leak fixes

#### Documentation
- Code documentation improvements
- Tutorial creation
- API documentation
- Translation to other languages

### Code Review Process

1. All submissions require review
2. Maintainers will review your PR
3. Address any requested changes
4. Once approved, your PR will be merged

### Community Guidelines

- Be respectful and constructive
- Help others learn and grow
- Follow the code of conduct
- Ask questions if you're unsure

### Getting Help

If you need help:
- Check existing issues and documentation
- Ask questions in issue comments
- Reach out to maintainers

### Recognition

Contributors will be:
- Listed in the project contributors
- Credited in release notes
- Thanked in the community

Thank you for contributing to Snake Game Java Edition! 🐍