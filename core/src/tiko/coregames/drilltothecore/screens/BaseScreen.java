package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import tiko.coregames.drilltothecore.managers.LocalizationManager;
import tiko.coregames.drilltothecore.managers.SettingsManager;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * BaseScreen class is core of every screen.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public abstract class BaseScreen extends Stage implements Screen {
    /**
     * Defines manager for localization.
     */
    LocalizationManager localization;

    /**
     * Defines manager for settings.
     */
    SettingsManager settings;

    /**
     * Defines manager for assets.
     */
    AssetManager assets;

    /**
     * Defines menu skin.
     */
    Skin skin;

    BaseScreen() {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));

        assets = new AssetManager();
        assets.load("ui/menu.json", Skin.class);
        assets.finishLoadingAsset("ui/menu.json");

        skin = assets.get("ui/menu.json");
        settings = SettingsManager.getActiveProfile(true);
        localization = new LocalizationManager();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setCatchBackKey(false);
        dispose();
    }

    @Override
    public void dispose() {
        assets.dispose();
        super.dispose();
    }
}