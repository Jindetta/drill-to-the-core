package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;
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
    /**
     * Defines background image.
     */
    private Image background;

    /**
     * Defines menu for options.
     */
    private Table gameMenu;

    /**
     * Defines menu for quick buttons.
     */
    private Table quickMenu;

    /**
     * Defines manager for sounds.
     */
    private SoundManager sounds;

    /**
     * Defines refresh state.
     */
    private boolean needsRefreshing;

    public MainMenuScreen() {
        needsRefreshing = false;

        sounds = new SoundManager(settings, assets);
        assets.load("images/menu-background.png", Texture.class);
        assets.finishLoadingAsset("images/menu-background.png");

        background = new Image(assets.get("images/menu-background.png", Texture.class));
        addActor(background);

        sounds.playMusic("sounds/background-music.mp3");

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "playMenu":
                        Setup.nextScreen(new LevelSelectScreen());
                        break;
                    case "settingsMenu":
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

        ImageButton playButton = new ImageButton(skin, localization.getValue("playMenu"));
        playButton.addListener(clickListener);
        playButton.setName("playMenu");

        final ImageButton settingsButton = new ImageButton(skin, localization.getValue("settingsMenu"));
        settingsButton.addListener(clickListener);
        settingsButton.setName("settingsMenu");

        ImageButton highScore = new ImageButton(skin, localization.getValue("scoresMenu"));
        highScore.addListener(clickListener);
        highScore.setName("highScore");

        ImageButton exit = new ImageButton(skin, localization.getValue("quitMenu"));
        exit.addListener(clickListener);

        gameMenu = new Table();
        gameMenu.add(playButton).row();
        gameMenu.add(settingsButton).padTop(MENU_DEFAULT_PADDING).row();
        gameMenu.add(highScore).padTop(MENU_DEFAULT_PADDING).row();
        gameMenu.add(exit).padTop(MENU_DEFAULT_PADDING);

        addActor(gameMenu);

        quickMenu = new Table();

        final CheckBox muteSounds = new CheckBox("", skin, "checkbox_sound2");
        muteSounds.setChecked(settings.getBooleanIfExists("soundMuted", false));
        muteSounds.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("soundMuted", muteSounds.isChecked());
                sounds.muteSounds(muteSounds.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox muteMusic = new CheckBox("", skin, "checkbox_music2");
        muteMusic.setChecked(settings.getBooleanIfExists("musicMuted", false));
        muteMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("musicMuted", muteMusic.isChecked());
                sounds.muteMusic(muteMusic.isChecked());
                settings.saveSettings();
            }
        });
        final Button languageSelection = new Button(skin, localization.getValue("language"));
        languageSelection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setCurrentLocale(localization.getValue("swappedLocale"));
                needsRefreshing = true;
            }
        });

        final SelectBox<String> profiles = new SelectBox<>(skin);
        profiles.setItems(SettingsManager.getProfileNames());

        int profileIndex = SettingsManager.getActiveProfile() + 1;
        if (profileIndex > 0) {
            profiles.setSelectedIndex(profileIndex);
        }

        profiles.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsManager.setActiveProfile(profiles.getSelectedIndex() - 1);
                needsRefreshing = true;
            }
        });

        final Input.TextInputListener listener = new Input.TextInputListener() {
            @Override
            public void input(String text) {
                if (text != null && !text.trim().equals("")) {
                    SettingsManager.createUserProfile(text, true);
                    profiles.setItems(SettingsManager.getProfileNames());
                    needsRefreshing = true;
                }
            }

            @Override
            public void canceled() {

            }
        };

        Button addProfile = new Button(skin, "icon1");
        addProfile.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.input.setOnscreenKeyboardVisible(true);
                Gdx.input.getTextInput(listener, localization.getValue("newProfile"), "", localization.getValue("profileName"));
            }
        });

        Table soundButtons = new Table();
        soundButtons.add(muteSounds);
        soundButtons.add(muteMusic);

        Table profileButtons = new Table();
        profileButtons.add(profiles).height(20).padRight(MENU_DEFAULT_PADDING);
        profileButtons.add(addProfile);

        quickMenu.defaults().uniform();
        quickMenu.add(soundButtons).left().padLeft(SAFE_ZONE_SIZE);
        quickMenu.add(languageSelection).expandX().center();
        quickMenu.add(profileButtons).right().padRight(SAFE_ZONE_SIZE);

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

        quickMenu.setWidth(viewport.getWorldWidth());
        quickMenu.setPosition(0, SAFE_ZONE_SIZE * 4);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (needsRefreshing) {
            needsRefreshing = false;
            Setup.nextScreen(new MainMenuScreen());
        }
    }
}