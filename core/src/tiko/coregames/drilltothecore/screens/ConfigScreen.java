package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

public class ConfigScreen extends BaseScreen {
    private Table settingsTable;

    public ConfigScreen() {
        final SettingsManager settings = SettingsManager.getDefaultProfile();

        final Label sensitivityLabelLeft = new Label("", skin);
        final Slider sensitivityLeft = new Slider(0, 10, 0.05f, false, skin);
        sensitivityLeft.setValue(settings.getFloat("sensitivityLeft"));
        sensitivityLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelLeft.setText(String.format("Sensitivity (Left): %.2f", sensitivityLeft.getValue()));
                settings.setFloatValue("sensitivityLeft", sensitivityLeft.getValue());
                settings.saveSettings();
            }
        });
        sensitivityLeft.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelRight = new Label("", skin);
        final Slider sensitivityRight = new Slider(0, 10, 0.05f, false, skin);
        sensitivityRight.setValue(settings.getFloat("sensitivityRight"));
        sensitivityRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelRight.setText(String.format("Sensitivity (Right): %.2f", sensitivityRight.getValue()));
                settings.setFloatValue("sensitivityRight", sensitivityRight.getValue());
                settings.saveSettings();
            }
        });
        sensitivityRight.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelUp = new Label("", skin);
        final Slider sensitivityUp = new Slider(0, 10, 0.05f, false, skin);
        sensitivityUp.setValue(settings.getFloat("sensitivityUp"));
        sensitivityUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelUp.setText(String.format("Sensitivity (Up): %.2f", sensitivityUp.getValue()));
                settings.setFloatValue("sensitivityUp", sensitivityUp.getValue());
                settings.saveSettings();
            }
        });
        sensitivityUp.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelDown = new Label("", skin);
        final Slider sensitivityDown = new Slider(0, 10, 0.05f, false, skin);
        sensitivityDown.setValue(settings.getFloat("sensitivityDown"));
        sensitivityDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelDown.setText(String.format("Sensitivity (Down): %.2f", sensitivityDown.getValue()));
                settings.setFloatValue("sensitivityDown", sensitivityDown.getValue());
                settings.saveSettings();
            }
        });
        sensitivityDown.fire(new ChangeListener.ChangeEvent());

        final CheckBox invertedX = new CheckBox(" Invert X-axis", skin);
        invertedX.setChecked(settings.getBoolean("invertedX"));
        invertedX.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedX", invertedX.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox invertedY = new CheckBox(" Invert Y-axis", skin);
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
        settingsTable.add(sensitivityLeft).padTop(10).row();
        settingsTable.add(sensitivityLabelRight).padTop(15).row();
        settingsTable.add(sensitivityRight).padTop(10).row();

        settingsTable.add(sensitivityLabelUp).padTop(30).row();
        settingsTable.add(sensitivityUp).padTop(10).row();
        settingsTable.add(sensitivityLabelDown).padTop(15).row();
        settingsTable.add(sensitivityDown).padTop(10).row();

        settingsTable.add(invertedX).padTop(30).row();
        settingsTable.add(invertedY).padTop(10);

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