package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {
    private Preferences preferences;

    /**
     * Defines debug tag for this class.
     */
    private static final String DEBUG_TAG = SettingsManager.class.getName();

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