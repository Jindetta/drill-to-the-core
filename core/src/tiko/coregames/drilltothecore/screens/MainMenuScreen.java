package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LocalizationManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class MainMenuScreen extends BaseScreen {
    private Table gameMenu;

    public MainMenuScreen() {
        LocalizationManager menu = new LocalizationManager("menu");

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "play":
                        CoreSetup.nextScreen(new GameScreen());
                        break;
                    case "settings":
                        CoreSetup.nextScreen(new ConfigScreen());
                        break;
                    case "highScore":
                        CoreSetup.nextScreen(new HighScoreScreen());
                        break;
                    default:
                        Gdx.app.exit();
                        break;
                }
            }
        };

        TextButton play = new TextButton(menu.getValue("play"), skin);
        play.addListener(clickListener);
        play.setName("play");

        TextButton settings = new TextButton(menu.getValue("settings"), skin);
        settings.addListener(clickListener);
        settings.setName("settings");

        TextButton highScore = new TextButton(menu.getValue("highScore"), skin);
        highScore.addListener(clickListener);
        highScore.setName("highScore");

        TextButton exit = new TextButton(menu.getValue("exit"), skin);
        exit.addListener(clickListener);

        gameMenu = new Table();
        gameMenu.add(play).row();
        gameMenu.add(settings).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(highScore).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(exit).padTop(MENU_PADDING_TOP);

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