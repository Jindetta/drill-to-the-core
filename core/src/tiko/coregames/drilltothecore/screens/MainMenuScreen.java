package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

public class MainMenuScreen extends BaseScreen {
    public MainMenuScreen(CoreSetup host) {
        super(new ScreenViewport(), host);
    }

    @Override
    public String getDebugTag() {
        return MainMenuScreen.class.getSimpleName();
    }
}