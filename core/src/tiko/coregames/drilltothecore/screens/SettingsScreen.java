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
    private Image background;
    private Texture backgroundTexture;
    private Table settingsTable;
    private Texture playerImage;

    private ImageButton[] buttons;

    public SettingsScreen() {
        backgroundTexture = new Texture("images/settings-background.png");

        background = new Image(backgroundTexture);
        addActor(background);

        settingsTable = new Table();
        playerImage = new Texture("images/player_atlas.png");

        ImageButton calibration = new ImageButton(skin, localization.getValue("calibrate"));
        calibration.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Setup.nextScreen(new CalibrationScreen());
            }
        });

        final Label soundVolumeLabel = new Label("", skin);
        final Slider soundVolume = new Slider(0, 100, 5, false, skin);
        soundVolume.setValue(settings.getIntegerIfExists("soundVolume", 1));
        soundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolumeLabel.setText(String.format("SOUND VOLUME: %.0f", soundVolume.getValue()));
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
                musicVolumeLabel.setText(String.format("MUSIC VOLUME: %.0f", musicVolume.getValue()));
                settings.setIntegerValue("musicVolume", Math.round(musicVolume.getValue()));
                settings.saveSettings();
            }
        });
        musicVolume.fire(new ChangeListener.ChangeEvent());

        settingsTable.add(soundVolumeLabel).row();
        settingsTable.add(soundVolume).row();
        settingsTable.add(musicVolumeLabel).row();
        settingsTable.add(musicVolume).row();

        settingsTable.add(calibration).center().colspan(10).row();
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
            settingsTable.add(buttons[i]).center().pad(15);
        }
        addActor(settingsTable);
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            Setup.nextScreen(new MainMenuScreen());
        }

        return super.keyDown(key);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float centerX = (viewport.getWorldWidth() - settingsTable.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - settingsTable.getHeight()) / 2;

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        settingsTable.setPosition(centerX, centerY);
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
    public void dispose() {
        backgroundTexture.dispose();
        playerImage.dispose();
        super.dispose();
    }
}