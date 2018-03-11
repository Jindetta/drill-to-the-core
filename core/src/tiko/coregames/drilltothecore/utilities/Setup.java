package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.CoreSetup;

public abstract class Setup {
    private static SpriteBatch batch;
    private static CoreSetup host;

    public static void initialize(CoreSetup host, SpriteBatch batch) {
        Setup.batch = batch;
        Setup.host = host;
    }

    public static SpriteBatch getBatch() {
        return batch;
    }

    public static void setScreen(Screen screen) {
        host.setScreen(screen);
    }
}
