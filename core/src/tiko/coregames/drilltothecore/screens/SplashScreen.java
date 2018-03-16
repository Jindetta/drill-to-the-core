package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class SplashScreen extends BaseScreen {
    private Texture currentTexture;
    private Image currentSplash;
    private int currentIndex;
    private float timeLeft;

    public SplashScreen() {
        super(new ScreenViewport());

        currentIndex = 0;
        setNextSplashScreen();
    }

    private boolean setNextSplashScreen() {
        if (currentTexture != null) {
            currentTexture.dispose();
            clear();
        }

        if (currentIndex < SPLASH_SCREENS.length) {
            currentTexture = new Texture(SPLASH_SCREENS[currentIndex++]);
            currentSplash = new Image(currentTexture);
            timeLeft = SINGLE_SPLASH_DURATION;

            centerSplashImage();
            addActor(currentSplash);

            return true;
        }

        return false;
    }

    private void centerSplashImage() {
        float centerX = (Gdx.graphics.getWidth() - currentSplash.getPrefWidth()) / 2;
        float centerY = (Gdx.graphics.getHeight() - currentSplash.getPrefHeight()) / 2;

        currentSplash.setPosition(centerX, centerY);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        centerSplashImage();
    }

    @Override
    public void render(float delta) {
        timeLeft -= delta;

        if (timeLeft <= 0 && !setNextSplashScreen()) {
            CoreSetup.nextScreen(new MainMenuScreen());
            return;
        }

        super.render(delta);
    }
}
