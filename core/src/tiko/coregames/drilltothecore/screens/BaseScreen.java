package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public abstract class BaseScreen extends Stage implements Screen {
    Skin skin;

    BaseScreen() {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));
        skin = new Skin(Gdx.files.internal("menu/uiskin.json"));

        Gdx.app.log(getClass().getSimpleName(), "Creating...");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        Gdx.graphics.setTitle("Drill to the Core - " + getClass().getSimpleName());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act(delta);
        draw();
    }

    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        Gdx.app.log(getClass().getSimpleName(), "Disposing...");

        super.dispose();
        skin.dispose();
    }
}