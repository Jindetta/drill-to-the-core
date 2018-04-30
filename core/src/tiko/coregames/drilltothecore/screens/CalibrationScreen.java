package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
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
    private Table settingsTable;

    public CalibrationScreen() {
        final Label sensitivityLabelLeft = new Label("", skin);
        final Slider sensitivityLeft = new Slider(1, 10, 1, false, skin);
        sensitivityLeft.setValue(settings.getInteger("sensitivityLeft"));
        sensitivityLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelLeft.setText(String.format("Sensitivity (Left): %.0f", sensitivityLeft.getValue()));
                settings.setIntegerValue("sensitivityLeft", Math.round(sensitivityLeft.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityLeft.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelRight = new Label("", skin);
        final Slider sensitivityRight = new Slider(1, 10, 1, false, skin);
        sensitivityRight.setValue(settings.getInteger("sensitivityRight"));
        sensitivityRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelRight.setText(String.format("Sensitivity (Right): %.0f", sensitivityRight.getValue()));
                settings.setIntegerValue("sensitivityRight", Math.round(sensitivityRight.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityRight.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelUp = new Label("", skin);
        final Slider sensitivityUp = new Slider(1, 10, 1, false, skin);
        sensitivityUp.setValue(settings.getInteger("sensitivityUp"));
        sensitivityUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelUp.setText(String.format("Sensitivity (Up): %.0f", sensitivityUp.getValue()));
                settings.setIntegerValue("sensitivityUp", Math.round(sensitivityUp.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityUp.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelDown = new Label("", skin);
        final Slider sensitivityDown = new Slider(1, 10, 1, false, skin);
        sensitivityDown.setValue(settings.getInteger("sensitivityDown"));
        sensitivityDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelDown.setText(String.format("Sensitivity (Down): %.0f", sensitivityDown.getValue()));
                settings.setIntegerValue("sensitivityDown", Math.round(sensitivityDown.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityDown.fire(new ChangeListener.ChangeEvent());

        final CheckBox invertedX = new CheckBox(" Invert X-axis", skin, "checkbox5");
        invertedX.setChecked(settings.getBoolean("invertedX"));
        invertedX.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedX", invertedX.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox invertedY = new CheckBox(" Invert Y-axis", skin, "checkbox5");
        invertedY.setChecked(settings.getBoolean("invertedY"));
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

        settingsTable.setPosition(centerX, centerY);
    }
}