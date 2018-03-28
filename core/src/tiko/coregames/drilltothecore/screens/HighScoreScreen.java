package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SettingsManager;

public class HighScoreScreen extends BaseScreen {
    private Table scoreList;

    public HighScoreScreen() {
        scoreList = new Table();

        for (int i = 0; i < 10; i++) {
            String text = "-- No results --";
            SettingsManager profile = SettingsManager.getUserProfile(i, false);

            if (profile != null) {
                text = String.valueOf(profile.getInteger("highScore"));
            }

            scoreList.add(new Label(text, skin)).padBottom(5).row();
        }

        addActor(scoreList);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        Gdx.input.setCatchBackKey(false);
        super.hide();
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