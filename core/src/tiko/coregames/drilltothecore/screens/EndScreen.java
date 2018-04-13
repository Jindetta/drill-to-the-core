package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EndScreen extends BaseScreen {
    private Table layout;

    public EndScreen() {
        layout = new Table();

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