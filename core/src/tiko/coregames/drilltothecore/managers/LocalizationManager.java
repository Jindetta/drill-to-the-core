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
            // TODO: Add more bundles
            default:
                path.append("core");
                break;
        }

        bundle = I18NBundle.createBundle(Gdx.files.internal(path.toString()), Locale.getDefault());
    }

    public String getValue(String key) {
        return bundle.get(key);
    }
}