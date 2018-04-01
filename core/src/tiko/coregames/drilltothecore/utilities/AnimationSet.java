package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class AnimationSet implements Disposable {
    private HashMap<String, FrameRegion> animations;
    private String animationKey;
    private Texture texture;

    public AnimationSet(String textureFile) {
        texture = new Texture(textureFile);
        animations = new HashMap<>();
    }

    public void setActiveAnimation(String key, Boolean loopMode, Float x, Float y) {
        animationKey = key;

        if (animations.containsKey(key)) {
            FrameRegion animation = animations.get(key);

            if (loopMode != null) {
                animation.looping = loopMode;
            }

            if (x != null) {
                animation.x = x;
            }

            if (y != null) {
                animation.y = y;
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        if (animationKey != null && animations.containsKey(animationKey)) {
            animations.get(animationKey).render(batch, delta, true, 0, 0);
        } else {
            for (FrameRegion frames : animations.values()) {
                frames.render(batch, delta, true, 0, 0);
            }
        }
    }

    public void createAnimation(String key, int offsetX, int offsetY, int rows, int columns, float framesPerSecond) {
        TextureRegion[] frames = new TextureRegion[rows * columns];

        for (int i = 0; i < frames.length; i++) {
            frames[i] = new TextureRegion(texture, (i + offsetX) * BIG_TILE_SIZE, 0, BIG_TILE_SIZE, BIG_TILE_SIZE);
        }

        animations.put(key, new FrameRegion(frames, framesPerSecond));
    }

    @Override
    public void dispose() {
        texture.dispose();
    }

    private static class FrameRegion {
        Animation<TextureRegion> animation;
        float keyState, x, y;
        boolean looping;

        public FrameRegion(TextureRegion[] frames, float framesPerSecond) {
            animation = new Animation<>(framesPerSecond, frames);

            looping = false;
            keyState = 0;
            x = 0;
            y = 0;
        }

        public void render(SpriteBatch batch, float delta, boolean loop, float x, float y) {
            keyState += delta;

            TextureRegion frame = animation.getKeyFrame(keyState, loop);
            batch.draw(frame, x, y);
        }
    }
}
