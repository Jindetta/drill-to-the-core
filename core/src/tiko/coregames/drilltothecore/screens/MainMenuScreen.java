package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

public class MainMenuScreen extends BaseScreen {
    public MainMenuScreen() {
        super(new ScreenViewport());

        TextButton play = new TextButton("START THE GAME", skin);
        play.setPosition((Gdx.graphics.getWidth() - play.getPrefWidth()) / 2, (Gdx.graphics.getHeight() - play.getPrefHeight()) / 2);
        play.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CoreSetup.nextScreen(new GameScreen());
            }
        });

        addActor(play);
    }
}