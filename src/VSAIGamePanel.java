import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import javax.swing.*;

/**
 * A competitive Snake game mode where a human player faces off against an AI opponent.
 * 
 * This game mode features:
 * - Player vs AI competition on the same board
 * - Three AI difficulty levels (Easy, Medium, Hard)
 * - Separate scoring for player and AI
 * - Dynamic speed increases as the game progresses
 * - Collision detection between snakes
 * - Pause/resume functionality
 * - Level progression system
 * 
 * The AI uses sophisticated pathfinding and strategy algorithms that vary
 * by difficulty level, providing an engaging challenge for players of all
 * skill levels. The game ends when either snake collides with walls,
 * obstacles, or the other snake.
 * 
 * Controls:
 * - Arrow keys or WASD for player movement
 * - SPACE to pause/resume
 * - ESC to exit to main menu
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class VSAIGamePanel extends JPanel implements ActionListener {
    private static final int CELL_SIZE = 20;
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    private static final int INITIAL_DELAY = 150;
    private static final int MIN_DELAY = 50;
    
    /** Current visual theme for colors and styling */
    private Theme currentTheme;
    
    private ArrayList<Point> playerSnake;
    private ArrayList<Point> aiSnake;
    private Point food;
    private int playerDirection;
    private boolean running;
    private boolean paused;
    private boolean gameStarted;
    private int playerScore;
    private int aiScore;
    private Timer timer;
    private Random random;
    private Font gameFont;
    private Font instructionFont;
    private int currentDelay;
    private int level;
    private JButton instructionButton;
    private JDialog instructionDialog;
    private JButton exitButton;
    private SnakeGame parent;
    private AISnake ai;
    private int aiDirection;
    private int aiDifficulty;
    private FoodType currentFoodType;
    private boolean playerDead;
    private boolean aiDead;
    
    public VSAIGamePanel(SnakeGame parent) {
        this.parent = parent;
        this.currentTheme = Settings.getInstance().getCurrentTheme();
        setPreferredSize(new Dimension(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE));
        setBackground(currentTheme.backgroundColor);
        setFocusable(true);
        setLayout(null);
        
        gameFont = new Font("SansSerif", Font.BOLD, 14);
        instructionFont = new Font("SansSerif", Font.PLAIN, 12);
        random = new Random();
        
        // 说明按钮
        instructionButton = new JButton("?");
        instructionButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        instructionButton.setBounds(WIDTH * CELL_SIZE - 70, HEIGHT * CELL_SIZE - 50, 60, 40);
        instructionButton.setFocusable(false);
        instructionButton.setOpaque(false);
        instructionButton.setContentAreaFilled(false);
        instructionButton.setBorderPainted(false);
        instructionButton.setBorder(null);
        instructionButton.setBackground(new Color(0, 0, 0, 0));
        instructionButton.setForeground(new Color(200, 200, 200, 180));
        instructionButton.addActionListener(e -> showInstructionDialog());
        add(instructionButton);
        
        // 退出按钮
        exitButton = new JButton("exit");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitButton.setBounds(WIDTH * CELL_SIZE - 140, HEIGHT * CELL_SIZE - 45, 80, 35);
        exitButton.setFocusable(false);
        exitButton.setOpaque(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setBorderPainted(false);
        exitButton.setBorder(null);
        exitButton.setBackground(new Color(0, 0, 0, 0));
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> exitToMapSelect());
        add(exitButton);
        
        createInstructionDialog();
        addKeyListener(new GameKeyAdapter());
        SwingUtilities.invokeLater(this::showDifficultyDialog);
        requestFocusInWindow();
    }
    
    private void showDifficultyDialog() {
        showThemedDifficultyDialog();
    }
    
    private void showThemedDifficultyDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "AI Difficulty Selection", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        
        // Main panel with theme colors
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBackground(currentTheme.backgroundColor);
         mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
         
         // Title panel
         JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         titlePanel.setBackground(currentTheme.backgroundColor);
         
         JLabel titleLabel = new JLabel("Please select AI difficulty");
         titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
         titleLabel.setForeground(currentTheme.textColor);
         titlePanel.add(titleLabel);
         
         // Button panel
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
         buttonPanel.setBackground(currentTheme.backgroundColor);
        
        // Difficulty buttons
        String[] difficulties = {"Easy", "Medium", "Hard"};
        Color[] buttonColors = {
            new Color(76, 175, 80),  // Green for Easy
            new Color(255, 193, 7),  // Yellow for Medium  
            new Color(244, 67, 54)   // Red for Hard
        };
        
        for (int i = 0; i < difficulties.length; i++) {
            final int difficulty = i + 1;
            JButton button = new JButton(difficulties[i]);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setPreferredSize(new Dimension(120, 50));
            button.setBackground(buttonColors[i]);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Hover effect
            Color originalColor = buttonColors[i];
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(originalColor.brighter());
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(originalColor);
                }
            });
            
            button.addActionListener(e -> {
                aiDifficulty = difficulty;
                dialog.dispose();
                initGame();
            });
            
            buttonPanel.add(button);
        }
        
        // Description panel
         JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         descPanel.setBackground(currentTheme.backgroundColor);
         
         JLabel descLabel = new JLabel("Choose your challenge level!");
         descLabel.setFont(new Font("Arial", Font.ITALIC, 14));
         descLabel.setForeground(currentTheme.textColor.brighter());
         descPanel.add(descLabel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(descPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Handle window closing - default to Easy
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                aiDifficulty = 1; // Default to Easy
                dialog.dispose();
                initGame();
            }
        });
        
        dialog.setVisible(true);
    }
    
    private void initGame() {
        playerSnake = new ArrayList<>();
        aiSnake = new ArrayList<>();
        
        // 根据难度初始化AI - 在resetSnakes之前初始化
        ai = new AISnake(WIDTH, HEIGHT, new ArrayList<>(), aiDifficulty);
        
        resetSnakes();
        playerScore = 0;
        aiScore = 0;
        playerDead = false;
        aiDead = false;
        running = true;
        paused = false;
        gameStarted = true;
        level = 1;
        currentDelay = INITIAL_DELAY;
        currentFoodType = FoodType.NORMAL;
        
        if (timer != null) timer.stop();
        timer = new Timer(currentDelay, this);
        timer.start();
    }
    
    private void resetSnakes() {
        // 重置玩家蛇
        playerSnake.clear();
        int startX = WIDTH / 2;
        int startY = HEIGHT / 2;
        playerSnake.add(new Point(startX, startY));
        playerSnake.add(new Point(startX, startY + 1));
        playerSnake.add(new Point(startX, startY + 2));
        playerDirection = KeyEvent.VK_UP;
        
        // 重置AI蛇 - 让AISnake类自己处理位置
        if (ai != null) {
            ai.resetSnake();
            aiSnake = new ArrayList<>(ai.getSnake());
        }
        aiDirection = KeyEvent.VK_DOWN;
        
        generateFood();
    }
    
    private void generateFood() {
        int x, y;
        do {
            x = random.nextInt(WIDTH);
            y = random.nextInt(HEIGHT);
        } while (isOnSnake(x, y));
        food = new Point(x, y);
        // Generate random food type for enhanced gameplay
        currentFoodType = FoodType.getRandomFoodType();
        ai.setFood(food);
    }
    
    private boolean isOnSnake(int x, int y) {
        for (Point p : playerSnake) {
            if (p.x == x && p.y == y) return true;
        }
        for (Point p : aiSnake) {
            if (p.x == x && p.y == y) return true;
        }
        return false;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
        }
        repaint();
    }
    
    private void move() {
        // 移动玩家蛇
        Point playerHead = playerSnake.get(0);
        Point newPlayerHead = new Point(playerHead);
        switch (playerDirection) {
            case KeyEvent.VK_UP: newPlayerHead.y--; break;
            case KeyEvent.VK_DOWN: newPlayerHead.y++; break;
            case KeyEvent.VK_LEFT: newPlayerHead.x--; break;
            case KeyEvent.VK_RIGHT: newPlayerHead.x++; break;
        }
        
        // 移动AI蛇
        Point aiHead = aiSnake.get(0);
        Point newAiHead = new Point(aiHead);
        
        // 更新AI蛇的状态
        ai.setFood(food);
        ai.setPlayerSnake(playerSnake);
        ai.move();
        newAiHead = ai.getSnake().get(0);
        
        // 更新AI蛇的位置 - 在碰撞检查之前更新
        aiSnake = new ArrayList<>(ai.getSnake());
        
        // 检查碰撞
        boolean playerCollided = checkPlayerCollision(newPlayerHead);
        boolean aiCollided = checkAICollision(newAiHead);
        
        if (playerCollided || aiCollided) {
            playerDead = playerCollided;
            aiDead = aiCollided;
            gameOver();
            return;
        }
        
        // 更新玩家蛇的位置
        playerSnake.add(0, newPlayerHead);
        
        // 检查是否吃到食物
        if (newPlayerHead.x == food.x && newPlayerHead.y == food.y) {
            playerScore += 10;
            generateFood();
            increaseDifficulty();
        } else {
            playerSnake.remove(playerSnake.size() - 1);
        }
        
        if (newAiHead.x == food.x && newAiHead.y == food.y) {
            aiScore += 10;
            generateFood();
        }
    }
    
    private boolean checkCollision(Point point) {
        // 检查是否撞墙
        if (point.x < 0 || point.x >= WIDTH || point.y < 0 || point.y >= HEIGHT) {
            return true;
        }
        
        return false;
    }
    
    private boolean checkPlayerCollision(Point newPlayerHead) {
        // Check wall collision
        if (newPlayerHead.x < 0 || newPlayerHead.x >= WIDTH || newPlayerHead.y < 0 || newPlayerHead.y >= HEIGHT) {
            return true;
        }
        
        // Check self collision (skip head at index 0)
        for (int i = 1; i < playerSnake.size(); i++) {
            if (playerSnake.get(i).equals(newPlayerHead)) {
                return true;
            }
        }
        
        // Check collision with AI snake
        for (Point p : aiSnake) {
            if (p.equals(newPlayerHead)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean checkAICollision(Point newAiHead) {
        // Check wall collision
        if (newAiHead.x < 0 || newAiHead.x >= WIDTH || newAiHead.y < 0 || newAiHead.y >= HEIGHT) {
            return true;
        }
        
        // Check self collision (AI snake collision with itself)
        for (int i = 1; i < aiSnake.size(); i++) {
            if (aiSnake.get(i).equals(newAiHead)) {
                return true;
            }
        }
        
        // Check collision with player snake
        for (Point p : playerSnake) {
            if (p.equals(newAiHead)) {
                return true;
            }
        }
        
        return false;
    }
    
    private void increaseDifficulty() {
        int newLevel = ((playerScore + aiScore) / 50) + 1;
        if (newLevel > level) {
            level = newLevel;
            currentDelay = Math.max(MIN_DELAY, INITIAL_DELAY - ((level - 1) * 10));
            timer.setDelay(currentDelay);
        }
    }
    
    private void gameOver() {
        running = false;
        timer.stop();
        
        showThemedGameOverDialog();
    }
    
    private void showThemedGameOverDialog() {
        JDialog gameOverDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Game Over", true);
        gameOverDialog.setSize(400, 300);
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setResizable(false);
        
        // Create main panel with theme colors
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(currentTheme.backgroundColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.wallColor, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(currentTheme.backgroundColor);
        JLabel titleLabel = new JLabel("🎮 Game Over", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(currentTheme.textColor);
        titlePanel.add(titleLabel);
        
        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(currentTheme.backgroundColor);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Player score
        JLabel playerScoreLabel = new JLabel("🎯 Player Score: " + playerScore, JLabel.CENTER);
        playerScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerScoreLabel.setForeground(currentTheme.snakeColor);
        playerScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // AI score
        JLabel aiScoreLabel = new JLabel("🤖 AI Score: " + aiScore, JLabel.CENTER);
        aiScoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        aiScoreLabel.setForeground(currentTheme.foodColor);
        aiScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Winner announcement - prioritize survival over score
        String winnerText;
        Color winnerColor;
        
        if (playerDead && aiDead) {
            // Both died simultaneously, compare scores
            if (playerScore > aiScore) {
                winnerText = "🏆 You Win!";
                winnerColor = currentTheme.snakeColor.brighter();
            } else if (aiScore > playerScore) {
                winnerText = "🤖 AI Wins!";
                winnerColor = currentTheme.foodColor.brighter();
            } else {
                winnerText = "🤝 It's a Tie!";
                winnerColor = currentTheme.textColor;
            }
        } else if (playerDead) {
            // Only player died, AI wins
            winnerText = "🤖 AI Wins!";
            winnerColor = currentTheme.foodColor.brighter();
        } else if (aiDead) {
            // Only AI died, player wins
            winnerText = "🏆 You Win!";
            winnerColor = currentTheme.snakeColor.brighter();
        } else {
            // Fallback to score comparison (shouldn't happen in normal gameplay)
            if (playerScore > aiScore) {
                winnerText = "🏆 You Win!";
                winnerColor = currentTheme.snakeColor.brighter();
            } else if (aiScore > playerScore) {
                winnerText = "🤖 AI Wins!";
                winnerColor = currentTheme.foodColor.brighter();
            } else {
                winnerText = "🤝 It's a Tie!";
                winnerColor = currentTheme.textColor;
            }
        }
        
        JLabel winnerLabel = new JLabel(winnerText, JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        winnerLabel.setForeground(winnerColor);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scorePanel.add(playerScoreLabel);
        scorePanel.add(Box.createVerticalStrut(10));
        scorePanel.add(aiScoreLabel);
        scorePanel.add(Box.createVerticalStrut(15));
        scorePanel.add(winnerLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(currentTheme.backgroundColor);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(currentTheme.wallColor);
        okButton.setForeground(currentTheme.textColor);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.gridColor, 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> {
            gameOverDialog.dispose();
            exitToMapSelect();
        });
        
        // Add hover effect
        okButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                okButton.setBackground(currentTheme.wallColor.brighter());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                okButton.setBackground(currentTheme.wallColor);
            }
        });
        
        buttonPanel.add(okButton);
        
        // Assemble dialog
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scorePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        gameOverDialog.add(mainPanel);
        gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        gameOverDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                exitToMapSelect();
            }
        });
        
        gameOverDialog.setVisible(true);
    }
    
    private void exitToMapSelect() {
        parent.showMapSelectPanel();
    }
    
    private void createInstructionDialog() {
        instructionDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "🤖 VS AI Instructions", true);
        instructionDialog.setSize(500, 400);
        instructionDialog.setLocationRelativeTo(this);
        instructionDialog.setResizable(false);
        instructionDialog.getContentPane().setBackground(new Color(15, 23, 42));
        
        // Create main panel with modern styling
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(15, 23, 42),
                    0, getHeight(), new Color(30, 41, 59)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle border
                g2d.setColor(new Color(71, 85, 105, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        panel.setOpaque(false);
        
        // Create title label
        JLabel titleLabel = new JLabel("🤖 VS AI Battle Guide");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create styled text area
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBackground(new Color(30, 41, 59));
        textArea.setForeground(new Color(226, 232, 240));
        textArea.setCaretColor(new Color(59, 130, 246));
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String instructions =
            "🎯 GOAL\n" +
            "Outsmart the AI snake and eat food faster to achieve higher scores!\n\n" +
            "🎮 CONTROLS\n" +
            "• Arrow Keys / WASD → Move your snake\n" +
            "• Spacebar / P → Pause/Resume\n" +
            "• R → Restart battle\n" +
            "• Shift + / → Show this help\n\n" +
            "⚔️ BATTLE RULES\n" +
            "• Each food eaten = 10 points\n" +
            "• Avoid walls, AI snake, and yourself\n" +
            "• Collision ends your game immediately\n" +
            "• Difficulty increases as scores rise\n\n" +
            "🧠 AI TACTICS\n" +
            "• Study AI movement patterns\n" +
            "• Block AI access to food\n" +
            "• Use strategic positioning\n" +
            "• Exploit AI predictable behavior";
        
        textArea.setText(instructions);
        
        // Create custom scroll pane with dark theme
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(new Color(30, 41, 59));
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(new Color(30, 41, 59));
        scrollPane.getVerticalScrollBar().setBackground(new Color(51, 65, 85));
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 116, 139);
                this.trackColor = new Color(51, 65, 85);
            }
        });
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Create styled close button
        JButton closeButton = new JButton("⚔️ Ready to Battle!") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button background with gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(239, 68, 68),
                    0, getHeight(), new Color(220, 38, 38)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Subtle border
                g2d.setColor(new Color(185, 28, 28));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setOpaque(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(150, 35));
        closeButton.addActionListener(e -> instructionDialog.setVisible(false));
        
        // Add hover effect
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                closeButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        instructionDialog.add(panel);
        instructionDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    
    private void showInstructionDialog() {
        boolean wasPaused = paused;
        if (running && !paused) {
            paused = true;
        }
        instructionDialog.setVisible(true);
        if (running && !wasPaused) {
            paused = false;
        }
        this.requestFocusInWindow();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawGrid(g2d);
        drawWalls(g2d);
        
        if (running) {
            drawGame(g2d);
            
            if (!gameStarted) {
                drawWelcomeScreen(g2d);
            } else if (paused) {
                drawPausedScreen(g2d);
            }
        } else {
            drawGameOver(g2d);
        }
    }
    
    private void drawGrid(Graphics2D g) {
        g.setColor(currentTheme.gridColor);
        for (int i = 0; i < WIDTH; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT * CELL_SIZE);
        }
        for (int i = 0; i < HEIGHT; i++) {
            g.drawLine(0, i * CELL_SIZE, WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
    }
    
    private void drawWalls(Graphics2D g) {
        g.setColor(currentTheme.wallColor);
        g.fillRect(0, 0, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, HEIGHT * CELL_SIZE - CELL_SIZE / 4, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
        g.fillRect(WIDTH * CELL_SIZE - CELL_SIZE / 4, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
    }
    
    private void drawGame(Graphics2D g) {
        // Draw player snake
        for (int i = 0; i < playerSnake.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.brighter());
            } else {
                g.setColor(currentTheme.snakeColor);
            }
            Point p = playerSnake.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1,
                    CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }
        
        // Draw AI snake
        for (int i = 0; i < aiSnake.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.darker().brighter());
            } else {
                g.setColor(currentTheme.snakeColor.darker());
            }
            Point p = aiSnake.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1,
                    CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }
        
        // Draw food based on type
        int foodX = food.x * CELL_SIZE + 2;
        int foodY = food.y * CELL_SIZE + 2;
        int foodSize = CELL_SIZE - 4;
        
        switch (currentFoodType) {
            case NORMAL:
                g.setColor(currentTheme.foodColor);
                g.fillOval(foodX, foodY, foodSize, foodSize);
                break;
            case BONUS:
                g.setColor(Color.YELLOW);
                // Draw star shape
                int[] xPoints = {foodX + foodSize/2, foodX + foodSize*3/5, foodX + foodSize, foodX + foodSize*2/3, foodX + foodSize*4/5, foodX + foodSize/2, foodX + foodSize/5, foodX + foodSize/3, foodX, foodX + foodSize*2/5};
                int[] yPoints = {foodY, foodY + foodSize*2/5, foodY + foodSize*2/5, foodY + foodSize*3/5, foodY + foodSize, foodY + foodSize*4/5, foodY + foodSize, foodY + foodSize*3/5, foodY + foodSize*2/5, foodY + foodSize*2/5};
                g.fillPolygon(xPoints, yPoints, 10);
                break;
            case SPEED:
                g.setColor(Color.CYAN);
                // Draw lightning bolt
                int[] xLightning = {foodX + foodSize/3, foodX + foodSize*2/3, foodX + foodSize/2, foodX + foodSize*2/3, foodX + foodSize/3, foodX + foodSize/2};
                int[] yLightning = {foodY, foodY + foodSize/3, foodY + foodSize/2, foodY + foodSize, foodY + foodSize*2/3, foodY + foodSize/2};
                g.fillPolygon(xLightning, yLightning, 6);
                break;
            case SLOW:
                g.setColor(Color.ORANGE);
                // Draw spiral
                for (int i = 0; i < 3; i++) {
                    g.fillOval(foodX + i*2, foodY + i*2, foodSize - i*4, foodSize - i*4);
                }
                break;
            case MEGA:
                g.setColor(Color.MAGENTA);
                // Draw hexagon
                int[] xHex = {foodX + foodSize/4, foodX + foodSize*3/4, foodX + foodSize, foodX + foodSize*3/4, foodX + foodSize/4, foodX};
                int[] yHex = {foodY, foodY, foodY + foodSize/2, foodY + foodSize, foodY + foodSize, foodY + foodSize/2};
                g.fillPolygon(xHex, yHex, 6);
                break;
        }
        
        // Draw score
        g.setColor(currentTheme.textColor);
        g.setFont(gameFont);
        g.drawString("Player Score: " + playerScore, 10, 20);
        g.drawString("AI Score: " + aiScore, 10, 40);
        g.drawString("Level: " + level, 10, 60);
        
        // Control instructions (moved to top)
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(currentTheme.textColor.darker());
        g.drawString("Controls: Arrow keys/WASD: Move | Space/P: Pause | R: Restart", 10, 80);
        
        // Food type legend
        g.drawString("Current Food: " + currentFoodType.name + " (" + currentFoodType.points + " pts)", 10, 95);
    }
    
    private void drawInstructions(Graphics2D g) {
        g.setColor(currentTheme.textColor);
        g.setFont(instructionFont);
        int y = getHeight() - 70;
        g.drawString("Controls:", 10, y);
        g.drawString("Arrow keys/WASD: Move snake", 10, y + 15);
        g.drawString("Space/P: Pause game", 10, y + 30);
        g.drawString("R: Restart game", 10, y + 45);
    }
    
    private void drawWelcomeScreen(Graphics2D g) {
        // Draw a darker semi-transparent background for better contrast
        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Draw title with bright yellow color for better visibility
        g.setColor(new Color(255, 255, 100));
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "VS AI Mode";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 60);
        
        // Draw start instruction with bright white
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        String startMsg = "Press Space or R to start game";
        int startMsgWidth = g.getFontMetrics().stringWidth(startMsg);
        g.drawString(startMsg, (getWidth() - startMsgWidth) / 2, getHeight() / 2);
        
        // Draw help instruction with light gray
        g.setColor(new Color(220, 220, 220));
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String helpMsg = "Click \"?\" button in bottom right or press Shift+/ for instructions";
        int helpMsgWidth = g.getFontMetrics().stringWidth(helpMsg);
        g.drawString(helpMsg, (getWidth() - helpMsgWidth) / 2, getHeight() / 2 + 40);
    }
    
    private void drawPausedScreen(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "Paused";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String subMsg = "Press Space to continue";
        int subMsgWidth = g.getFontMetrics().stringWidth(subMsg);
        g.drawString(subMsg, (getWidth() - subMsgWidth) / 2, getHeight() / 2 + 30);
    }
    
    private void drawGameOver(Graphics2D g) {
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        String msg = "Game Over";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 60);
        
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        String playerScoreMsg = "Player Score: " + playerScore;
        int playerScoreWidth = g.getFontMetrics().stringWidth(playerScoreMsg);
        g.drawString(playerScoreMsg, (getWidth() - playerScoreWidth) / 2, getHeight() / 2 - 25);
        
        String aiScoreMsg = "AI Score: " + aiScore;
        int aiScoreWidth = g.getFontMetrics().stringWidth(aiScoreMsg);
        g.drawString(aiScoreMsg, (getWidth() - aiScoreWidth) / 2, getHeight() / 2 + 5);
        
        String levelMsg = "Final Level: " + level;
        int levelWidth = g.getFontMetrics().stringWidth(levelMsg);
        g.drawString(levelMsg, (getWidth() - levelWidth) / 2, getHeight() / 2 + 35);
        
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        String restartMsg = "Press R to restart game";
        int restartWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (getWidth() - restartWidth) / 2, getHeight() / 2 + 70);
    }
    
    private boolean isSafeDirectionChange(int newDirection) {
        if (playerSnake.size() < 2) return true; // Safe if snake is too short to collide with itself
        
        Point head = playerSnake.get(0);
        Point neck = playerSnake.get(1);
        Point newHead = new Point(head);
        
        // Calculate where the head would be with the new direction
        switch (newDirection) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        
        // Check if the new head position would be the same as the neck (immediate collision)
        return !newHead.equals(neck);
    }
    
    /**
     * Updates the current theme and refreshes the panel appearance.
     * Called when theme settings are changed.
     */
    public void updateTheme() {
        currentTheme = Settings.getInstance().getCurrentTheme();
        setBackground(currentTheme.backgroundColor);
        repaint();
    }
    
    private class GameKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            if (key == KeyEvent.VK_SLASH && e.isShiftDown()) {
                showInstructionDialog();
                return;
            }
            
            if (running) {
                if (!gameStarted) {
                    if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_R) {
                        gameStarted = true;
                        paused = false;
                    }
                    return;
                }
                
                if (key == KeyEvent.VK_P || key == KeyEvent.VK_SPACE) {
                    paused = !paused;
                    return;
                }
                
                if (paused) return;
                
                // Store the current direction to prevent multiple changes in one frame
                int newPlayerDirection = playerDirection;
                
                if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && playerDirection != KeyEvent.VK_DOWN) {
                    newPlayerDirection = KeyEvent.VK_UP;
                } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && playerDirection != KeyEvent.VK_UP) {
                    newPlayerDirection = KeyEvent.VK_DOWN;
                } else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && playerDirection != KeyEvent.VK_RIGHT) {
                    newPlayerDirection = KeyEvent.VK_LEFT;
                } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && playerDirection != KeyEvent.VK_LEFT) {
                    newPlayerDirection = KeyEvent.VK_RIGHT;
                }
                
                // Only update direction if it's different and safe
                if (newPlayerDirection != playerDirection && isSafeDirectionChange(newPlayerDirection)) {
                    playerDirection = newPlayerDirection;
                }
            } else if (key == KeyEvent.VK_R) {
                initGame();
            } else if (key == KeyEvent.VK_ESCAPE) {
                parent.showMapSelectPanel();
            }
        }
    }
}