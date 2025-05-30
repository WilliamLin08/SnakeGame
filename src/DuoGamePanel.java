import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Multiplayer game panel for two-player Snake game mode.
 * 
 * This class manages a competitive Snake game where two players control
 * separate snakes on the same game board. Players compete to eat food
 * and avoid collisions with walls, obstacles, and each other.
 * 
 * Controls:
 * - Player 1: WASD keys for movement
 * - Player 2: Arrow keys for movement
 * - R key: Restart game
 * - ESC key: Return to map selection
 * 
 * Game Features:
 * - Separate scoring for each player
 * - Collision detection between players
 * - Shared food source
 * - Real-time competitive gameplay
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class DuoGamePanel extends JPanel implements ActionListener {
    /** Reference to the main game window for navigation */
    private SnakeGame parent;
    
    /** Game timer that controls the movement speed of both snakes */
    private javax.swing.Timer timer;
    
    /** Body segments for Player 1's snake (head at index 0) */
    private ArrayList<Point> snake1;
    
    /** Body segments for Player 2's snake (head at index 0) */
    private ArrayList<Point> snake2;
    
    /** Current movement direction for Player 1 (using KeyEvent constants) */
    private int direction1;
    
    /** Current movement direction for Player 2 (using KeyEvent constants) */
    private int direction2;
    
    /** Whether Player 1's snake is still alive and moving */
    private boolean running1;
    
    /** Whether Player 2's snake is still alive and moving */
    private boolean running2;
    
    /** Current score for Player 1 */
    private int score1;
    
    /** Current score for Player 2 */
    private int score2;
    
    /** Current position of the food on the game board */
    private Point food;
    
    /** Current type of food with special effects */
    private FoodType currentFoodType;
    
    /** Random number generator for food placement */
    private Random random;
    
    /** Size of each grid cell in pixels */
    private static final int CELL_SIZE = 20;
    
    /** Game board dimensions in grid cells */
    private static final int WIDTH = 25, HEIGHT = 25;
    
    /** Initial delay between snake movements in milliseconds */
    private static final int INITIAL_DELAY = 120;
    
    /** Font used for rendering game text and scores */
    private Font gameFont = new Font("SansSerif", Font.BOLD, 16);
    
    /** Flag to prevent multiple game over dialogs from appearing */
    private boolean gameOverDialogShown = false;
    
    /** Current visual theme for colors and styling */
    private Theme currentTheme;

    /**
     * Creates a new multiplayer game panel.
     * 
     * Initializes the game board, sets up the UI components, and prepares
     * both snakes for competitive gameplay. The panel is configured to
     * receive keyboard input for both players.
     * 
     * @param parent Reference to the main SnakeGame window for navigation
     */
    public DuoGamePanel(SnakeGame parent) {
        this.parent = parent;
        this.currentTheme = Settings.getInstance().getCurrentTheme();
        setPreferredSize(new Dimension(WIDTH * CELL_SIZE, HEIGHT * CELL_SIZE));
        setBackground(currentTheme.backgroundColor);
        setFocusable(true);
        setLayout(null);
        random = new Random();
        initGame();
        addKeyListener(new DuoKeyAdapter());
        requestFocusInWindow();
    }

    /**
     * Initializes a new multiplayer game session.
     * 
     * Sets up both snakes in opposite corners of the game board to ensure
     * fair starting positions. Resets all game state variables including
     * scores, directions, and running status.
     */
    private void initGame() {
        snake1 = new ArrayList<>();
        snake2 = new ArrayList<>();
        
        // Player 1 initial position: Left side, moving right
        // Snake positioned horizontally with head at (3,5)
        snake1.add(new Point(3, 5));  // Head
        snake1.add(new Point(2, 5));  // Body segment 1
        snake1.add(new Point(1, 5));  // Tail
        
        // Player 2 initial position: Right side, moving left
        // Snake positioned horizontally with head at (21,19)
        snake2.add(new Point(21, 19)); // Head
        snake2.add(new Point(22, 19)); // Body segment 1
        snake2.add(new Point(23, 19)); // Tail
        
        // Set initial movement directions
        direction1 = KeyEvent.VK_RIGHT; // Player 1 moves right
        direction2 = KeyEvent.VK_LEFT;  // Player 2 moves left
        
        // Initialize game state
        running1 = true;
        running2 = true;
        score1 = 0;
        score2 = 0;
        gameOverDialogShown = false;
        currentFoodType = FoodType.NORMAL;
        generateFood();
        if (timer != null) timer.stop();
        timer = new javax.swing.Timer(INITIAL_DELAY, this);
        timer.start();
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
    }

    private boolean isOnSnake(int x, int y) {
        for (Point p : snake1) if (p.x == x && p.y == y) return true;
        for (Point p : snake2) if (p.x == x && p.y == y) return true;
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running1) moveSnake(snake1, 1);
        if (running2) moveSnake(snake2, 2);
        repaint();
        if (!running1 && !running2 && !gameOverDialogShown) {
            gameOverDialogShown = true;
            SwingUtilities.invokeLater(this::showGameOverDialog);
        }
    }

    private void moveSnake(ArrayList<Point> snake, int player) {
        int direction = (player == 1) ? direction1 : direction2;
        Point head = snake.get(0);
        Point newHead = new Point(head);
        switch (direction) {
            case KeyEvent.VK_UP: newHead.y--; break;
            case KeyEvent.VK_DOWN: newHead.y++; break;
            case KeyEvent.VK_LEFT: newHead.x--; break;
            case KeyEvent.VK_RIGHT: newHead.x++; break;
        }
        // Hit wall or hit self
        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            if (player == 1) running1 = false; else running2 = false;
            return;
        }
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(i).equals(newHead)) {
                if (player == 1) running1 = false; else running2 = false;
                return;
            }
        }
        // Hit other snake
        ArrayList<Point> other = (player == 1) ? snake2 : snake1;
        for (Point p : other) {
            if (p.equals(newHead)) {
                if (player == 1) running1 = false; else running2 = false;
                return;
            }
        }
        snake.add(0, newHead);
        if (newHead.equals(food)) {
            if (player == 1) score1 += 10; else score2 += 10;
            generateFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw background elements
        drawGrid(g2d);
        drawWalls(g2d);
        
        if (running1 || running2) {
            drawGame(g2d);
        } else {
            drawGameOver(g2d);
        }
    }
    
    /**
     * Draws the game grid lines.
     * Creates a visual grid to help players see cell boundaries.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGrid(Graphics2D g) {
        g.setColor(currentTheme.gridColor);
        // Draw vertical lines
        for (int i = 0; i <= WIDTH; i++) {
            g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, HEIGHT * CELL_SIZE);
        }
        // Draw horizontal lines
        for (int i = 0; i <= HEIGHT; i++) {
            g.drawLine(0, i * CELL_SIZE, WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
    }
    
    /**
     * Draws the border walls around the game area.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawWalls(Graphics2D g) {
        g.setColor(currentTheme.wallColor);
        // Draw border walls
        g.fillRect(0, 0, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, HEIGHT * CELL_SIZE - CELL_SIZE / 4, WIDTH * CELL_SIZE, CELL_SIZE / 4);
        g.fillRect(0, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
        g.fillRect(WIDTH * CELL_SIZE - CELL_SIZE / 4, 0, CELL_SIZE / 4, HEIGHT * CELL_SIZE);
    }
    
    /**
     * Draws the main game elements including snakes, food, and UI.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGame(Graphics2D g) {
        // Draw Player 1 snake
        for (int i = 0; i < snake1.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.brighter().brighter()); // Head is brighter
            } else {
                g.setColor(currentTheme.snakeColor.brighter()); // Body
            }
            Point p = snake1.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }
        
        // Draw Player 2 snake
        for (int i = 0; i < snake2.size(); i++) {
            if (i == 0) {
                g.setColor(currentTheme.snakeColor.darker().brighter()); // Head is brighter
            } else {
                g.setColor(currentTheme.snakeColor.darker()); // Body
            }
            Point p = snake2.get(i);
            g.fillRoundRect(p.x * CELL_SIZE + 1, p.y * CELL_SIZE + 1, CELL_SIZE - 2, CELL_SIZE - 2, 8, 8);
        }
        
        // Draw food with type-specific appearance
        if (food != null && currentFoodType != null) {
            Color foodColor = currentFoodType.getThemeColor(currentTheme);
            g.setColor(foodColor);
            
            int baseX = food.x * CELL_SIZE;
            int baseY = food.y * CELL_SIZE;
            
            switch (currentFoodType) {
                case NORMAL:
                    g.fillOval(baseX + 2, baseY + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                    break;
                case BONUS:
                    // Star shape for bonus food
                    int[] xPoints = {baseX + CELL_SIZE/2, baseX + CELL_SIZE/2 + 3, baseX + CELL_SIZE - 2, baseX + CELL_SIZE/2 + 5, baseX + CELL_SIZE/2 + 8, baseX + CELL_SIZE/2, baseX + CELL_SIZE/2 - 8, baseX + CELL_SIZE/2 - 5, baseX + 2};
                    int[] yPoints = {baseY + 2, baseY + CELL_SIZE/2 - 2, baseY + CELL_SIZE/2, baseY + CELL_SIZE/2 + 5, baseY + CELL_SIZE - 2, baseY + CELL_SIZE/2 + 3, baseY + CELL_SIZE - 2, baseY + CELL_SIZE/2 + 5, baseY + CELL_SIZE/2};
                    g.fillPolygon(xPoints, yPoints, 9);
                    break;
                case SPEED:
                    // Lightning bolt shape
                    g.fillRect(baseX + 6, baseY + 2, 4, 8);
                    g.fillRect(baseX + 10, baseY + 6, 6, 4);
                    g.fillRect(baseX + 6, baseY + 10, 4, 8);
                    break;
                case SLOW:
                    // Snail shell spiral
                    g.fillOval(baseX + 4, baseY + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                    g.setColor(foodColor.darker());
                    g.fillOval(baseX + 6, baseY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                    break;
                case MEGA:
                    // Hexagon shape
                    int[] hexX = {baseX + CELL_SIZE/4, baseX + 3*CELL_SIZE/4, baseX + CELL_SIZE - 2, baseX + 3*CELL_SIZE/4, baseX + CELL_SIZE/4, baseX + 2};
                    int[] hexY = {baseY + 2, baseY + 2, baseY + CELL_SIZE/2, baseY + CELL_SIZE - 2, baseY + CELL_SIZE - 2, baseY + CELL_SIZE/2};
                    g.fillPolygon(hexX, hexY, 6);
                    break;
                case SHRINK:
                    // Triangle shape
                    int[] triX = {baseX + CELL_SIZE/2, baseX + CELL_SIZE - 2, baseX + 2};
                    int[] triY = {baseY + 2, baseY + CELL_SIZE - 2, baseY + CELL_SIZE - 2};
                    g.fillPolygon(triX, triY, 3);
                    break;
            }
        }
        
        // Draw UI elements
        drawUI(g);
    }
    
    /**
     * Draws the user interface elements including scores and instructions.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawUI(Graphics2D g) {
        // Score display for Player 1 with transparency
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g.setColor(currentTheme.snakeColor);
        g.fillRoundRect(10, 10, 140, 32, 16, 16);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(currentTheme.textColor);
        g.drawString("Player 1: " + score1, 22, 32);
        
        // Score display for Player 2 with transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g.setColor(currentTheme.snakeColor.darker());
        g.fillRoundRect(170, 10, 140, 32, 16, 16);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(currentTheme.textColor);
        g.drawString("Player 2: " + score2, 182, 32);
        
        // Control instructions - positioned at bottom left corner
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Controls: WASD: Player 1 | Arrow keys: Player 2 | R: Restart | ESC: Exit", 10, getHeight() - 30);
        
        // Food types legend - positioned above controls in bottom left
        if (currentFoodType != null) {
            g.setColor(currentTheme.textColor.darker());
            g.drawString("Current Food: " + currentFoodType.name + " (" + currentFoodType.points + " pts)", 10, getHeight() - 50);
        }
    }
    
    /**
     * Draws the game over screen with final scores and winner announcement.
     * 
     * @param g The Graphics2D context for drawing
     */
    private void drawGameOver(Graphics2D g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // Game Over title
        g.setColor(currentTheme.textColor);
        g.setFont(new Font("SansSerif", Font.BOLD, 36));
        String msg = "Game Over";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2 - 60);
        
        // Final scores
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        String scoreMsg = "Player 1: " + score1 + "  |  Player 2: " + score2;
        int scoreMsgWidth = g.getFontMetrics().stringWidth(scoreMsg);
        g.drawString(scoreMsg, (getWidth() - scoreMsgWidth) / 2, getHeight() / 2 - 20);
        
        // Winner announcement
        String result;
        if (score1 > score2) result = "Player 1 Wins!";
        else if (score2 > score1) result = "Player 2 Wins!";
        else result = "It's a Draw!";
        int resultWidth = g.getFontMetrics().stringWidth(result);
        g.drawString(result, (getWidth() - resultWidth) / 2, getHeight() / 2 + 20);
        
        // Restart instructions
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String restartMsg = "Press R to restart or ESC to return to menu";
        int restartMsgWidth = g.getFontMetrics().stringWidth(restartMsg);
        g.drawString(restartMsg, (getWidth() - restartMsgWidth) / 2, getHeight() / 2 + 60);
    }

    private void showGameOverDialog() {
        JDialog gameOverDialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Multiplayer Mode - Game Over", true);
        gameOverDialog.setSize(450, 350);
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setResizable(false);
        
        // Create main panel with theme colors
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(currentTheme.backgroundColor);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.wallColor, 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(currentTheme.backgroundColor);
        JLabel titleLabel = new JLabel("🎮 Game Over", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(currentTheme.textColor);
        titlePanel.add(titleLabel);
        
        // Score panel
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.setBackground(currentTheme.backgroundColor);
        scorePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));
        
        // Player 1 score
        JLabel player1ScoreLabel = new JLabel("🎯 Player 1 Score: " + score1, JLabel.CENTER);
        player1ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player1ScoreLabel.setForeground(currentTheme.snakeColor);
        player1ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Player 2 score
        JLabel player2ScoreLabel = new JLabel("🎯 Player 2 Score: " + score2, JLabel.CENTER);
        player2ScoreLabel.setFont(new Font("Arial", Font.BOLD, 18));
        player2ScoreLabel.setForeground(currentTheme.foodColor);
        player2ScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Winner announcement with enhanced styling
        String winnerText;
        String winnerEmoji;
        Color winnerColor;
        if (score1 > score2) {
            winnerText = "Player 1 Wins!";
            winnerEmoji = "🏆";
            winnerColor = currentTheme.snakeColor.brighter();
        } else if (score2 > score1) {
            winnerText = "Player 2 Wins!";
            winnerEmoji = "🏆";
            winnerColor = currentTheme.foodColor.brighter();
        } else {
            winnerText = "It's a Draw!";
            winnerEmoji = "🤝";
            winnerColor = currentTheme.textColor.brighter();
        }
        
        JLabel winnerLabel = new JLabel(winnerEmoji + " " + winnerText, JLabel.CENTER);
        winnerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        winnerLabel.setForeground(winnerColor);
        winnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add competitive spirit message
        String spiritText;
        if (Math.abs(score1 - score2) <= 10) {
            spiritText = "🔥 What a close match!";
        } else if (Math.max(score1, score2) >= 100) {
            spiritText = "⭐ Outstanding gameplay!";
        } else {
            spiritText = "🎊 Great game everyone!";
        }
        
        JLabel spiritLabel = new JLabel(spiritText, JLabel.CENTER);
        spiritLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        spiritLabel.setForeground(currentTheme.gridColor.brighter());
        spiritLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        scorePanel.add(player1ScoreLabel);
        scorePanel.add(Box.createVerticalStrut(12));
        scorePanel.add(player2ScoreLabel);
        scorePanel.add(Box.createVerticalStrut(18));
        scorePanel.add(winnerLabel);
        scorePanel.add(Box.createVerticalStrut(12));
        scorePanel.add(spiritLabel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(currentTheme.backgroundColor);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBackground(currentTheme.wallColor);
        okButton.setForeground(currentTheme.textColor);
        okButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(currentTheme.gridColor, 1),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> gameOverDialog.dispose());
        
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
        gameOverDialog.setVisible(true);
    }

    private boolean isSafeDirectionChange1(int newDirection) {
        if (snake1.size() < 2) return true; // Safe if snake is too short to collide with itself
        
        Point head = snake1.get(0);
        Point neck = snake1.get(1);
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
     * Checks if changing to a new direction would cause Player 2's snake to immediately
     * collide with its own neck (second segment).
     * 
     * This prevents the snake from reversing into itself, which would cause
     * an immediate game over.
     * 
     * @param newDirection The proposed new direction for Player 2
     * @return true if the direction change is safe, false if it would cause collision
     */
    private boolean isSafeDirectionChange2(int newDirection) {
        if (snake2.size() < 2) return true; // Safe if snake is too short to collide with itself
        
        Point head = snake2.get(0);
        Point neck = snake2.get(1);
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

    private class DuoKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            // 玩家1: WASD
            if (running1) {
                // Store the current direction to prevent multiple changes in one frame
                int newDirection1 = direction1;
                
                if ((key == KeyEvent.VK_W) && direction1 != KeyEvent.VK_DOWN) newDirection1 = KeyEvent.VK_UP;
                else if ((key == KeyEvent.VK_S) && direction1 != KeyEvent.VK_UP) newDirection1 = KeyEvent.VK_DOWN;
                else if ((key == KeyEvent.VK_A) && direction1 != KeyEvent.VK_RIGHT) newDirection1 = KeyEvent.VK_LEFT;
                else if ((key == KeyEvent.VK_D) && direction1 != KeyEvent.VK_LEFT) newDirection1 = KeyEvent.VK_RIGHT;
                
                // Only update direction if it's different and safe
                if (newDirection1 != direction1 && isSafeDirectionChange1(newDirection1)) {
                    direction1 = newDirection1;
                }
            }
            // 玩家2: 方向键
            if (running2) {
                // Store the current direction to prevent multiple changes in one frame
                int newDirection2 = direction2;
                
                if ((key == KeyEvent.VK_UP) && direction2 != KeyEvent.VK_DOWN) newDirection2 = KeyEvent.VK_UP;
                else if ((key == KeyEvent.VK_DOWN) && direction2 != KeyEvent.VK_UP) newDirection2 = KeyEvent.VK_DOWN;
                else if ((key == KeyEvent.VK_LEFT) && direction2 != KeyEvent.VK_RIGHT) newDirection2 = KeyEvent.VK_LEFT;
                else if ((key == KeyEvent.VK_RIGHT) && direction2 != KeyEvent.VK_LEFT) newDirection2 = KeyEvent.VK_RIGHT;
                
                // Only update direction if it's different and safe
                if (newDirection2 != direction2 && isSafeDirectionChange2(newDirection2)) {
                    direction2 = newDirection2;
                }
            }
            // R键重新开始
            if (key == KeyEvent.VK_R && (!running1 || !running2)) {
                initGame();
            }
            // ESC返回地图选择
            if (key == KeyEvent.VK_ESCAPE) {
                parent.showMapSelectPanel();
            }
        }
    }
}