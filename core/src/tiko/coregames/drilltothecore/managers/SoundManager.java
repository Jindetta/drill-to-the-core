package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

public class SoundManager implements Disposable {
    private HashMap<String, Sound> sounds;
    private Music music;

    private boolean soundMuted, musicMuted;
    private float soundVolume, musicVolume;

    public SoundManager(SettingsManager settings) {
        sounds = new HashMap<>();

        soundVolume = settings.getFloatIfExists("soundVolume", 0.5f);
        soundMuted = settings.getBooleanIfExists("soundMuted", false);

        musicVolume = settings.getFloatIfExists("musicVolume", 0.5f);
        musicMuted = settings.getBooleanIfExists("musicMuted", false);
    }

    public void addSound(String identifier, String fileName) {
        if (!sounds.containsKey(identifier)) {
            FileHandle file = Gdx.files.internal(fileName);

            if (file != null && file.exists()) {
                sounds.put(identifier, Gdx.audio.newSound(file));
            }
        }
    }

    private float getSoundVolume() {
        return soundMuted ? 0 : soundVolume;
    }

    public void playSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Sound sound = sounds.get(identifier);
            sound.play(getSoundVolume());
        }
    }

    public void loopSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Sound sound = sounds.get(identifier);
            sound.loop(getSoundVolume());
        }
    }

    public void deleteSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            sounds.remove(identifier).dispose();
        }
    }

    public void muteSounds(boolean value) {
        soundMuted = value;

        if (sounds != null && !sounds.isEmpty()) {
            for (Sound sound : sounds.values()) {
                sound.setVolume(-1, getSoundVolume());
            }
        }
    }

    private float getMusicVolume() {
        return musicMuted ? 0 : musicVolume;
    }

    public void playMusic(String file) {
        if (music == null) {
            music = Gdx.audio.newMusic(Gdx.files.internal(file));

            music.setLooping(true);
            music.setVolume(getMusicVolume());
            music.play();
        }
    }

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
    }
}