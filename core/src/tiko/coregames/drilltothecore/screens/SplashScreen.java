package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class SplashScreen extends BaseScreen {
    private Texture texture;
    private float timeLeft;
    private Image splash;

    public SplashScreen() {
        timeLeft = SINGLE_SPLASH_DURATION;
        texture = new Texture("images/splash.png");
        splash = new Image(texture);

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
            CoreSetup.nextScreen(new MainMenuScreen());
        }

        super.render(delta);
    }

    @Override
    public void dispose() {
        texture.dispose();
        super.dispose();
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
