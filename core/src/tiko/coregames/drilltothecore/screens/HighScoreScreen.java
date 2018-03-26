package tiko.coregames.drilltothecore.screens;

import tiko.coregames.drilltothecore.Setup;

public class HighScoreScreen extends BaseScreen {
    @Override
    public void show() {
        Setup.nextScreen(new MainMenuScreen());
    }
}