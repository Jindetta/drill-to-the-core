package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class SplashScreen extends BaseScreen {
    private Image splash;
    private float timeLeft;

    public SplashScreen() {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));

        splash = new Image(new Texture("images/splash.jpg"));
        splash.setPosition((WORLD_WIDTH - splash.getPrefWidth()) / 2, (WORLD_HEIGHT - splash.getPrefHeight()) / 2);
        stage.addActor(splash);

        timeLeft = 3;
    }

    @Override
    public void render(float delta) {
        float alpha = splash.getColor().a;

        if (alpha <= 0) {
            CoreSetup.nextScreen(new MainMenuScreen());
        } else {
            timeLeft -= delta;

            if (timeLeft <= 0) {
                alpha = Math.max(alpha - delta, 0);
                splash.setColor(1, 1, 1, alpha);
            }

            super.render(delta);
        }
    }
}
