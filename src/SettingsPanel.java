// Import necessary Java Swing and AWT libraries for GUI components
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * A comprehensive graphical user interface panel for configuring game settings.
 * 
 * This panel serves as the central hub for all game customization options,
 * providing an intuitive and visually appealing interface for players to
 * personalize their gaming experience. The panel includes:
 * 
 * FEATURES:
 * - Theme selection from a curated list of visual themes
 * - Random theme option for dynamic variety in each game session
 * - Sound effects enable/disable toggle for audio preferences
 * - Real-time theme preview showing visual changes instantly
 * - Automatic settings persistence using the Settings singleton pattern
 * 
 * DESIGN PRINCIPLES:
 * - Modern dark theme with gradient buttons and smooth animations
 * - Responsive layout using GridBagLayout for proper component alignment
 * - Immediate visual feedback through live preview functionality
 * - User-friendly error handling and confirmation dialogs
 * 
 * TECHNICAL IMPLEMENTATION:
 * - Extends JPanel for seamless integration with the main game window
 * - Uses observer pattern for real-time preview updates
 * - Implements custom painting for gradient buttons and theme previews
 * - Follows MVC pattern with clear separation of UI and data logic
 * 
 * @author Snake Game Development Team
 * @version 1.0
 * @since 1.0
 */
public class SettingsPanel extends JPanel {
    /** Reference to the main game window for navigation */
    private SnakeGame parent;
    
    /** Settings instance for loading and saving configuration */
    private Settings settings;
    
    /** Dropdown for selecting visual themes */
    private JComboBox<String> themeComboBox;
    
    /** Checkbox for enabling random theme selection */
    private JCheckBox randomThemeCheckBox;
    
    /** Checkbox for enabling/disabling sound effects */
    private JCheckBox soundEnabledCheckBox;
    
    /** Panel that displays a preview of the selected theme */
    private JPanel previewPanel;
    
    /**
     * Creates a new settings panel with the specified parent window.
     * 
     * @param parent The main game window that contains this panel
     */
    public SettingsPanel(SnakeGame parent) {
        this.parent = parent;
        this.settings = Settings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(new Color(15, 23, 42));
        initComponents();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = createStyledLabel("⚙️ Game Settings", new Font("Segoe UI", Font.BOLD, 28), new Color(248, 250, 252));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Theme selection
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createStyledLabel("Theme:", new Font("Segoe UI", Font.BOLD, 16), new Color(248, 250, 252)), gbc);
        
        gbc.gridx = 1;
        String[] themeNames = Theme.THEMES.stream().map(t -> t.name).toArray(String[]::new);
        themeComboBox = new JComboBox<>(themeNames);
        themeComboBox.setSelectedItem(settings.getSelectedTheme());
        themeComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        themeComboBox.addActionListener(e -> updatePreview());
        contentPanel.add(themeComboBox, gbc);
        
        // Random themes checkbox
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        randomThemeCheckBox = createStyledCheckBox("Use random themes for each game", settings.isRandomThemes());
        randomThemeCheckBox.addActionListener(e -> {
            themeComboBox.setEnabled(!randomThemeCheckBox.isSelected());
            updatePreview();
        });
        contentPanel.add(randomThemeCheckBox, gbc);
        
        // Sound enabled checkbox
        gbc.gridy = 2;
        soundEnabledCheckBox = createStyledCheckBox("Enable sound effects", settings.isSoundEnabled());
        contentPanel.add(soundEnabledCheckBox, gbc);
        
        // Theme preview
        gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        
        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setOpaque(false);
        
        JLabel previewLabel = createStyledLabel("Theme Preview:", new Font("SansSerif", Font.PLAIN, 16), Color.WHITE);
        previewLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        previewContainer.add(previewLabel, BorderLayout.NORTH);
        
        previewPanel = new ThemePreviewPanel();
        previewPanel.setPreferredSize(new Dimension(300, 200));
        previewPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        previewContainer.add(previewPanel, BorderLayout.CENTER);
        
        contentPanel.add(previewContainer, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        
        JButton saveButton = createGradientButton("💾 Save & Apply", 
            new Color(34, 197, 94), new Color(22, 163, 74), new Color(21, 128, 61), 
            Color.WHITE, new Dimension(160, 40));
        saveButton.addActionListener(this::saveSettings);
        
        JButton cancelButton = createGradientButton("❌ Cancel", 
            new Color(71, 85, 105), new Color(51, 65, 85), new Color(30, 41, 59), 
            new Color(226, 232, 240), new Dimension(140, 40));
        cancelButton.addActionListener(e -> parent.showMapSelectPanel());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initialize preview
        themeComboBox.setEnabled(!randomThemeCheckBox.isSelected());
        updatePreview();
        setPreferredSize(new Dimension(600, 700));
    }
    
    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }
    
    private JCheckBox createStyledCheckBox(String text, boolean selected) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setSelected(selected);
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        checkBox.setForeground(new Color(148, 163, 184));
        checkBox.setOpaque(false);
        return checkBox;
    }
    
    private JButton createGradientButton(String text, Color startColor, Color endColor, 
                                        Color borderColor, Color textColor, Dimension size) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(textColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setPreferredSize(size);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private void updatePreview() {
        previewPanel.repaint();
    }
    
    private void saveSettings(ActionEvent e) {
        settings.setRandomThemes(randomThemeCheckBox.isSelected());
        if (!randomThemeCheckBox.isSelected()) {
            settings.setSelectedTheme((String) themeComboBox.getSelectedItem());
        }
        settings.setSoundEnabled(soundEnabledCheckBox.isSelected());
        
        JOptionPane.showMessageDialog(this, "Settings saved successfully!", "Settings", JOptionPane.INFORMATION_MESSAGE);
        parent.showMapSelectPanel();
    }
    
    private class ThemePreviewPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Theme currentTheme;
            if (randomThemeCheckBox.isSelected()) {
                // Show a sample theme when random is selected
                currentTheme = Theme.THEMES.get(0);
                
                // Draw "Random" text
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
                String randomText = "Random Theme";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(randomText);
                g2d.drawString(randomText, (getWidth() - textWidth) / 2, 30);
            } else {
                currentTheme = Theme.getThemeByName((String) themeComboBox.getSelectedItem());
            }
            
            // Draw background
            g2d.setColor(currentTheme.backgroundColor);
            g2d.fillRect(0, 50, getWidth(), getHeight() - 50);
            
            // Draw grid
            g2d.setColor(currentTheme.gridColor);
            int cellSize = 15;
            for (int i = 0; i < getWidth() / cellSize; i++) {
                g2d.drawLine(i * cellSize, 50, i * cellSize, getHeight());
            }
            for (int i = 0; i < (getHeight() - 50) / cellSize; i++) {
                g2d.drawLine(0, 50 + i * cellSize, getWidth(), 50 + i * cellSize);
            }
            
            // Draw sample snake
            g2d.setColor(currentTheme.snakeColor.brighter());
            g2d.fillRoundRect(60, 80, cellSize - 2, cellSize - 2, 5, 5); // head
            g2d.setColor(currentTheme.snakeColor);
            g2d.fillRoundRect(45, 80, cellSize - 2, cellSize - 2, 5, 5); // body
            g2d.fillRoundRect(30, 80, cellSize - 2, cellSize - 2, 5, 5); // body
            g2d.fillRoundRect(15, 80, cellSize - 2, cellSize - 2, 5, 5); // tail
            
            // Draw sample food
            g2d.setColor(currentTheme.foodColor);
            g2d.fillOval(105, 110, cellSize - 4, cellSize - 4);
            
            // Draw sample wall
            g2d.setColor(currentTheme.wallColor);
            g2d.fillRect(0, 50, getWidth(), 3);
            g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
            g2d.fillRect(0, 50, 3, getHeight() - 50);
            g2d.fillRect(getWidth() - 3, 50, 3, getHeight() - 50);
            
            // Draw sample obstacle
            g2d.setColor(currentTheme.obstacleColor);
            g2d.fillRect(150, 140, cellSize, cellSize);
            
            // Draw sample text
            g2d.setColor(currentTheme.textColor);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("Score: 150", 10, 70);
            g2d.drawString("Theme: " + currentTheme.name, 10, getHeight() - 10);
        }
    }
}