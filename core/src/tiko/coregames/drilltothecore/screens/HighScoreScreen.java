package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

import java.util.ArrayList;
import java.util.Locale;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * HighScoreScreen class will display high scores.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class HighScoreScreen extends BaseScreen {
    /**
     * Defines background image.
     */
    private Image background;

    /**
     * Defines screen layout.
     */
    private Table windowLayout;

    public HighScoreScreen() {
        assets.load("images/endscreen-background.png", Texture.class);
        assets.finishLoadingAsset("images/endscreen-background.png");

        background = new Image(assets.get("images/endscreen-background.png", Texture.class));
        addActor(background);

        windowLayout = new Table();
        windowLayout.defaults().expand().uniform();

        Table scoreList = new Table();
        scoreList.columnDefaults(3);
        scoreList.defaults().expandX().uniform();

        ArrayList<HighScore> scores = getHighestScores();

        for (HighScore scorer : scores) {
            Label name = new Label(scorer.getName(), skin, "menu");
            Label score = new Label(scorer.getScore(), skin, "menu");
            Label time = new Label(scorer.getTime(), skin, "menu");

            scoreList.add(name);
            scoreList.add(time);
            scoreList.add(score).row();
        }

        windowLayout.add(new ImageButton(skin, "highscoretitle_eng")).row();
        windowLayout.add(scoreList).top().row();

        ImageButton backButton = new ImageButton(skin, localization.getValue("backButton"));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                HighScoreScreen.this.keyDown(Input.Keys.BACK);
            }
        });

        windowLayout.add(backButton).align(Align.bottomLeft).pad(SAFE_ZONE_SIZE);

        addActor(windowLayout);
    }

    /**
     * Gets all high scores in order.
     *
     * @return list of scores
     */
    private ArrayList<HighScore> getHighestScores() {
        ArrayList<HighScore> highScores = new ArrayList<>();
        ArrayList<HighScore> sortedHighScores = new ArrayList<>(10);

        for (int i = 0; i < MAX_SAVED_PROFILES; i++) {
            SettingsManager profile = SettingsManager.getUserProfile(i, false);

            if (profile != null) {
                highScores.add(new HighScore(SettingsManager.getProfileName(i), profile));
            }
        }

        for (int i = 0; i < 10; i++) {
            if (highScores.isEmpty()) {
                break;
            }

            sortedHighScores.add(highScores.remove(getHighestPossibleRating(highScores)));
        }

        return sortedHighScores;
    }

    /**
     * Gets highest score rating in the given list.
     *
     * @param scores list of scores
     * @return highest value
     */
    private int getHighestPossibleRating(ArrayList<HighScore> scores) {
        if (scores != null && !scores.isEmpty()) {
            int index = 0;
            double maxValue = scores.get(0).totalScore / scores.get(0).time;

            for (int i = 1; i < scores.size(); i++) {
                double value = scores.get(i).totalScore / scores.get(i).time;

                if (maxValue < value) {
                    maxValue = value;
                    index = i;
                }
            }

            return index;
        }

        return -1;
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        windowLayout.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        windowLayout.setPosition(0, 0);
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            Setup.nextScreen(new MainMenuScreen());
        }

        return true;
    }

    /**
     * Class, which stores all important values.
     */
    private class HighScore {
        /**
         * Defines name.
         */
        private String name;

        /**
         * Defines total score.
         */
        private long totalScore;

        /**
         * Defines total time.
         */
        private float time;

        public HighScore(String name, SettingsManager profile) {
            this.name = name;
            totalScore = 0;
            time = 0;

            if (DEMO_MODE) {
                totalScore = profile.getIntegerIfExists("level_0", 0);
                time = profile.getIntegerIfExists("time_0", 0);
            } else {
                for (int i = 1; i < LEVEL_COUNT; i++) {
                    totalScore += profile.getIntegerIfExists("level_" + i, 0);
                    time += profile.getIntegerIfExists("time_" + i, 0);
                }
            }
        }

        /**
         * Gets name in uppercase format.
         *
         * @return player name
         */
        public String getName() {
            return name.toUpperCase();
        }

        /**
         * Gets total score.
         *
         * @return player total score as string
         */
        public String getScore() {
            return String.valueOf(totalScore);
        }

        /**
         * Gets total time.
         *
         * @return total time as formatted string
         */
        public String getTime() {
            return String.format(
                Locale.ENGLISH,
                "%02d:%02d:%02d",
                (int) time / (60 * 60),
                (int) time / 60 % 60,
                (int) time % 60
            );
        }
    }
}