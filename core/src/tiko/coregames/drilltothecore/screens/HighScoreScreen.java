package tiko.coregames.drilltothecore.screens;

import tiko.coregames.drilltothecore.CoreSetup;

public class HighScoreScreen extends BaseScreen {
    @Override
    public void show() {
        CoreSetup.nextScreen(new MainMenuScreen());
    }
}