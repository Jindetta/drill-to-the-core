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

        private static String debugInfo() {
            long frameId = Gdx.graphics.getFrameId();
            int framesPerSecond = Gdx.graphics.getFramesPerSecond();
            float delta = Gdx.graphics.getDeltaTime();

            timeElapsed += delta;

            float timeLeft = timeElapsed;
            int hours = (int) timeLeft / (60 * 60);
            int minutes = (int) timeLeft / 60 % 60;
            int seconds = (int) timeLeft % 60;

            return String.format(
                Locale.ENGLISH,
                "Frames/second: %d, Current frame: %d%n%d:%02d:%02d -> %.3f (+%.3f)",
                framesPerSecond, frameId, hours, minutes, seconds, timeElapsed, delta
            );
        }

        public static void render(SpriteBatch batch) {
            font.draw(batch, debugInfo(), SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }

        public static void dispose() {
            font.dispose();
        }
    }
}