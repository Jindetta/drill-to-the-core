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

        TextButton settings = new TextButton(menu.getValue("settings"), skin);
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CoreSetup.nextScreen(new ConfigScreen());
            }
        });

        TextButton exit = new TextButton(menu.getValue("exit"), skin);
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        gameMenu = new Table();
        gameMenu.add(play).row();
        gameMenu.add(settings).padTop(15).row();
        gameMenu.add(exit).padTop(15);

        addActor(gameMenu);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gameMenu.setPosition((width - gameMenu.getWidth()) / 2, (height - gameMenu.getHeight()) / 2);
    }
}