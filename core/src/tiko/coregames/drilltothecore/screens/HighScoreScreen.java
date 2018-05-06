package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
    private Table windowLayout, scoreList;

    public HighScoreScreen() {
        windowLayout = new Table();
        windowLayout.defaults().expand().uniform();

        scoreList = new Table();
        scoreList.columnDefaults(3);
        scoreList.defaults().expandX().uniform();

        ArrayList<HighScore> scores = getHighestScores();

        for (int i = 0; i < scores.size(); i++) {
            HighScore scorer = scores.get(i);

            Label name = new Label(scorer.getName(), skin, "scoresMenu");
            Label score = new Label(scorer.getScore(), skin, "scoresMenu");
            Label time = new Label(scorer.getTime(), skin, "scoresMenu");

            scoreList.add(name);
            scoreList.add(time);
            scoreList.add(score).row();
        }

        windowLayout.add(new ImageButton(skin, "highscoretitle_eng")).row();
        windowLayout.add(scoreList).top();

        addActor(windowLayout);
    }

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

            long maxValue = highScores.get(0).totalScore;

            for (int j = 0; j < highScores.size(); j++) {
                if (maxValue <= highScores.get(j).totalScore) {
                    maxValue = highScores.get(j).totalScore;
                    sortedHighScores.add(highScores.remove(j));
                }
            }
        }

        return sortedHighScores;
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

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

    private class HighScore {
        private String name;
        private long totalScore;
        private float time;

        public HighScore(String name, SettingsManager profile) {
            this.name = name;
            totalScore = 0;
            time = 0;

            for (int i = 1; i < LEVEL_COUNT; i++) {
                totalScore += profile.getIntegerIfExists("level_" + i, 0);
                time += profile.getIntegerIfExists("time_" + i, 0);
            }
        }

        public String getName() {
            return name.toUpperCase();
        }

        public String getScore() {
            return String.valueOf(totalScore);
        }

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