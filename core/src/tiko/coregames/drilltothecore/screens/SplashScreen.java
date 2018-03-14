package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class SplashScreen extends BaseScreen {
    private Texture splash1, splash2;
    private Array<Image> splash;
    private float timeLeft;

    public SplashScreen() {
        super(new ScreenViewport());

        timeLeft = INTRO_DURATION;
        splash = new Array<>();

        splash1 = new Texture("images/splash.jpg");
        splash.add(new Image(splash1));

        splash2 = new Texture("images/player.png");
        splash.add(new Image(splash2));
    }

    @Override
    public void resize(int width, int height) {
        for (Image image : splash) {
            image.setPosition((width - image.getPrefWidth()) / 2, (height - image.getPrefHeight()) / 2);
        }

        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        timeLeft -= delta;

        if (timeLeft <= 0) {
            timeLeft = INTRO_DURATION;
            splash.removeIndex(0);
            stage.clear();

            if (splash.size <= 0) {
                CoreSetup.nextScreen(new MainMenuScreen());
                return;
            }
        }

        stage.addActor(splash.first());
        super.render(delta);
    }

    @Override
    public void dispose() {
        splash1.dispose();
        splash2.dispose();
        super.dispose();
    }
}
