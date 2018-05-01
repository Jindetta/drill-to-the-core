package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SoundManager;

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

    private SoundManager sounds;

    public MainMenuScreen() {
        sounds = new SoundManager(settings);
        backgroundTexture = new Texture("images/menu-background.png");

        sounds.playMusic("sounds/background-music.mp3", true);

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "play":
                        Setup.nextScreen(new LevelSelectScreen());
                        break;
                    case "settings":
                        Setup.nextScreen(new SettingsScreen());
                        break;
                    case "highScore":
                        Setup.nextScreen(new HighScoreScreen());
                        break;
                    case "profiles":
                        break;
                    default:
                        Gdx.app.exit();
                        break;
                }
            }
        };

        ImageButton playButton = new ImageButton(skin, coreLocalization.getValue("play"));
        playButton.addListener(clickListener);
        playButton.setName("play");

        final ImageButton settingsButton = new ImageButton(skin, coreLocalization.getValue("settings"));
        settingsButton.addListener(clickListener);
        settingsButton.setName("settings");

        ImageButton highScore = new ImageButton(skin, coreLocalization.getValue("scores"));
        highScore.addListener(clickListener);
        highScore.setName("highScore");

        ImageButton exit = new ImageButton(skin, coreLocalization.getValue("quit"));
        exit.addListener(clickListener);

        background = new Image(backgroundTexture);
        addActor(background);

        gameMenu = new Table();
        gameMenu.add(playButton).row();
        gameMenu.add(settingsButton).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(highScore).padTop(MENU_PADDING_TOP).row();
        gameMenu.add(exit).padTop(MENU_PADDING_TOP);

        addActor(gameMenu);

        quickMenu = new Table();

        CheckBox muteSounds = new CheckBox("", skin, "checkbox5");

        final CheckBox muteMusic = new CheckBox("", skin, "checkbox3");
        muteMusic.setChecked(settings.getBooleanIfExists("musicMuted", false));
        muteMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("musicMuted", muteMusic.isChecked());
                sounds.muteMusic(muteMusic.isChecked());
                settings.saveSettings();
            }
        });
        final Button languageSelection = new Button(skin, coreLocalization.getValue("language"));
        languageSelection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setCurrentLocale(coreLocalization.getValue("swappedLocale"));
                settings.saveSettings();

                Setup.nextScreen(new MainMenuScreen());
            }
        });

        /*final SelectBox<String> profiles = new SelectBox<>(skin);
        profiles.setItems(SettingsManager.getProfileNames());
        Button addProfile = new Button(skin);
        addProfile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setOnscreenKeyboardVisible(true);
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        SettingsManager.createUserProfile(text, true);
                        profiles.setItems(SettingsManager.getProfileNames());
                    }

                    @Override
                    public void canceled() {

                    }
                }, "New profile", "", "Profile name");
            }
        });*/

        quickMenu.add(muteSounds);
        quickMenu.add(muteMusic);
        quickMenu.add(languageSelection);
        //quickMenu.add(profiles);
        //quickMenu.add(addProfile);
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
        sounds.dispose();
        super.dispose();
    }
}