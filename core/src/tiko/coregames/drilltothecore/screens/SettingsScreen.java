package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

/**
 * SettingsScreen class will display all settings.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SettingsScreen extends BaseScreen {
    private Table settingsTable;

    public SettingsScreen() {
        settingsTable = new Table();

        TextButton calibration = new TextButton("Calibrate", skin);
        calibration.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Setup.nextScreen(new CalibrationScreen());
            }
        });

        settingsTable.add(calibration);

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

        settingsTable.setPosition(centerX, centerY);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        Gdx.input.setCatchBackKey(false);
        super.hide();
    }
}