package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;

public class EndScreen extends BaseScreen {
    private Table layout;

    public EndScreen() {
        layout = new Table();

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "continue":
                        // Next level
                        Setup.nextScreen(new GameScreen());
                        break;
                    case "restart":
                        Setup.nextScreen(new GameScreen());
                        break;
                    default:
                        Setup.nextScreen(new MainMenuScreen());
                        break;
                }
            }
        };

        TextButton continueButton = new TextButton(coreLocalization.getValue("game_nextLevel"), skin);
        continueButton.addListener(clickListener);
        continueButton.setName("continue");

        TextButton restartButton = new TextButton(coreLocalization.getValue("pause_restartLevel"), skin);
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        TextButton exitButton = new TextButton(coreLocalization.getValue("pause_exit"), skin);
        exitButton.addListener(clickListener);

        layout.add(continueButton).row();
        layout.add(restartButton).padTop(15).row();
        layout.add(exitButton).padTop(15);

        addActor(layout);
    }

    public void setTitle(String title) {
        //Not implemented
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        layout.setPosition(
            (viewport.getWorldWidth() - layout.getWidth()) / 2,
            (viewport.getWorldHeight() - layout.getHeight()) / 2
        );
    }
}