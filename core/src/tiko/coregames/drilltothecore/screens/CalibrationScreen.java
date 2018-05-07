package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

/**
 * CalibrationScreen class will display calibration settings.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class CalibrationScreen extends BaseScreen {
    private Image background;
    private Table settingsTable;

    public CalibrationScreen() {
        assets.load("images/settings-background.png", Texture.class);
        assets.finishLoadingAsset("images/settings-background.png");

        background = new Image(assets.get("images/settings-background.png", Texture.class));
        addActor(background);

        final Label sensitivityLabelLeft = new Label("", skin);
        final Slider sensitivityLeft = new Slider(1, 10, 1, false, skin);
        sensitivityLeft.setValue(settings.getIntegerIfExists("sensitivityLeft", 1));
        sensitivityLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelLeft.setText(localization.getFormatted("sensitivityLeft", sensitivityLeft.getValue()));
                settings.setIntegerValue("sensitivityLeft", Math.round(sensitivityLeft.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityLeft.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelRight = new Label("", skin);
        final Slider sensitivityRight = new Slider(1, 10, 1, false, skin);
        sensitivityRight.setValue(settings.getIntegerIfExists("sensitivityRight", 1));
        sensitivityRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelRight.setText(localization.getFormatted("sensitivityRight", sensitivityRight.getValue()));
                settings.setIntegerValue("sensitivityRight", Math.round(sensitivityRight.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityRight.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelUp = new Label("", skin);
        final Slider sensitivityUp = new Slider(1, 10, 1, false, skin);
        sensitivityUp.setValue(settings.getIntegerIfExists("sensitivityUp", 1));
        sensitivityUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelUp.setText(localization.getFormatted("sensitivityUp", sensitivityUp.getValue()));
                settings.setIntegerValue("sensitivityUp", Math.round(sensitivityUp.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityUp.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelDown = new Label("", skin);
        final Slider sensitivityDown = new Slider(1, 10, 1, false, skin);
        sensitivityDown.setValue(settings.getIntegerIfExists("sensitivityDown", 1));
        sensitivityDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelDown.setText(localization.getFormatted("sensitivityDown", sensitivityDown.getValue()));
                settings.setIntegerValue("sensitivityDown", Math.round(sensitivityDown.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityDown.fire(new ChangeListener.ChangeEvent());

        final CheckBox invertedX = new CheckBox(localization.getValue("invertX"), skin);
        invertedX.setChecked(settings.getBooleanIfExists("invertedX", false));
        invertedX.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedX", invertedX.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox invertedY = new CheckBox(localization.getValue("invertY"), skin);
        invertedY.setChecked(settings.getBooleanIfExists("invertedY", false));
        invertedY.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedY", invertedY.isChecked());
                settings.saveSettings();
            }
        });

        settingsTable = new Table();
        settingsTable.add(sensitivityLabelLeft).row();
        settingsTable.add(sensitivityLeft).padTop(5).row();
        settingsTable.add(sensitivityLabelRight).padTop(10).row();
        settingsTable.add(sensitivityRight).padTop(5).row();

        settingsTable.add(sensitivityLabelUp).padTop(15).row();
        settingsTable.add(sensitivityUp).padTop(5).row();
        settingsTable.add(sensitivityLabelDown).padTop(10).row();
        settingsTable.add(sensitivityDown).padTop(5).row();

        settingsTable.add(invertedX).padTop(15).row();
        settingsTable.add(invertedY).padTop(5).row();

        addActor(settingsTable);
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            Setup.nextScreen(new SettingsScreen());
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
}