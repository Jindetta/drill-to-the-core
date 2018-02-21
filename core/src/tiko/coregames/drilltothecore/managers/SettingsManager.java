package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private Preferences preferences;

    public SettingsManager() {
        preferences = Gdx.app.getPreferences("DrillToTheCore.prefs");
    }
}