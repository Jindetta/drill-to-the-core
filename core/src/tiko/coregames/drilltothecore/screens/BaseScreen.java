package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public abstract class BaseScreen extends Stage implements Screen {
    Skin skin;

    BaseScreen(Viewport viewport) {
        super(viewport, CoreSetup.getBatch());
        skin = new Skin(Gdx.files.internal("menu/uiskin.json"));
    }

    BaseScreen() {
        this(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        Gdx.graphics.setTitle("Drill to the Core - " + getClass().getSimpleName());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        act();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();

        if (viewport != null) {
            viewport.update(width, height, true);
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
        dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
        skin.dispose();
    }
}