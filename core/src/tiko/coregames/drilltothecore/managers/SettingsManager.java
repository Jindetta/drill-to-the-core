package tiko.coregames.drilltothecore.managers;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * SettingsManager class will manage all settings and profiles.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
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

        if (defaultProfile) {
            setIntegerValue("sensitivityUp", getIntegerIfExists("sensitivityUp", 1));
            setIntegerValue("sensitivityDown", getIntegerIfExists("sensitivityDown", 1));
            setIntegerValue("sensitivityRight", getIntegerIfExists("sensitivityRight", 1));
            setIntegerValue("sensitivityLeft", getIntegerIfExists("sensitivityLeft", 1));
            setBooleanValue("invertedX", getBooleanIfExists("invertedX", false));
            setBooleanValue("invertedY", getBooleanIfExists("invertedY", false));

            setIntegerValue("soundVolume", getIntegerIfExists("soundVolume", 50));
            setIntegerValue("musicVolume", getIntegerIfExists("musicVolume", 50));
            setBooleanValue("soundMuted", getBooleanIfExists("soundMuted", false));
            setBooleanValue("musicMuted", getBooleanIfExists("musicMuted", false));

            setIntegerValue("playerColor", getIntegerIfExists("playerColor", 0));
        } else {
            SettingsManager profile = getDefaultProfile();

            setIntegerValue("sensitivityUp", getIntegerIfExists("sensitivityUp", profile.getIntegerIfExists("sensitivityUp", 1)));
            setIntegerValue("sensitivityDown", getIntegerIfExists("sensitivityDown", profile.getIntegerIfExists("sensitivityDown", 1)));
            setIntegerValue("sensitivityRight", getIntegerIfExists("sensitivityRight", profile.getIntegerIfExists("sensitivityRight", 1)));
            setIntegerValue("sensitivityLeft", getIntegerIfExists("sensitivityLeft", profile.getIntegerIfExists("sensitivityLeft", 1)));
            setBooleanValue("invertedX", getBooleanIfExists("invertedX", profile.getBooleanIfExists("invertedX", false)));
            setBooleanValue("invertedY", getBooleanIfExists("invertedY", profile.getBooleanIfExists("invertedY", false)));

            setIntegerValue("soundVolume", getIntegerIfExists("soundVolume", profile.getIntegerIfExists("soundVolume", 50)));
            setIntegerValue("musicVolume", getIntegerIfExists("musicVolume", profile.getIntegerIfExists("musicVolume", 50)));
            setBooleanValue("soundMuted", getBooleanIfExists("soundMuted", profile.getBooleanIfExists("soundMuted", false)));
            setBooleanValue("musicMuted", getBooleanIfExists("musicMuted", profile.getBooleanIfExists("musicMuted", false)));

            setIntegerValue("playerColor", getIntegerIfExists("playerColor", profile.getIntegerIfExists("playerColor", 0)));
        }

        saveSettings();
    }

    private static boolean hasProfile(int index) {
        SettingsManager profiles = new SettingsManager();

        return profiles.hasValue(getProfileKey(index));
    }

    public static String getProfileName(int index) {
        if (hasProfile(index)) {
            SettingsManager profiles = new SettingsManager();

            return profiles.getString(getProfileKey(index));
        }

        return null;
    }

    public static SettingsManager getDefaultProfile() {
        return new SettingsManager(getSaveName("defaultProfile"), true);
    }

    public static SettingsManager getUserProfile(int index, boolean defaultProfile) {
        if (hasProfile(index)) {
            return new SettingsManager(getProfilePath(index), false);
        }

        return defaultProfile ? getDefaultProfile() : null;
    }

    public static SettingsManager getActiveProfile(boolean defaultProfile) {
        SettingsManager profile = getDefaultProfile();

        return getUserProfile(profile.getIntegerIfExists("activeProfile", -1), defaultProfile);
    }

    public static void setActiveProfile(int index) {
        SettingsManager profile = getDefaultProfile();

        if (hasProfile(index)) {
            profile.setIntegerValue("activeProfile", index);
        } else {
            profile.removeKey("activeProfile");
        }

        profile.saveSettings();
    }

    public static int createUserProfile(String name, boolean setActive) {
        if (name != null) {
            SettingsManager profiles = new SettingsManager();

            for (int i = 0; i < MAX_SAVED_PROFILES; i++) {
                if (!hasProfile(i) || name.equals(getProfileName(i))) {
                    profiles.setStringValue(getProfileKey(i), name);
                    profiles.saveSettings();

                    if (setActive) {
                        setActiveProfile(i);
                    }

                    return i;
                }
            }
        }

        return -1;
    }

    public static Array<String> getProfileNames() {
        Array<String> profiles = new Array<>();

        // Default profile
        profiles.add("");

        for (int i = 0; i < MAX_SAVED_PROFILES; i++) {
            String name = getProfileName(i);

            if (name != null) {
                profiles.add(name.toUpperCase());
            }
        }

        return profiles;
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

    public void setIntegerValue(String key, int value) {
        preferences.putInteger(key, value);
    }

    public int getInteger(String key) {
        return preferences.getInteger(key);
    }

    public int getIntegerIfExists(String key, int defaultValue) {
        if (hasValue(key)) {
            return getInteger(key);
        }

        return defaultValue;
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key);
    }

    public boolean getBooleanIfExists(String key, boolean defaultValue) {
        if (hasValue(key)) {
            return getBoolean(key);
        }

        return defaultValue;
    }

    public String getString(String key) {
        return preferences.getString(key);
    }

    public String getStringIfExists(String key, String defaultValue) {
        if (hasValue(key)) {
            return getString(key);
        }

        return defaultValue;
    }

    public void setCurrentLocale(String localeKey) {
        Locale locale = getLocaleById(localeKey);
        setStringValue("locale", locale.getLanguage());
    }

    public String getLocaleString() {
        return getString("locale");
    }

    public Locale getLocaleById(String localeKey) {
        Locale locale;

        if (localeKey == null) {
            locale = Locale.getDefault();
        } else {
            if (localeKey.equals("fi")) {
                locale = new Locale("fi", "FI");
            } else {
                locale = new Locale("en", "US");
            }
        }

        return locale;
    }

    public void removeKey(String key) {
        preferences.remove(key);
    }

    public void resetProfile() {
        preferences.clear();
        saveSettings();
    }

    public void saveSettings() {
        preferences.flush();
    }
}