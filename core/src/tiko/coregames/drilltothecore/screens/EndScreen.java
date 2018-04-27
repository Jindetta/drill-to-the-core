package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;

public class EndScreen extends BaseScreen {
    private Table layout;
    private int gradualHighScore;
    private int scorePerSecond;
    private int totalScore;

    private Label totalScoreLabel;

    public EndScreen(String message, int highScore, float basescore, float drillDepth, final int levelIndex) {
        layout = new Table();

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "continue":
                        // Next level
                        Setup.nextScreen(new GameScreen(levelIndex + 1, false));
                        break;
                    case "restart":
                        Setup.nextScreen(new GameScreen(levelIndex, false));
                        break;
                    default:
                        Setup.nextScreen(new MainMenuScreen());
                        break;
                }
            }
        };

        gradualHighScore = 0;
        scorePerSecond = highScore / 3;
        totalScore = highScore;

        Label title = new Label(message + ": ", skin);
        Label baseScore = new Label("Basescore: " + Math.round(basescore), skin);
        Label Depth = new Label("Maximum Depth achieved: " + Math.round(drillDepth),skin );
        totalScoreLabel = new Label("Total Score: ", skin);

        TextButton continueButton = new TextButton(coreLocalization.getValue("game_nextLevel"), skin);
        continueButton.addListener(clickListener);
        continueButton.setName("continue");

        TextButton restartButton = new TextButton(coreLocalization.getValue("pause_restartLevel"), skin);
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        TextButton exitButton = new TextButton(coreLocalization.getValue("pause_exit"), skin);
        exitButton.addListener(clickListener);


        layout.add(title).row();
        layout.add(baseScore).row();
        layout.add(Depth).row();
        layout.add(totalScoreLabel).row();
        layout.add(continueButton).row();
        layout.add(restartButton).padTop(15).row();
        layout.add(exitButton).padTop(15);

        addActor(layout);

        if (!settings.hasValue("level_" + levelIndex) || settings.getInteger("level_" + levelIndex) < highScore) {
            settings.setIntegerValue("level_" + levelIndex, highScore);
            settings.saveSettings();
        }
    }

    public void setTitle(String title) {
        //Not implemented
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        totalScoreLabel.setText("Total Score: " + gradualHighScore);
        gradualHighScore = Math.min(totalScore, Math.round(gradualHighScore + scorePerSecond * delta));
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