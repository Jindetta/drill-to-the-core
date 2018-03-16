package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private Preferences preferences;

    private SettingsManager(String fileName) {
        // Private constructor
        preferences = Gdx.app.getPreferences(fileName);
    }

    public static SettingsManager getSettings() {
        return new SettingsManager("DrillToTheCore.settings");
    }

    public static SettingsManager getUserProfiles() {
        return new SettingsManager("DrillToTheCore.profiles");
    }

    public void setStringValue(String key, String value) {
        preferences.putString(key, value);
    }

    public void setBooleanValue(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    public void setFloatValue(String key, float value) {
        preferences.putFloat(key, value);
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