package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

import java.util.Locale;

public class Utilities {
    public static final float WORLD_WIDTH = 8f;
    public static final float WORLD_HEIGHT = 7f;
    public static final float WORLD_SCALE = 100f;

    public static float toWorldUnits(float value) {
        return value / WORLD_SCALE;
    }

    public static int toPixelUnits(float value) {
        return Math.round(value * WORLD_SCALE);
    }

    public static class Debug {
        private static float timeElapsed = 0;
        private static BitmapFont font = new BitmapFont();

        private static final int SAFEZONE_SIZE = 5;

        private static String debugInfo(float delta) {
            long frameId = Gdx.graphics.getFrameId();
            int framesPerSecond = Gdx.graphics.getFramesPerSecond();

            return String.format(
                Locale.ENGLISH,
                "FPS: %d, FrameID: %d, Delta: %.3f, TimeElapsed: %.3f",
                framesPerSecond, frameId, delta, timeElapsed
            );
        }

        public static void render(SpriteBatch batch) {
            float delta = Gdx.graphics.getDeltaTime();
            timeElapsed += delta;

            font.draw(batch, debugInfo(delta), SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }

        public static void dispose() {
            font.dispose();
        }
    }
}