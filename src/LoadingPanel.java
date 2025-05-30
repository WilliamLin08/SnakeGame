import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A loading panel that displays an animated loading screen when switching between game panels.
 * 
 * This panel provides visual feedback to users during panel transitions, featuring:
 * - Animated loading spinner
 * - Progress bar with smooth animation
 * - Customizable loading message
 * - Modern, sleek design with gradient background
 * - Automatic completion after specified duration
 * 
 * The loading panel helps create a smooth user experience by providing visual
 * feedback during potentially slow operations like panel initialization.
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class LoadingPanel extends JPanel {
    /** Reference to the main game window */
    private SnakeGame parent;
    
    /** Timer for animating the loading spinner */
    private Timer animationTimer;
    
    /** Timer for controlling the loading duration */
    private Timer loadingTimer;
    
    /** Current rotation angle for the spinner animation */
    private int spinnerAngle = 0;
    
    /** Current progress value (0-100) */
    private int progress = 0;
    
    /** Loading message to display */
    private String loadingMessage;
    
    /** Callback to execute when loading is complete */
    private Runnable onComplete;
    
    /** Font for the loading message */
    private Font messageFont;
    
    /** Font for the progress percentage */
    private Font progressFont;
    
    /**
     * Creates a new loading panel with the specified parameters.
     * 
     * @param parent The main game window
     * @param message The loading message to display
     * @param duration Loading duration in milliseconds
     * @param onComplete Callback to execute when loading is complete
     */
    public LoadingPanel(SnakeGame parent, String message, int duration, Runnable onComplete) {
        this.parent = parent;
        this.loadingMessage = message;
        this.onComplete = onComplete;
        
        // Initialize fonts
        messageFont = new Font("SansSerif", Font.BOLD, 18);
        progressFont = new Font("SansSerif", Font.PLAIN, 14);
        
        // Set panel properties with modern dark theme
        setPreferredSize(new Dimension(600, 400));
        setBackground(new Color(15, 23, 42));  // Match main theme
        
        // Initialize and start timers
        initializeTimers(duration);
        startLoading();
    }
    
    /**
     * Initializes the animation and loading timers.
     * 
     * @param duration Total loading duration in milliseconds
     */
    private void initializeTimers(int duration) {
        // Animation timer for spinner rotation (60 FPS)
        animationTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spinnerAngle = (spinnerAngle + 6) % 360;
                repaint();
            }
        });
        
        // Progress timer for smooth progress bar animation
        Timer progressTimer = new Timer(duration / 100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (progress < 100) {
                    progress++;
                    repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        
        // Loading completion timer
        loadingTimer = new Timer(duration, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopLoading();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        
        // Set timers to fire only once for loading completion
        loadingTimer.setRepeats(false);
        
        // Start progress timer
        progressTimer.start();
    }
    
    /**
     * Starts the loading animation and timer.
     */
    public void startLoading() {
        animationTimer.start();
        loadingTimer.start();
    }
    
    /**
     * Stops the loading animation and timer.
     */
    public void stopLoading() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
        if (loadingTimer != null && loadingTimer.isRunning()) {
            loadingTimer.stop();
        }
    }
    
    /**
     * Custom paint method to render the loading screen.
     * 
     * @param g Graphics context for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable antialiasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw gradient background
        drawGradientBackground(g2d, width, height);
        
        // Draw loading spinner
        drawLoadingSpinner(g2d, width, height);
        
        // Draw loading message
        drawLoadingMessage(g2d, width, height);
        
        // Draw progress bar
        drawProgressBar(g2d, width, height);
        
        g2d.dispose();
    }
    
    /**
     * Draws the modern gradient background matching the main theme.
     */
    private void drawGradientBackground(Graphics2D g2d, int width, int height) {
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(15, 23, 42),
            0, height, new Color(30, 41, 59)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Add subtle radial glow effect in center
        RadialGradientPaint radialGlow = new RadialGradientPaint(
            width / 2f, height / 2f, Math.min(width, height) / 3f,
            new float[]{0f, 1f},
            new Color[]{new Color(59, 130, 246, 30), new Color(59, 130, 246, 0)}
        );
        g2d.setPaint(radialGlow);
        g2d.fillRect(0, 0, width, height);
    }
    
    /**
     * Draws the animated loading spinner.
     */
    private void drawLoadingSpinner(Graphics2D g2d, int width, int height) {
        int centerX = width / 2;
        int centerY = height / 2 - 40;
        int radius = 30;
        
        // Save the current transform
        g2d.translate(centerX, centerY);
        g2d.rotate(Math.toRadians(spinnerAngle));
        
        // Draw spinner arcs with varying opacity using modern accent color
        for (int i = 0; i < 8; i++) {
            float alpha = 1.0f - (i * 0.125f);
            g2d.setColor(new Color(59, 130, 246, (int)(alpha * 255)));  // Modern blue accent
            g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int angle = i * 45;
            int x1 = (int)(Math.cos(Math.toRadians(angle)) * (radius - 10));
            int y1 = (int)(Math.sin(Math.toRadians(angle)) * (radius - 10));
            int x2 = (int)(Math.cos(Math.toRadians(angle)) * radius);
            int y2 = (int)(Math.sin(Math.toRadians(angle)) * radius);
            
            g2d.drawLine(x1, y1, x2, y2);
        }
        
        // Restore the transform
        g2d.rotate(-Math.toRadians(spinnerAngle));
        g2d.translate(-centerX, -centerY);
    }
    
    /**
     * Draws the loading message.
     */
    private void drawLoadingMessage(Graphics2D g2d, int width, int height) {
        g2d.setFont(messageFont);
        g2d.setColor(Color.WHITE);
        
        FontMetrics fm = g2d.getFontMetrics();
        int messageWidth = fm.stringWidth(loadingMessage);
        int x = (width - messageWidth) / 2;
        int y = height / 2 + 40;
        
        g2d.drawString(loadingMessage, x, y);
    }
    
    /**
     * Draws the progress bar and percentage.
     */
    private void drawProgressBar(Graphics2D g2d, int width, int height) {
        int barWidth = 300;
        int barHeight = 8;
        int x = (width - barWidth) / 2;
        int y = height / 2 + 70;
        
        // Draw progress bar background with modern styling
        g2d.setColor(new Color(51, 65, 85));
        g2d.fillRoundRect(x, y, barWidth, barHeight, barHeight, barHeight);
        
        // Draw progress bar fill with modern gradient
        if (progress > 0) {
            int fillWidth = (barWidth * progress) / 100;
            GradientPaint progressGradient = new GradientPaint(
                x, y, new Color(59, 130, 246),
                x + fillWidth, y, new Color(37, 99, 235)
            );
            g2d.setPaint(progressGradient);
            g2d.fillRoundRect(x, y, fillWidth, barHeight, barHeight, barHeight);
        }
        
        // Draw progress percentage
        g2d.setFont(progressFont);
        g2d.setColor(Color.WHITE);
        String progressText = progress + "%";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(progressText);
        int textX = (width - textWidth) / 2;
        int textY = y + barHeight + 20;
        
        g2d.drawString(progressText, textX, textY);
    }
}