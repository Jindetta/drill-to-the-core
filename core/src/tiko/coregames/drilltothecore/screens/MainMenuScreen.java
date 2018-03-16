package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LocalizationManager;
import tiko.coregames.drilltothecore.managers.SettingsManager;

public class MainMenuScreen extends BaseScreen {
    private Table gameMenu;

    public MainMenuScreen() {
        super(new ScreenViewport());

        LocalizationManager menu = new LocalizationManager("menu");

        TextButton play = new TextButton(menu.getValue("play"), skin);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CoreSetup.nextScreen(new GameScreen());
            }
        });

        TextButton exit = new TextButton(menu.getValue("exit"), skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

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

        gameMenu = new Table();
        gameMenu.setPosition((Gdx.graphics.getWidth() - gameMenu.getPrefWidth()) / 2, (Gdx.graphics.getHeight() - gameMenu.getPrefHeight()) / 2);
        gameMenu.add(play).row();
        gameMenu.add(exit).padTop(15).row();

        gameMenu.add(sensitivityLabelX).padTop(50).row();
        gameMenu.add(sensitivityX).padTop(15).row();
        gameMenu.add(sensitivityLabelY).padTop(20).row();
        gameMenu.add(sensitivityY).padTop(15).row();
        gameMenu.add(inverted).padTop(20);

        addActor(gameMenu);
    }
}