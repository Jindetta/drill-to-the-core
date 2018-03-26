package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class SoundManager implements Disposable {
    private Array<Sound> sounds;
    private Music music;

    public SoundManager() {
        // Not implemented
    }

    @Override
    public void dispose() {
        music.dispose();
        for (Sound sound : sounds) {
            sound.dispose();
        }
    }
}