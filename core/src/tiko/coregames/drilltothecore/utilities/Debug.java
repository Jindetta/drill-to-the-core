package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Debug {
    private static BitmapFont font;
    private static Array<BaseDebug> debugData;

    static {
        font = new BitmapFont(Gdx.files.internal("menu/debug.fnt"));
        addDebugger(new BaseDebug());
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
        font.dispose();
    }

    public static class BaseDebug {
        String debugString;
        GlyphLayout layout;

        private float timeElapsed;

        BaseDebug() {
            timeElapsed = 0;
            debugString = "FPS: %d (%d)%n%d:%02d:%02d -> %.3f (+%.3f)%nAccelX: %.4f%nAccelY: %.4f%nAccelZ: %.4f";
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
                    timeElapsed,
                    delta,
                    Gdx.input.getAccelerometerX(),
                    Gdx.input.getAccelerometerY(),
                    Gdx.input.getAccelerometerZ()
                )
            );

            font.draw(batch, layout, SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }
    }
}