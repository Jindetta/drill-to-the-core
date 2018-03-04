package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private Preferences preferences;

    public SettingsManager() {
        preferences = Gdx.app.getPreferences("DrillToTheCore.prefs");
    }

    public void setValue(String key, String value) {
        preferences.putString(key, value);
    }

    public int getInteger(String key) {
        return preferences.getInteger(key);
    }

    public float getFloat(String key) {
        return preferences.getFloat(key);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key);
    }

    public String getString(String key) {
        return preferences.getString(key);
    }

    public void saveSettings() {
        preferences.flush();
    }
}