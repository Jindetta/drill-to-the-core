package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Locale;

public class Utilities {
    public static final float WORLD_WIDTH = 768f;
    public static final float WORLD_HEIGHT = 512f;
    public static final float WORLD_SCALE = 1f;

    public static final float SAFEZONE_SIZE = 5;
    public static final boolean DEBUG_MODE = true;

    public static float toWorldUnits(float value) {
        return value / WORLD_SCALE;
    }

    public static int toPixelUnits(float value) {
        return Math.round(value * WORLD_SCALE);
    }

    public static class Debug {
        private static float timeElapsed;
        private static BitmapFont font;

        public static void render(SpriteBatch batch) {
            if (DEBUG_MODE) {
                float delta = Gdx.graphics.getDeltaTime();
                timeElapsed += delta;

                final String debugString = String.format(
                    Locale.ENGLISH, "Frames/second: %d, Current frame: %d%n%d:%02d:%02d -> %.3f (+%.3f)",
                    Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getFrameId(),
                    (int) timeElapsed / (60 * 60),
                    (int) timeElapsed / 60 % 60,
                    (int) timeElapsed % 60,
                    timeElapsed, delta
                );

                font.draw(batch, debugString, SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
            }
        }

        public static void dispose() {
            if (DEBUG_MODE) {
                font.dispose();
            }
        }

        static {
            if (DEBUG_MODE) {
                font = new BitmapFont();
                timeElapsed = 0;
            }
        }
    }
}