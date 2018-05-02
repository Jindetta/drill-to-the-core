package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class LocalizationManager {
    private I18NBundle bundle;

    public LocalizationManager() {
        bundle = I18NBundle.createBundle(Gdx.files.internal("localization/menu"), getProfileLocale());
    }

    private Locale getProfileLocale() {
        SettingsManager profile = SettingsManager.getActiveProfile(true);

        if (profile != null && profile.hasValue("locale")) {
            return profile.getLocaleById(profile.getLocaleString());
        }

        return Locale.getDefault();
    }

    public String getValue(String key) {
        return bundle.get(key);
    }
}