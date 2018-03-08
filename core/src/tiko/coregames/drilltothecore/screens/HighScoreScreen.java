package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

public class HighScoreScreen extends BaseScreen {
    /**
     * Defines debug tag for this class.
     */
    private static final String DEBUG_TAG = HighScoreScreen.class.getName();

    public HighScoreScreen(CoreSetup host) {
        super(new ScreenViewport(), host);
    }
}