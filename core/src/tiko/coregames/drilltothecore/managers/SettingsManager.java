package tiko.coregames.drilltothecore.managers;

import java.util.Locale;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class SettingsManager {
    private boolean defaultProfile;
    private Preferences preferences;

    private static String getSaveName(String value) {
        return String.format(Locale.ENGLISH, "DrillToTheCore.%s", value);
    }

    private static String getProfileKey(int index) {
        return String.format(Locale.ENGLISH, "profile_%d", index);
    }

    private static String getProfilePath(int index) {
        return getSaveName(getProfileKey(index));
    }

    private SettingsManager() {
        this(getSaveName("profiles"), false);
    }

    private SettingsManager(String fileName, boolean defaultProfile) {
        preferences = Gdx.app.getPreferences(fileName);
        this.defaultProfile = defaultProfile;
    }

    private static boolean hasProfile(int index) {
        SettingsManager profiles = new SettingsManager();

        return profiles.hasValue(getProfileKey(index));
    }

    public static SettingsManager getDefaultProfile() {
        return new SettingsManager(getSaveName("defaultProfile"), true);
    }

    public static SettingsManager getUserProfile(int index) {
        if (hasProfile(index)) {
            return new SettingsManager(getProfilePath(index), false);
        }

        return getDefaultProfile();
    }

    public static SettingsManager getActiveProfile() {
        SettingsManager profile = getDefaultProfile();

        if (profile.defaultProfile && profile.hasValue("activeProfile")) {
            return getUserProfile(profile.getInteger("activeProfile"));
        }

        return null;
    }

    public static void setActiveProfile(int index) {
        if (hasProfile(index)) {
            SettingsManager profile = getDefaultProfile();

            profile.setIntegerValue("activeProfile", index);
            profile.saveSettings();
        }
    }

    public static int createUserProfile(String name, boolean setActive) {
        if (name != null) {
            SettingsManager profiles = new SettingsManager();

            for (int i = 0; i < MAX_SAVED_PROFILES; i++) {
                if (!hasProfile(i)) {
                    if (setActive) {
                        setActiveProfile(i);
                    }

                    profiles.setStringValue(getProfileKey(i), name);
                    profiles.saveSettings();

                    return i;
                }
            }
        }

        return -1;
    }

    public boolean hasValue(String key) {
        return preferences.contains(key);
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

    public void setIntegerValue(String key, int value) {
        preferences.putInteger(key, value);
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

    public void resetProfile() {
        preferences.clear();
        saveSettings();
    }

    public void saveSettings() {
        preferences.flush();
    }
}