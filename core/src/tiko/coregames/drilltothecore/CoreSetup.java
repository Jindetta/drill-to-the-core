package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Rectangle;
import tiko.coregames.drilltothecore.managers.CameraManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.utilities.Utilities.*;

public class CoreSetup extends ApplicationAdapter {
	private SpriteBatch batch;
	private CameraManager cameraManager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// TODO: Initialize managers
		cameraManager = new CameraManager();

		Gdx.graphics.setTitle("Drill to the Core");
	}

    @Override
    public void resize(int width, int height) {
        cameraManager.resizeHud(width, height);
    }

    @Override
	public void render () {
	    float delta = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		cameraManager.applyHudCamera(batch);

        Debug.render(batch);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

		// Dispose debug font
		Debug.dispose();
    }
}