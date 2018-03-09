package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

import java.util.Locale;

public class Debug {
    private static Array<BaseDebug> debugData;

    static {
        addDebugger(new BaseDebug());
        addDebugger(new ControllerDebug());
    }

    public static void addDebugger(BaseDebug debugObject) {
        if (debugObject != null) {
            if (debugData == null) {
                debugData = new Array<>();
            }

            debugData.add(debugObject);
        }
    }

    public static void render(SpriteBatch batch) {
        if (DEBUG_MODE && debugData != null) {
            for (BaseDebug debugObject : debugData) {
                debugObject.render(batch);
            }
        }
    }

    public static void dispose() {
        if (DEBUG_MODE && debugData != null) {
            for (BaseDebug debugObject : debugData) {
                debugObject.dispose();
            }
        }
    }

    public static class BaseDebug implements Disposable {
        BitmapFont font;
        String debugString;
        GlyphLayout layout;

        private float timeElapsed;

        BaseDebug() {
            timeElapsed = 0;
            debugString = "Frames/second: %d, Current frame: %d%n%d:%02d:%02d -> %.3f (+%.3f)";
            setup();
        }

        void setup() {
            font = new BitmapFont();
            layout = new GlyphLayout(font, debugString);
        }

        public void render(SpriteBatch batch) {
            float delta = Gdx.graphics.getDeltaTime();
            timeElapsed += delta;

            layout.setText(font,
                String.format(
                    debugString,
                    Gdx.graphics.getFramesPerSecond(),
                    Gdx.graphics.getFrameId(),
                    (int) timeElapsed / (60 * 60),
                    (int) timeElapsed / 60 % 60,
                    (int) timeElapsed % 60,
                    timeElapsed, delta
                )
            );

            font.draw(batch, layout, SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }

        @Override
        public void dispose() {
            font.dispose();
        }
    }

    public static class ControllerDebug extends BaseDebug {
        ControllerDebug() {
            debugString = "Accelerometer values:%nX = %.4f, Y = %.4f, Z = %.4f";
            setup();
        }

        public void render(SpriteBatch batch) {
            layout.setText(font,
                String.format(
                    debugString,
                    Gdx.input.getAccelerometerX(),
                    Gdx.input.getAccelerometerY(),
                    Gdx.input.getAccelerometerZ()
                )
            );

            font.draw(batch, layout, SAFEZONE_SIZE, SAFEZONE_SIZE + layout.height);
        }
    }
}