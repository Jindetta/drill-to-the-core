package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;

/**
 * EndScreen class will display end screen.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class EndScreen extends BaseScreen {
    private Table layout;
    private Image background;
    private Texture backgroundTexture;
    private int gradualHighScore;
    private int scorePerSecond;
    private int totalScore;

    private Label totalScoreLabel;

    public EndScreen(String message, int highScore, float basescore, float drillDepth, final int levelIndex) {
        backgroundTexture = new Texture("images/endscreen-background.png");

        background = new Image(backgroundTexture);
        addActor(background);

        layout = new Table();

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "continueGame":
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
        Label baseScore = new Label("BASESCORE: " + Math.round(basescore), skin);
        Label Depth = new Label("MAXIMUM DEPTH: " + Math.round(drillDepth),skin );
        totalScoreLabel = new Label("", skin);

        ImageButton continueButton = new ImageButton(skin, localization.getValue("nextMenu"));
        continueButton.addListener(clickListener);
        continueButton.setName("continueGame");

        ImageButton restartButton = new ImageButton(skin, localization.getValue("restartGame"));
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        ImageButton exitButton = new ImageButton(skin, localization.getValue("backButtonGame"));
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

    @Override
    public void render(float delta) {
        super.render(delta);
        totalScoreLabel.setText("TOTAL SCORE: " + gradualHighScore);
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

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        super.dispose();
    }
}