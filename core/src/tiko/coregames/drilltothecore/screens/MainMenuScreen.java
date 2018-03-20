package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LocalizationManager;

public class MainMenuScreen extends BaseScreen {
    private Table gameMenu;

    public MainMenuScreen() {
        LocalizationManager menu = new LocalizationManager("menu");

        TextButton play = new TextButton(menu.getValue("play"), skin);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CoreSetup.nextScreen(new GameScreen());
            }
        });

        TextButton settings = new TextButton(menu.getValue("settings"), skin);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CoreSetup.nextScreen(new ConfigScreen());
            }
        });

        TextButton exit = new TextButton(menu.getValue("exit"), skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        gameMenu = new Table();
        gameMenu.add(play).row();
        gameMenu.add(settings).padTop(15).row();
        gameMenu.add(exit).padTop(15);

        addActor(gameMenu);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float centerX = (viewport.getWorldWidth() - gameMenu.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - gameMenu.getHeight()) / 2;

        gameMenu.setPosition(centerX, centerY);
    }
}