package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;

/**
 * SoundManager class will manage all sound related stuff.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SoundManager {
    /**
     * Stores all sounds.
     */
    private HashMap<String, String> sounds;

    /**
     * Stores current background music path.
     */
    private String musicFile;

    /**
     * Stores instance of AssetManager.
     */
    private AssetManager assets;

    /**
     * Defines muted states.
     */
    private boolean soundMuted, musicMuted;

    /**
     * Defines volume states.
     */
    private float soundVolume, musicVolume;

    /**
     * Overloads default constructor.
     *
     * @param settings  SettingsManager instance
     * @param assets    AssetsManager instance
     */
    public SoundManager(SettingsManager settings, AssetManager assets) {
        sounds = new HashMap<>();
        this.assets = assets;

        loadSoundSettings(settings);
    }

    /**
     * Loads volume/mute settings.
     *
     * @param settings  SettingsManager instance
     */
    public void loadSoundSettings(SettingsManager settings) {
        soundVolume = (float) settings.getIntegerIfExists("soundVolume", 50) / 100;
        soundMuted = settings.getBooleanIfExists("soundMuted", false);

        musicVolume = (float) settings.getIntegerIfExists("musicVolume", 50) / 100;
        musicMuted = settings.getBooleanIfExists("musicMuted", false);
    }

    /**
     * Adds a new sound to the list (wrapper).
     *
     * @param identifier    sound identifier
     * @param fileName      file path
     */
    public void addSound(String identifier, String fileName, boolean longSound) {
        addSound(identifier, fileName, longSound ? Music.class : Sound.class);
    }

    /**
     * Adds a new sound to the list.
     *
     * @param identifier    sound identifier
     * @param fileName      file path
     * @param type          class type
     * @param <T>           generic type
     */
    private <T> void addSound(String identifier, String fileName, Class<T> type) {
        if (!sounds.containsKey(identifier)) {
            FileHandle file = Gdx.files.internal(fileName);

            if (file != null && file.exists()) {
                sounds.put(identifier, fileName);

                assets.load(fileName, type);
                assets.finishLoadingAsset(fileName);
            }
        }
    }

    /**
     * Gets current sound volume.
     *
     * @return current volume
     */
    private float getSoundVolume() {
        return soundMuted ? 0 : soundVolume;
    }

    /**
     * Plays sound by given identifier.
     *
     * @param identifier    sound identifier
     */
    public void playSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Sound sound = assets.get(sounds.get(identifier));
            sound.play(getSoundVolume());
        }
    }

    /**
     * Plays long sound by given identifier.
     *
     * @param identifier    sound identifier
     */
    public void playLongSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Music longSound = assets.get(sounds.get(identifier));
            longSound.setVolume(getSoundVolume());
            longSound.setLooping(true);

            if (!longSound.isPlaying()) {
                longSound.play();
            }
        }
    }

    /**
     * Pauses long sound by given identifier.
     *
     * @param identifier    sound identifier
     */
    public void pauseLongSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Music longSound = assets.get(sounds.get(identifier));

            if (longSound.isPlaying()) {
                longSound.pause();
            }
        }
    }

    /**
     * Mutes all sounds.
     *
     * @param value     mute state
     */
    public void muteSounds(boolean value) {
        soundMuted = value;
        update();
    }

    /**
     * Gets current music volume
     *
     * @return current volume
     */
    private float getMusicVolume() {
        return musicMuted ? 0 : musicVolume;
    }

    /**
     * Plays music.
     *
     * @param file  file path
     */
    public void playMusic(String file) {
        if (musicFile == null) {
            assets.load(file, Music.class);
            assets.finishLoadingAsset(file);
            Music music = assets.get(file);
            musicFile = file;

            music.setLooping(true);
            music.setVolume(getMusicVolume());
            music.play();
        }
    }

    /**
     * Mutes music.
     *
     * @param value     mute state
     */
    public void muteMusic(boolean value) {
        musicMuted = value;
        update();
    }

    /**
     * Update volume status.
     */
    public void update() {
        if (musicFile != null) {
            Music music = assets.get(musicFile);
            music.setVolume(getMusicVolume());
        }

        if (sounds != null && !sounds.isEmpty()) {
            for (String id : sounds.values()) {
                if (assets.getAssetType(id) == Sound.class) {
                    Sound sound = assets.get(id);
                    sound.setVolume(0, getSoundVolume());
                } else {
                    Music sound = assets.get(id);
                    sound.setVolume(getSoundVolume());
                }
            }
        }
    }
}