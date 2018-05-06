package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * SoundManager class will manage all sound related stuff.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SoundManager implements Disposable {
    private HashMap<String, Sound> sounds;
    private HashMap<String, Music> longSounds;
    private Music music;

    private boolean soundMuted, musicMuted;
    private float soundVolume, musicVolume;

    public SoundManager(SettingsManager settings) {
        sounds = new HashMap<>();
        longSounds = new HashMap<>();

        soundVolume = settings.getFloatIfExists("soundVolume", 50) / 100;
        soundMuted = settings.getBooleanIfExists("soundMuted", false);

        musicVolume = settings.getFloatIfExists("musicVolume", 50) / 100;
        musicMuted = settings.getBooleanIfExists("musicMuted", false);
    }

    /**
     * Adds a new sound to the list.
     *
     * @param identifier    sound identifier
     * @param fileName      file path
     */
    public void addSound(String identifier, String fileName) {
        if (!sounds.containsKey(identifier)) {
            FileHandle file = Gdx.files.internal(fileName);

            if (file != null && file.exists()) {
                sounds.put(identifier, Gdx.audio.newSound(file));
            }
        }
    }

    public void addLongSound(String identifier, String fileName) {
        if (!longSounds.containsKey(identifier)) {
            FileHandle file = Gdx.files.internal(fileName);

            if (file != null && file.exists()) {
                longSounds.put(identifier, Gdx.audio.newMusic(file));
            }
        }
    }

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
            Sound sound = sounds.get(identifier);
            sound.play(getSoundVolume());
        }
    }

    /**
     * Plays long sound by given identifier.
     *
     * @param identifier    sound identifier
     */
    public void playLongSound(String identifier) {
        if (longSounds.containsKey(identifier)) {
            Music longSound = longSounds.get(identifier);
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
        if (longSounds.containsKey(identifier)) {
            Music longSound = longSounds.get(identifier);

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

        if (sounds != null && !sounds.isEmpty()) {
            for (Sound sound : sounds.values()) {
                sound.setVolume(0, getSoundVolume());
            }
        }
    }

    private float getMusicVolume() {
        return musicMuted ? 0 : musicVolume;
    }

    /**
     * Plays music.
     *
     * @param file  file path
     */
    public void playMusic(String file) {
        if (music == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal(file));

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

        if (music != null && music.isPlaying()) {
            music.setVolume(getMusicVolume());
        }
    }

    @Override
    public void dispose() {
        if (music != null) {
            music.dispose();
        }

        for (Sound sound : sounds.values()) {
            sound.dispose();
        }

        for (Music longSound : longSounds.values()) {
            longSound.dispose();
        }
    }
}