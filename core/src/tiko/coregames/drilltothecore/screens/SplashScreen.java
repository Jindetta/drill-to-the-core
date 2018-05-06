package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * SplashScreen class will display a splash screen.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SplashScreen extends BaseScreen {
    private float timeLeft;
    private Image splash;

    public SplashScreen() {
        timeLeft = SINGLE_SPLASH_DURATION;
        assets.load("images/splash.png", Texture.class);
        assets.finishLoadingAsset("images/splash.png");

        splash = new Image(assets.get("images/splash.png", Texture.class));
        splash.setSize(getWidth(), getHeight());
        addActor(splash);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float centerX = (viewport.getWorldWidth() - splash.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - splash.getHeight()) / 2;

        splash.setPosition(centerX, centerY);
    }

    @Override
    public void render(float delta) {
        timeLeft -= delta;

        if (timeLeft <= 0) {
            Setup.nextScreen(new MainMenuScreen());
        }

        super.render(delta);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public boolean keyDown(int keyCode) {
        timeLeft = 0;
        return true;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return keyDown(0);
    }
}