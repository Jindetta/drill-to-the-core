package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

/**
 * LocalizationManager class will manage localization of the game.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class LocalizationManager {
    /**
     * Stores instance of localization bundle.
     */
    private I18NBundle bundle;

    public LocalizationManager() {
        bundle = I18NBundle.createBundle(Gdx.files.internal("localization/menu"), getProfileLocale());
    }

    /**
     * Gets locale from currently active profile.
     */
    private Locale getProfileLocale() {
        SettingsManager profile = SettingsManager.getActiveProfile(true);

        if (profile != null && profile.hasValue("locale")) {
            return profile.getLocaleById(profile.getLocaleString());
        }

        return Locale.getDefault();
    }

    /**
     * Gets locale value by identifier.
     *
     * @param key   locale identifier
     * @return  localized string
     */
    public String getValue(String key) {
        return bundle.get(key);
    }

    /**
     * Gets formatted locale value.
     *
     * @param key   locale identifier
     * @param args  list of arguments
     * @return formatted string
     */
    public String getFormatted(String key, Object... args) {
        return String.format(getValue(key), args);
    }
}