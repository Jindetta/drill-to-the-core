package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Rectangle;
import tiko.coregames.drilltothecore.managers.CameraManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Utilities.*;

import java.util.logging.Level;

public class CoreSetup extends ApplicationAdapter {
	private SpriteBatch batch;

	// Debug
	private CameraManager cameraManager;
	private Player player;
	@Override
	public void create () {
		batch = new SpriteBatch();

		// TODO: Initialize managers properly
		cameraManager = new CameraManager();
		LevelManager.setupLevel(0);
		player = new Player();

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

        cameraManager.applyHudCamera(batch);
		LevelManager.applyCameraAndRender(cameraManager.getHUDCamera());

		batch.begin();
		player.draw(batch, delta);
		cameraManager.followObject(player);
        Debug.render(batch);
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();

		Debug.dispose();
		LevelManager.dispose();
    }
}