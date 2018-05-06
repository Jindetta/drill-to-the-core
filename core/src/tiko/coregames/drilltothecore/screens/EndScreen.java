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
import tiko.coregames.drilltothecore.objects.Player;

import java.util.Locale;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

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

    public EndScreen(Player player, float time, final int levelIndex) {
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
        totalScore = player.getTotalScore();
        scorePerSecond = totalScore / 3;

        Label timeValue = new Label(
            String.format(
                Locale.ENGLISH,
                "TIME: %02d:%02d:%02d",
                (int) time / (60 * 60),
                (int) time / 60 % 60,
                (int) time % 60
            ),
            skin
        );

        Label depthValue = new Label("MAXIMUM DEPTH: " + Math.round(player.getDrillDepth()),skin );
        totalScoreLabel = new Label("", skin);

        ImageButton continueButton = new ImageButton(skin, localization.getValue("nextMenu"));
        continueButton.addListener(clickListener);
        continueButton.setName("continueGame");

        ImageButton restartButton = new ImageButton(skin, localization.getValue("restartLevel"));
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        ImageButton exitButton = new ImageButton(skin, localization.getValue("backButtonGame"));
        exitButton.addListener(clickListener);

        layout.add(timeValue).row();
        layout.add(depthValue).row();
        layout.add(totalScoreLabel).row();

        if (levelIndex < LEVEL_COUNT - 1) {
            layout.add(continueButton).row();
            settings.setIntegerValue("currentLevel", levelIndex + 1);
        } else {
            settings.removeKey("currentLevel");
        }

        settings.saveSettings();

        layout.add(restartButton).pad(MENU_DEFAULT_PADDING).row();
        layout.add(exitButton).pad(MENU_DEFAULT_PADDING);

        addActor(layout);

        if (!settings.hasValue("level_" + levelIndex) || settings.getInteger("level_" + levelIndex) < totalScore) {
            settings.setIntegerValue("level_" + levelIndex, totalScore);
            settings.setIntegerValue("time_" + levelIndex, Math.round(time));
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