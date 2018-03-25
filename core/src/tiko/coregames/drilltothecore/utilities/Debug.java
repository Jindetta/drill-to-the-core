package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class Debug {
    private static BitmapFont font;
    private static HashMap<String, BaseDebug> debugData;
    private static OrthographicCamera debugCamera;

    static {
        font = new BitmapFont(Gdx.files.internal("menu/debug.fnt"));
        debugCamera = new OrthographicCamera();
        addDebugger(new BaseDebug(), "");
    }

    public static void addDebugger(BaseDebug debugObject, String key) {
        if (debugObject != null) {
            if (debugData == null) {
                debugData = new HashMap<>();
            }

            debugData.put(key, debugObject);
        }
    }

    public static void render(SpriteBatch batch) {
        if (DEBUG_MODE && debugData != null) {
            batch.setProjectionMatrix(debugCamera.combined);
            for (BaseDebug debugObject : debugData.values()) {
                debugObject.render(batch);
            }
        }
    }

    public static void resize(int width, int height) {
        debugCamera.setToOrtho(false, width, height);
    }

    public static void dispose() {
        font.dispose();
    }

    private static class BaseDebug {
        String debugString;
        GlyphLayout layout;

        private float timeElapsed;

        BaseDebug() {
            timeElapsed = 0;
            debugString = "FPS: %d (%d)\n%d:%02d:%02d -> %.1f +%.3f\nAccelX: %.4f\nAccelY: %.4f";
            layout = new GlyphLayout(font, debugString);
        }

        void render(SpriteBatch batch) {
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
                    Gdx.input.getAccelerometerY(),
                    Gdx.input.getAccelerometerZ()
                )
            );

            font.draw(batch, layout, SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }
    }

    public static class CustomDebug extends BaseDebug {
        public CustomDebug() {
            debugString = "";
            layout = new GlyphLayout(font, debugString);
        }

        public void setDebugString(String value) {
            debugString = value;
        }

        void render(SpriteBatch batch) {
            if (debugString == null) {
                return;
            }

            layout.setText(font, debugString);
            font.draw(batch, layout, Gdx.graphics.getWidth() - layout.width - SAFEZONE_SIZE, Gdx.graphics.getHeight() - SAFEZONE_SIZE);
        }
    }
}