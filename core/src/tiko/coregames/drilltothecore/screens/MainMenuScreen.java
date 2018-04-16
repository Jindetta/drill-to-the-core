package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * MainMenuScreen class will display main menu.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class MainMenuScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Image background;
    private Table gameMenu, quickMenu;

    public MainMenuScreen() {
        backgroundTexture = new Texture("images/menu-background.png");

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "play":
                        Setup.nextScreen(new GameScreen());
                        break;
                    case "settings":
                        Setup.nextScreen(new SettingsScreen());
                        break;
                    case "highScore":
                        Setup.nextScreen(new HighScoreScreen());
                        break;
                    default:
                        Gdx.app.exit();
                        break;
                }
            }
        };

        TextButton play = new TextButton(coreLocalization.getValue("menu_play"), skin);
        play.addListener(clickListener);
        play.setName("play");

        TextButton settings = new TextButton(coreLocalization.getValue("menu_settings"), skin);
        settings.addListener(clickListener);
        settings.setName("settings");

        TextButton highScore = new TextButton(coreLocalization.getValue("menu_highScore"), skin);
        highScore.addListener(clickListener);
        highScore.setName("highScore");

        TextButton exit = new TextButton(coreLocalization.getValue("menu_exit"), skin);
        exit.addListener(clickListener);

        background = new Image(backgroundTexture);
        addActor(background);

        gameMenu = new Table();
        gameMenu.add(play).row();
        gameMenu.add(settings).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(highScore).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(exit).padTop(MENU_PADDING_TOP);

        addActor(gameMenu);

        quickMenu = new Table();

        CheckBox muteSounds = new CheckBox("", skin, "Sound");
        CheckBox muteMusic = new CheckBox("", skin, "music");
        Button languageSelection = new Button(skin);
        SelectBox<String> profiles = new SelectBox<>(skin);

        profiles.setItems("default");

        quickMenu.add(muteSounds);
        quickMenu.add(muteMusic);
        quickMenu.add(languageSelection);
        quickMenu.add(profiles);
        quickMenu.debug();

        addActor(quickMenu);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float centerX = (viewport.getWorldWidth() - gameMenu.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - gameMenu.getHeight()) / 2;

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        gameMenu.setPosition(centerX, centerY);

        quickMenu.setPosition(centerX, SAFE_ZONE_SIZE * 4);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        super.dispose();
    }
}