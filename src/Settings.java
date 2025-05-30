import java.io.*;
import java.util.Properties;

/**
 * Manages persistent game settings and configuration.
 * 
 * This singleton class handles loading, saving, and managing user preferences
 * for the Snake Game. Settings are stored in a properties file and include:
 * - Visual theme selection (Classic, Ocean, Forest, etc.)
 * - Random theme mode toggle
 * - Sound effects enable/disable
 * 
 * The class uses the Singleton pattern to ensure consistent settings
 * access throughout the application and automatic persistence of changes.
 * 
 * Settings File: game_settings.properties
 * 
 * @author Snake Game Development Team
 * @version 1.0
 */
public class Settings {
    /** Name of the properties file where settings are stored */
    private static final String SETTINGS_FILE = "game_settings.properties";
    
    /** Singleton instance of the Settings class */
    private static Settings instance;
    
    /** Properties object for reading/writing settings file */
    private Properties properties;
    
    /** Currently selected visual theme name (e.g., "Classic", "Ocean") */
    private String selectedTheme = "Classic";
    
    /** Whether to randomly cycle through themes during gameplay */
    private boolean randomThemes = false;
    
    /** Whether sound effects and background music are enabled */
    private boolean soundEnabled = true;
    
    /**
     * Private constructor for singleton pattern.
     * 
     * Initializes the Properties object and loads existing settings
     * from the settings file. If the file doesn't exist, default
     * values are used.
     */
    private Settings() {
        properties = new Properties();
        loadSettings();
    }
    
    /**
     * Returns the singleton instance of the Settings class.
     * 
     * Creates a new instance if one doesn't exist, ensuring that
     * all parts of the application share the same settings state.
     * 
     * @return The singleton Settings instance
     */
    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    /**
     * Loads settings from the properties file.
     * 
     * Attempts to read the settings file and parse the stored values.
     * If the file doesn't exist or cannot be read (e.g., first run),
     * default values are used instead:
     * - Theme: "Classic"
     * - Random Themes: false
     * - Sound Enabled: true
     */
    private void loadSettings() {
        try (FileInputStream fis = new FileInputStream(SETTINGS_FILE)) {
            properties.load(fis);
            selectedTheme = properties.getProperty("theme", "Classic");
            randomThemes = Boolean.parseBoolean(properties.getProperty("randomThemes", "false"));
            soundEnabled = Boolean.parseBoolean(properties.getProperty("soundEnabled", "true"));
        } catch (IOException e) {
            // File doesn't exist or can't be read, use defaults
            selectedTheme = "Classic";
            randomThemes = false;
            soundEnabled = true;
        }
    }
    
    /**
     * Saves current settings to the properties file.
     * 
     * Writes all current setting values to the persistent storage file.
     * This method is automatically called whenever a setting is changed
     * through the setter methods, ensuring settings are always saved.
     * 
     * If the file cannot be written (e.g., permission issues), an error
     * message is printed to stderr but the application continues running.
     */
    public void saveSettings() {
        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            properties.setProperty("theme", selectedTheme);
            properties.setProperty("randomThemes", String.valueOf(randomThemes));
            properties.setProperty("soundEnabled", String.valueOf(soundEnabled));
            properties.store(fos, "Snake Game Settings");
        } catch (IOException e) {
            System.err.println("Could not save settings: " + e.getMessage());
        }
    }
    
    public String getSelectedTheme() {
        return selectedTheme;
    }
    
    public void setSelectedTheme(String theme) {
        this.selectedTheme = theme;
        saveSettings();
    }
    
    public boolean isRandomThemes() {
        return randomThemes;
    }
    
    public void setRandomThemes(boolean randomThemes) {
        this.randomThemes = randomThemes;
        saveSettings();
    }
    
    public Theme getCurrentTheme() {
        if (randomThemes) {
            return Theme.getRandomTheme();
        } else {
            return Theme.getThemeByName(selectedTheme);
        }
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        saveSettings();
    }
}