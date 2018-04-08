package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;

import java.util.Locale;

public class LocalizationManager {
    private I18NBundle bundle;

    public LocalizationManager(String bundleKey) {
        initializeBundle(bundleKey == null ? "" : bundleKey);
    }

    private void initializeBundle(String bundleKey) {
        StringBuilder path = new StringBuilder("localization/");

        switch (bundleKey) {
            case "menu":
            case "game":
                path.append(bundleKey);
                break;
            default:
                path.append("core");
                break;
        }

        bundle = I18NBundle.createBundle(Gdx.files.internal(path.toString()), getProfileLocale());
    }

    private Locale getProfileLocale() {
        SettingsManager profile = SettingsManager.getActiveProfile(true);

        if (profile != null && profile.hasValue("locale")) {
            return new Locale(profile.getString("locale"));
        }

        return Locale.getDefault();
    }

    public String getValue(String key) {
        return bundle.get(key);
    }
}