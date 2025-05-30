import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages sound effects and background music for the Snake Game.
 * Provides theme-specific audio experiences that match the visual themes.
 * Handles loading, playing, and managing audio clips efficiently.
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, Clip> soundClips;
    private Map<String, String> themeSoundMappings;
    private boolean soundEnabled = true;
    private float volume = 0.7f;
    private Clip backgroundMusic;
    private String currentTheme = "Classic";
    
    private SoundManager() {
        soundClips = new ConcurrentHashMap<>();
        themeSoundMappings = new HashMap<>();
        initializeThemeSoundMappings();
        loadSoundEffects();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Maps each theme to its corresponding sound profile.
     * Each theme has unique sound effects that match its visual style.
     */
    private void initializeThemeSoundMappings() {
        // Classic theme - traditional arcade sounds
        themeSoundMappings.put("Classic_eat", "classic_eat");
        themeSoundMappings.put("Classic_gameOver", "classic_gameOver");
        themeSoundMappings.put("Classic_levelUp", "classic_levelUp");
        themeSoundMappings.put("Classic_background", "classic_background");
        
        // Ocean theme - water and bubble sounds
        themeSoundMappings.put("Ocean_eat", "ocean_bubble");
        themeSoundMappings.put("Ocean_gameOver", "ocean_crash");
        themeSoundMappings.put("Ocean_levelUp", "ocean_wave");
        themeSoundMappings.put("Ocean_background", "ocean_ambient");
        
        // Forest theme - nature sounds
        themeSoundMappings.put("Forest_eat", "forest_berry");
        themeSoundMappings.put("Forest_gameOver", "forest_fall");
        themeSoundMappings.put("Forest_levelUp", "forest_growth");
        themeSoundMappings.put("Forest_background", "forest_ambient");
        
        // Neon theme - electronic sounds
        themeSoundMappings.put("Neon_eat", "neon_zap");
        themeSoundMappings.put("Neon_gameOver", "neon_shutdown");
        themeSoundMappings.put("Neon_levelUp", "neon_powerup");
        themeSoundMappings.put("Neon_background", "neon_synth");
        
        // Sunset theme - warm, mellow sounds
        themeSoundMappings.put("Sunset_eat", "sunset_chime");
        themeSoundMappings.put("Sunset_gameOver", "sunset_fade");
        themeSoundMappings.put("Sunset_levelUp", "sunset_glow");
        themeSoundMappings.put("Sunset_background", "sunset_ambient");
        
        // Retro theme - classic 8-bit sounds
        themeSoundMappings.put("Retro_eat", "retro_coin");
        themeSoundMappings.put("Retro_gameOver", "retro_death");
        themeSoundMappings.put("Retro_levelUp", "retro_levelup");
        themeSoundMappings.put("Retro_background", "retro_bgm");
        
        // Cyberpunk theme - futuristic sounds
        themeSoundMappings.put("Cyberpunk_eat", "cyber_data");
        themeSoundMappings.put("Cyberpunk_gameOver", "cyber_error");
        themeSoundMappings.put("Cyberpunk_levelUp", "cyber_upgrade");
        themeSoundMappings.put("Cyberpunk_background", "cyber_ambient");
        
        // Sakura theme - Japanese-inspired sounds
        themeSoundMappings.put("Sakura_eat", "sakura_petal");
        themeSoundMappings.put("Sakura_gameOver", "sakura_wind");
        themeSoundMappings.put("Sakura_levelUp", "sakura_bloom");
        themeSoundMappings.put("Sakura_background", "sakura_zen");
        
        // Arctic theme - ice and wind sounds
        themeSoundMappings.put("Arctic_eat", "arctic_crystal");
        themeSoundMappings.put("Arctic_gameOver", "arctic_crack");
        themeSoundMappings.put("Arctic_levelUp", "arctic_aurora");
        themeSoundMappings.put("Arctic_background", "arctic_wind");
        
        // Lava theme - fire and volcanic sounds
        themeSoundMappings.put("Lava_eat", "lava_sizzle");
        themeSoundMappings.put("Lava_gameOver", "lava_explosion");
        themeSoundMappings.put("Lava_levelUp", "lava_eruption");
        themeSoundMappings.put("Lava_background", "lava_rumble");
        
        // Galaxy theme - space sounds
        themeSoundMappings.put("Galaxy_eat", "galaxy_star");
        themeSoundMappings.put("Galaxy_gameOver", "galaxy_void");
        themeSoundMappings.put("Galaxy_levelUp", "galaxy_cosmic");
        themeSoundMappings.put("Galaxy_background", "galaxy_space");
    }
    
    /**
     * Loads all sound effects into memory for quick playback.
     * Creates synthetic sounds when audio files are not available.
     */
    private void loadSoundEffects() {
        // For now, we'll create simple synthetic sounds
        // In a full implementation, you would load actual audio files
        createSyntheticSounds();
    }
    
    /**
     * Creates simple synthetic sound effects using Java's audio synthesis.
     * This provides basic audio feedback when actual sound files aren't available.
     */
    private void createSyntheticSounds() {
        try {
            // Create basic sound effects
            soundClips.put("classic_eat", createTone(800, 100, 0.3f));
            soundClips.put("classic_gameOver", createTone(200, 500, 0.5f));
            soundClips.put("classic_levelUp", createTone(1000, 300, 0.4f));
            
            // Ocean theme sounds
            soundClips.put("ocean_bubble", createTone(600, 150, 0.2f));
            soundClips.put("ocean_crash", createTone(150, 600, 0.4f));
            soundClips.put("ocean_wave", createTone(400, 400, 0.3f));
            
            // Forest theme sounds
            soundClips.put("forest_berry", createTone(700, 120, 0.25f));
            soundClips.put("forest_fall", createTone(300, 400, 0.35f));
            soundClips.put("forest_growth", createTone(500, 350, 0.3f));
            
            // Neon theme sounds
            soundClips.put("neon_zap", createTone(1200, 80, 0.4f));
            soundClips.put("neon_shutdown", createTone(100, 800, 0.5f));
            soundClips.put("neon_powerup", createTone(1500, 200, 0.4f));
            
            // Add more synthetic sounds for other themes...
            createAdditionalThemeSounds();
            
        } catch (Exception e) {
            System.err.println("Error creating synthetic sounds: " + e.getMessage());
        }
    }
    
    /**
     * Creates additional synthetic sounds for remaining themes.
     */
    private void createAdditionalThemeSounds() {
        try {
            // Sunset theme
            soundClips.put("sunset_chime", createTone(900, 200, 0.3f));
            soundClips.put("sunset_fade", createTone(250, 700, 0.4f));
            soundClips.put("sunset_glow", createTone(600, 300, 0.35f));
            
            // Retro theme
            soundClips.put("retro_coin", createTone(1000, 100, 0.4f));
            soundClips.put("retro_death", createTone(200, 600, 0.5f));
            soundClips.put("retro_levelup", createTone(800, 250, 0.4f));
            
            // Cyberpunk theme
            soundClips.put("cyber_data", createTone(1100, 90, 0.35f));
            soundClips.put("cyber_error", createTone(180, 500, 0.45f));
            soundClips.put("cyber_upgrade", createTone(1300, 180, 0.4f));
            
            // Sakura theme
            soundClips.put("sakura_petal", createTone(750, 180, 0.25f));
            soundClips.put("sakura_wind", createTone(350, 450, 0.3f));
            soundClips.put("sakura_bloom", createTone(650, 320, 0.3f));
            
            // Arctic theme
            soundClips.put("arctic_crystal", createTone(1400, 120, 0.3f));
            soundClips.put("arctic_crack", createTone(220, 550, 0.4f));
            soundClips.put("arctic_aurora", createTone(850, 400, 0.35f));
            
            // Lava theme
            soundClips.put("lava_sizzle", createTone(450, 150, 0.4f));
            soundClips.put("lava_explosion", createTone(120, 700, 0.6f));
            soundClips.put("lava_eruption", createTone(300, 500, 0.5f));
            
            // Galaxy theme
            soundClips.put("galaxy_star", createTone(1600, 100, 0.25f));
            soundClips.put("galaxy_void", createTone(80, 900, 0.4f));
            soundClips.put("galaxy_cosmic", createTone(1200, 350, 0.3f));
            
        } catch (Exception e) {
            System.err.println("Error creating additional theme sounds: " + e.getMessage());
        }
    }
    
    /**
     * Creates a simple tone using Java's audio synthesis.
     * 
     * @param frequency The frequency of the tone in Hz
     * @param duration The duration of the tone in milliseconds
     * @param volume The volume level (0.0 to 1.0)
     * @return A Clip containing the generated tone
     */
    private Clip createTone(int frequency, int duration, float volume) throws Exception {
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        int frameCount = (int) (format.getFrameRate() * duration / 1000);
        byte[] audioData = new byte[frameCount * format.getFrameSize()];
        
        for (int i = 0; i < frameCount; i++) {
            double angle = 2.0 * Math.PI * i * frequency / format.getFrameRate();
            short sample = (short) (Math.sin(angle) * Short.MAX_VALUE * volume);
            audioData[i * 2] = (byte) (sample & 0xFF);
            audioData[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream ais = new AudioInputStream(bais, format, frameCount);
        
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        return clip;
    }
    
    /**
     * Sets the current theme for sound effects.
     * 
     * @param theme The name of the theme to use for sounds
     */
    public void setTheme(String theme) {
        this.currentTheme = theme;
        stopBackgroundMusic(); // Stop current background music when theme changes
    }
    
    /**
     * Plays the eating sound effect for the current theme.
     */
    public void playEatSound() {
        if (!soundEnabled) return;
        String soundKey = themeSoundMappings.get(currentTheme + "_eat");
        playSound(soundKey);
    }
    
    /**
     * Plays the game over sound effect for the current theme.
     */
    public void playGameOverSound() {
        if (!soundEnabled) return;
        String soundKey = themeSoundMappings.get(currentTheme + "_gameOver");
        playSound(soundKey);
    }
    
    /**
     * Plays the level up sound effect for the current theme.
     */
    public void playLevelUpSound() {
        if (!soundEnabled) return;
        String soundKey = themeSoundMappings.get(currentTheme + "_levelUp");
        playSound(soundKey);
    }
    
    /**
     * Starts playing background music for the current theme.
     */
    public void playBackgroundMusic() {
        if (!soundEnabled) return;
        String soundKey = themeSoundMappings.get(currentTheme + "_background");
        if (soundKey != null && soundClips.containsKey(soundKey)) {
            stopBackgroundMusic();
            backgroundMusic = soundClips.get(soundKey);
            if (backgroundMusic != null) {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }
    
    /**
     * Stops the currently playing background music.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }
    
    /**
     * Plays a specific sound effect.
     * 
     * @param soundKey The key identifying the sound to play
     */
    private void playSound(String soundKey) {
        if (soundKey != null && soundClips.containsKey(soundKey)) {
            Clip clip = soundClips.get(soundKey);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            }
        }
    }
    
    /**
     * Enables or disables all sound effects.
     * 
     * @param enabled true to enable sounds, false to disable
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopBackgroundMusic();
        }
    }
    
    /**
     * Checks if sound is currently enabled.
     * 
     * @return true if sound is enabled, false otherwise
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Sets the master volume for all sounds.
     * 
     * @param volume Volume level from 0.0 (silent) to 1.0 (full volume)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        // Apply volume to all loaded clips
        for (Clip clip : soundClips.values()) {
            if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(this.volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(Math.max(gainControl.getMinimum(), dB));
            }
        }
    }
    
    /**
     * Gets the current volume level.
     * 
     * @return Current volume level (0.0 to 1.0)
     */
    public float getVolume() {
        return volume;
    }
    
    /**
     * Cleans up resources when the sound manager is no longer needed.
     */
    public void cleanup() {
        stopBackgroundMusic();
        for (Clip clip : soundClips.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundClips.clear();
    }
}