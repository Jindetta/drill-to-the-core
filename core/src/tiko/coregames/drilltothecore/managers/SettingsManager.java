package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import tiko.coregames.drilltothecore.utilities.Debugger;

public class SettingsManager implements Debugger {
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

    @Override
    public String getDebugTag() {
        return SettingsManager.class.getSimpleName();
    }
}