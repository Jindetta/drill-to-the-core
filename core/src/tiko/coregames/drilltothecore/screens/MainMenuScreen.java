package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LocalizationManager;

public class MainMenuScreen extends BaseScreen {
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

        Table gameMenu = new Table();
        gameMenu.setPosition((Gdx.graphics.getWidth() - gameMenu.getPrefWidth()) / 2, (Gdx.graphics.getHeight() - gameMenu.getPrefHeight()) / 2);
        gameMenu.add(play).row();
        gameMenu.add(exit).padTop(15);

        addActor(gameMenu);
    }
}