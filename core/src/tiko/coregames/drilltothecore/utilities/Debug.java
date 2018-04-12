package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class Debug {
    private BitmapFont font;
    private HashMap<String, BaseDebug> debugData;
    private OrthographicCamera screen;
    private String customDebugString;
    private static Debug instance;

    private Debug() {
        customDebugString = "";
        screen = new OrthographicCamera();
        font = new BitmapFont(Gdx.files.internal("ui/debug.fnt"));
    }

    public static void initialize() {
        if (DEBUG_MODE && instance == null) {
            instance = new Debug();
            addDebugger(new BaseDebug(), "");
        }
    }

    public static void addDebugger(BaseDebug debugObject, String key) {
        if (instance != null && debugObject != null) {
            if (instance.debugData == null) {
                instance.debugData = new HashMap<>();
            }

            instance.debugData.put(key, debugObject);
        }
    }

    public static void render(SpriteBatch batch) {
        if (instance != null && instance.debugData != null) {
            batch.setProjectionMatrix(instance.screen.combined);
            for (BaseDebug debugObject : instance.debugData.values()) {
                debugObject.render(batch);
            }
        }
    }

    public static void resize(int width, int height) {
        if (instance != null) {
            instance.screen.setToOrtho(false, width, height);
        }
    }

    public static void setCustomDebugString(String value) {
        if (instance != null && value != null) {
            instance.customDebugString = value;
        }
    }

    public static void dispose() {
        if (instance != null) {
            instance.font.dispose();
            instance = null;
        }
    }

    private static class BaseDebug {
        String debugString;
        GlyphLayout layout;

        private float timeElapsed;

        BaseDebug() {
            timeElapsed = 0;
            debugString = "FPS: %d (%d)\n%d:%02d:%02d -> %.1f +%.3f\nX: %.04f\nY: %.04f\nZ: %.04f\n\n%s";
            layout = new GlyphLayout(instance.font, debugString);
        }

        void render(SpriteBatch batch) {
            float delta = Gdx.graphics.getDeltaTime();
            timeElapsed += delta;

            layout.setText(instance.font,
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
                    Gdx.input.getAccelerometerZ(),
                    instance.customDebugString
                )
            );

            instance.font.draw(batch, layout, SAFE_ZONE_SIZE, instance.screen.viewportHeight - SAFE_ZONE_SIZE);
        }
    }
}