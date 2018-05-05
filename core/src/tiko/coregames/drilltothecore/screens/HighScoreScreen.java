package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

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
    private Table scoreList;

    public HighScoreScreen() {
        scoreList = new Table();

        for (int i = 0; i < 10; i++) {
            String text = "-- NO RESULTS --";
            SettingsManager profile = SettingsManager.getUserProfile(i, false);

            if (profile != null) {
                long totalHighScore = 0;

                for (int j = 1; j <= LEVEL_COUNT; j++) {
                    int value = profile.getInteger("level_" + j);
                    totalHighScore += value;
                }

                if (totalHighScore > 0) {
                    text = String.valueOf(totalHighScore);
                }
            }

            scoreList.add(new Label(text, skin, "scoresMenu")).padBottom(5).row();
        }

        addActor(scoreList);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        scoreList.setPosition(
            (viewport.getWorldWidth() - scoreList.getWidth()) / 2,
            (viewport.getWorldHeight() - scoreList.getHeight()) / 2
        );
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
        private int time;

        public HighScore(String name, SettingsManager profile) {
            this.name = name;
            totalScore = 0;
            time = 0;

            for (int i = 1; i < LEVEL_COUNT; i++) {
                totalScore += profile.getIntegerIfExists("level_" + i, 0);
                time += profile.getIntegerIfExists("time_" + i, 0);
            }
        }
    }
}