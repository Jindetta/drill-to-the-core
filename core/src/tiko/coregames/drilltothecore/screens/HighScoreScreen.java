package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

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
            String text = "-- No results --";
            SettingsManager profile = SettingsManager.getUserProfile(i, false);

            if (profile != null) {
                int value = profile.getInteger("highScore");

                if (value > 0) {
                    text = String.valueOf(value);
                }
            }

            scoreList.add(new Label(text, skin)).padBottom(5).row();
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
}