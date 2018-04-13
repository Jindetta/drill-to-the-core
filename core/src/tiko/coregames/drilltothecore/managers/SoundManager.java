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

    private boolean soundsMuted, musicMuted;
    private float soundVolume, musicVolume;

    public SoundManager() {
        // Not implemented
        sounds = new HashMap<>();
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
        return soundsMuted ? 0 : soundVolume;
    }

    public void playSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            Sound sound = sounds.get(identifier);
            sound.play(getSoundVolume());
        }
    }

    public void deleteSound(String identifier) {
        if (sounds.containsKey(identifier)) {
            sounds.remove(identifier).dispose();
        }
    }

    @Override
    public void dispose() {
        music.dispose();
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
    }
}