package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

public class HighScoreScreen extends BaseScreen {
    public HighScoreScreen(CoreSetup host) {
        super(new ScreenViewport(), host);
    }

    @Override
    public String getDebugTag() {
        return HighScoreScreen.class.getSimpleName();
    }
}