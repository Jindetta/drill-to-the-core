package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class SplashScreen extends BaseScreen {
    private Image splash;
    private float timeLeft;

    public SplashScreen() {
        super(new ScreenViewport());

        timeLeft = INTRO_DURATION;
        splash = new Image(new Texture("images/splash.jpg"));
        stage.addActor(splash);
    }

    @Override
    public void resize(int width, int height) {
        splash.setPosition((width - splash.getPrefWidth()) / 2, (height - splash.getPrefHeight()) / 2);
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        float alpha = splash.getColor().a;

        if (alpha <= 0) {
            CoreSetup.nextScreen(new MainMenuScreen());
        } else {
            timeLeft -= delta;

            if (timeLeft <= 0) {
                alpha = Math.max(alpha - delta / INTRO_FADE_DURATION, 0);
                splash.setColor(1, 1, 1, alpha);
            }

            super.render(delta);
        }
    }
}
