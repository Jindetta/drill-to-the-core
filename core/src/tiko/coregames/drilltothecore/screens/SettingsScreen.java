package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * SettingsScreen class will display all settings.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SettingsScreen extends BaseScreen {
    /**
     * Defines background image.
     */
    private Image background;

    /**
     * Defines screen layout.
     */
    private Table screenLayout;

    /**
     * Defines buttons for player colors.
     */
    private ImageButton[] buttons;

    public SettingsScreen() {
        assets.load("images/settings-background.png", Texture.class);
        assets.load("images/player-atlas.png", Texture.class);
        assets.finishLoading();

        background = new Image(assets.get("images/settings-background.png", Texture.class));
        addActor(background);

        screenLayout = new Table();
        screenLayout.defaults().expand().uniform();

        ImageButton calibration = new ImageButton(skin, localization.getValue("calibrate"));
        calibration.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Setup.nextScreen(new CalibrationScreen());
            }
        });

        Table volumeControls = new Table();
        volumeControls.columnDefaults(2);

        final Label soundVolumeLabel = new Label("", skin);
        final Slider soundVolume = new Slider(0, 100, 5, false, skin);
        soundVolume.setValue(settings.getIntegerIfExists("soundVolume", 1));
        soundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolumeLabel.setText(String.format(localization.getValue("soundVolumeTitle"), soundVolume.getValue()));
                settings.setIntegerValue("soundVolume", Math.round(soundVolume.getValue()));
                settings.saveSettings();
            }
        });
        soundVolume.fire(new ChangeListener.ChangeEvent());

        final Label musicVolumeLabel = new Label("", skin);
        final Slider musicVolume = new Slider(0, 100, 5, false, skin);
        musicVolume.setValue(settings.getIntegerIfExists("musicVolume", 1));
        musicVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolumeLabel.setText(String.format(localization.getValue("musicVolumeTitle"), musicVolume.getValue()));
                settings.setIntegerValue("musicVolume", Math.round(musicVolume.getValue()));
                settings.saveSettings();
            }
        });
        musicVolume.fire(new ChangeListener.ChangeEvent());

        final CheckBox muteSounds = new CheckBox("", skin, "checkbox_sound2");
        muteSounds.setChecked(settings.getBooleanIfExists("soundMuted", false));
        muteSounds.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("soundMuted", muteSounds.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox muteMusic = new CheckBox("", skin, "checkbox_music2");
        muteMusic.setChecked(settings.getBooleanIfExists("musicMuted", false));
        muteMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("musicMuted", muteMusic.isChecked());
                settings.saveSettings();
            }
        });

        volumeControls.add(soundVolumeLabel).pad(MENU_DEFAULT_PADDING).colspan(2).row();
        volumeControls.add(soundVolume).pad(MENU_DEFAULT_PADDING).right();
        volumeControls.add(muteSounds).pad(MENU_DEFAULT_PADDING).left().row();
        volumeControls.add(musicVolumeLabel).pad(MENU_DEFAULT_PADDING).colspan(2).row();
        volumeControls.add(musicVolume).pad(MENU_DEFAULT_PADDING).right();
        volumeControls.add(muteMusic).pad(MENU_DEFAULT_PADDING).left();

        Table playerColor = new Table();
        Texture playerImage = assets.get("images/player-atlas.png");
        buttons = new ImageButton[playerImage.getHeight() / BIG_TILE_SIZE];

        for (int i = 0; i < buttons.length; i++) {
            final int colorIndex = i;
            TextureRegion region = new TextureRegion(playerImage, BIG_TILE_SIZE * 4, i * BIG_TILE_SIZE, BIG_TILE_SIZE, BIG_TILE_SIZE);
            buttons[i] = new ImageButton(new TextureRegionDrawable(region));
            buttons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    settings.setIntegerValue("playerColor", colorIndex);
                    settings.saveSettings();
                }
            });
            playerColor.add(buttons[i]).pad(MENU_DEFAULT_PADDING);
        }

        Table extraMenu = new Table();

        extraMenu.add(calibration).row();
        extraMenu.add(playerColor).pad(MENU_DEFAULT_PADDING * 2);

        screenLayout.add(new ImageButton(skin, localization.getValue("settingsTitle"))).colspan(2).row();
        screenLayout.add(volumeControls);
        screenLayout.add(extraMenu).row();

        ImageButton backButton = new ImageButton(skin, localization.getValue("backButton"));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsScreen.this.keyDown(Input.Keys.BACK);
            }
        });

        screenLayout.add(backButton).align(Align.bottomLeft).pad(SAFE_ZONE_SIZE).colspan(2);

        addActor(screenLayout);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        screenLayout.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        screenLayout.setPosition(0, 0);
    }

    @Override
    public void render(float delta) {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setSize(BIG_TILE_SIZE, BIG_TILE_SIZE);

            if (settings.hasValue("playerColor") && i == settings.getInteger("playerColor")) {
                buttons[i].setSize(BIG_TILE_SIZE, BIG_TILE_SIZE * 1.5f);
            }
        }

        super.render(delta);
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            Setup.nextScreen(new MainMenuScreen());
        }

        return super.keyDown(key);
    }
}