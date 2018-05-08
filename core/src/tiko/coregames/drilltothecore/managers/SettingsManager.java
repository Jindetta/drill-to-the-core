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
    /**
     * Stores instance of Preferences.
     */
    private Preferences preferences;

    /**
     * Gets formatted save name.
     *
     * @param value extension
     * @return complete file name
     */
    private static String getSaveName(String value) {
        return String.format(Locale.ENGLISH, "DrillToTheCore.%s", value);
    }

    /**
     * Gets profile identifier key.
     *
     * @param index profile index
     * @return profile identifier
     */
    private static String getProfileKey(int index) {
        return String.format(Locale.ENGLISH, "profile_%d", index);
    }

    /**
     * Gets profile file extension.
     *
     * @param index profile index
     * @return complete file name
     */
    private static String getProfilePath(int index) {
        return getSaveName(getProfileKey(index));
    }

    private SettingsManager() {
        this(getSaveName("profiles"), false);
    }

    /**
     * Overloads default constructor.
     *
     * @param fileName          file path
     * @param defaultProfile    default profile type
     */
    private SettingsManager(String fileName, boolean defaultProfile) {
        preferences = Gdx.app.getPreferences(fileName);

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

    /**
     * Checks if a profile exists.
     *
     * @param index profile index
     * @return true if profile exists, otherwise false
     */
    private static boolean hasProfile(int index) {
        SettingsManager profiles = new SettingsManager();

        return profiles.hasValue(getProfileKey(index));
    }

    /**
     * Gets profile name by index.
     *
     * @param index profile index
     * @return profile name
     */
    public static String getProfileName(int index) {
        if (hasProfile(index)) {
            SettingsManager profiles = new SettingsManager();

            return profiles.getString(getProfileKey(index));
        }

        return null;
    }

    /**
     * Gets default profile.
     *
     * @return profile instance
     */
    public static SettingsManager getDefaultProfile() {
        return new SettingsManager(getSaveName("defaultProfile"), true);
    }

    /**
     * Gets user profile by index.
     *
     * @param index             profile index
     * @param defaultProfile    fallback to defaut
     * @return instance to profile
     */
    public static SettingsManager getUserProfile(int index, boolean defaultProfile) {
        if (hasProfile(index)) {
            return new SettingsManager(getProfilePath(index), false);
        }

        return defaultProfile ? getDefaultProfile() : null;
    }

    /**
     * Gets currently active profile.
     *
     * @param defaultProfile    fallback to default
     * @return instance to profile
     */
    public static SettingsManager getActiveProfile(boolean defaultProfile) {
        SettingsManager profile = getDefaultProfile();

        return getUserProfile(profile.getIntegerIfExists("activeProfile", -1), defaultProfile);
    }

    /**
     * Sets active profile.
     *
     * @param index profile index
     */
    public static void setActiveProfile(int index) {
        SettingsManager profile = getDefaultProfile();

        if (hasProfile(index)) {
            profile.setIntegerValue("activeProfile", index);
        } else {
            profile.removeKey("activeProfile");
        }

        profile.saveSettings();
    }

    /**
     * Creates a new user profile.
     *
     * @param name      profile name
     * @param setActive set active
     * @return index to newly created profile
     */
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

    /**
     * Gets list of profile names.
     *
     * @return list of profile names
     */
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

    /**
     * Check if settings key exists.
     *
     * @param key identifier
     * @return true if exists, otherwise false
     */
    public boolean hasValue(String key) {
        return preferences.contains(key);
    }

    /**
     * Sets string value.
     *
     * @param key   identifier
     * @param value string value
     */
    public void setStringValue(String key, String value) {
        preferences.putString(key, value);
    }

    /**
     * Sets boolean value.
     *
     * @param key   identifier
     * @param value boolean value
     */
    public void setBooleanValue(String key, boolean value) {
        preferences.putBoolean(key, value);
    }

    /**
     * Sets integer value.
     *
     * @param key   identifier
     * @param value integer value
     */
    public void setIntegerValue(String key, int value) {
        preferences.putInteger(key, value);
    }

    /**
     * Gets integer value.
     *
     * @param key identifier
     * @return integer value
     */
    public int getInteger(String key) {
        return preferences.getInteger(key);
    }

    /**
     * Gets integer value if it exists.
     *
     * @param key           identifier
     * @param defaultValue  fallback value
     * @return integer value
     */
    public int getIntegerIfExists(String key, int defaultValue) {
        if (hasValue(key)) {
            return getInteger(key);
        }

        return defaultValue;
    }

    /**
     * Gets boolean value.
     *
     * @param key identifier
     * @return boolean value
     */
    public boolean getBoolean(String key) {
        return preferences.getBoolean(key);
    }

    /**
     * Gets boolean value if it exists.
     *
     * @param key           identifier
     * @param defaultValue  fallback value
     * @return boolean value
     */
    public boolean getBooleanIfExists(String key, boolean defaultValue) {
        if (hasValue(key)) {
            return getBoolean(key);
        }

        return defaultValue;
    }

    /**
     * Gets string value.
     *
     * @param key identifier
     * @return string value
     */
    public String getString(String key) {
        return preferences.getString(key);
    }

    /**
     * Gets string value if it exists.
     *
     * @param key           identifier
     * @param defaultValue  fallback value
     * @return string value
     */
    public String getStringIfExists(String key, String defaultValue) {
        if (hasValue(key)) {
            return getString(key);
        }

        return defaultValue;
    }

    /**
     * Sets current locale.
     *
     * @param localeKey locale identifier
     */
    public void setCurrentLocale(String localeKey) {
        Locale locale = getLocaleById(localeKey);
        setStringValue("locale", locale.getLanguage());
    }

    /**
     * Gets current locale.
     *
     * @return locale identifier
     */
    public String getLocaleString() {
        return getString("locale");
    }

    /**
     * Gets locale by identifier.
     *
     * @param localeKey identifier
     * @return instance of Locale
     */
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

    /**
     * Removes setting value.
     *
     * @param key identifier
     */
    public void removeKey(String key) {
        preferences.remove(key);
    }

    /**
     * Resets settings values.
     */
    public void resetProfile() {
        preferences.clear();
        saveSettings();
    }

    /**
     * Saves settings.
     */
    public void saveSettings() {
        preferences.flush();
    }
}