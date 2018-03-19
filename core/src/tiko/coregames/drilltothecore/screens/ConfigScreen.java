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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

public class ConfigScreen extends BaseScreen {
    private Table settingsTable;

    public ConfigScreen() {
        super(new ScreenViewport());

        final SettingsManager settings = SettingsManager.getUserProfiles();

        final Label sensitivityLabelX = new Label("", skin);
        final Slider sensitivityX = new Slider(0, 10, 0.05f, false, skin);
        sensitivityX.setValue(settings.getFloat("sensitivityX"));
        sensitivityX.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelX.setText(String.format("Sensitivity X: %.2f", sensitivityX.getValue()));
                settings.setFloatValue("sensitivityX", sensitivityX.getValue());
                settings.saveSettings();
            }
        });
        sensitivityX.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelY = new Label("", skin);
        final Slider sensitivityY = new Slider(0, 10, 0.05f, false, skin);
        sensitivityY.setValue(settings.getFloat("sensitivityY"));
        sensitivityY.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelY.setText(String.format("Sensitivity Y: %.2f", sensitivityY.getValue()));
                settings.setFloatValue("sensitivityY", sensitivityY.getValue());
                settings.saveSettings();
            }
        });
        sensitivityY.fire(new ChangeListener.ChangeEvent());

        final CheckBox inverted = new CheckBox(" Invert Y-axis", skin);
        inverted.setChecked(settings.getBoolean("invertedY"));
        inverted.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedY", inverted.isChecked());
                settings.saveSettings();
            }
        });

        settingsTable = new Table();
        settingsTable.add(sensitivityLabelX).padTop(50).row();
        settingsTable.add(sensitivityX).padTop(15).row();
        settingsTable.add(sensitivityLabelY).padTop(20).row();
        settingsTable.add(sensitivityY).padTop(15).row();
        settingsTable.add(inverted).padTop(20);

        addActor(settingsTable);
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            CoreSetup.nextScreen(new MainMenuScreen());
        }

        return super.keyDown(key);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        settingsTable.setPosition((width - settingsTable.getWidth()) / 2, (height - settingsTable.getHeight()) / 2);
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
