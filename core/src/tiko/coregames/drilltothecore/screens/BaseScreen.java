package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.utilities.Debugger;

public class BaseScreen implements Screen, Debugger {
    protected CoreSetup host;
    protected SpriteBatch batch;
    protected Stage stage;

    public BaseScreen(Viewport viewport, CoreSetup host) {
        batch = host.getBatch();
        this.host = host;

        stage = new Stage(viewport, batch);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = stage.getViewport();

        if (viewport != null) {
            viewport.setScreenSize(width, height);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // Dispose this screen when it's hidden
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public static String getDebugTag() {
        return BaseScreen.class.getSimpleName();
    }
}