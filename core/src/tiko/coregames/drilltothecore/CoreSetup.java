package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.managers.CameraManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Utilities.*;

public class CoreSetup extends Game {
	private SpriteBatch batch;

	// Debug
	private CameraManager cameraManager;
	private Player player;

    /**
     * Initializes game.
     */
	@Override
	public void create () {
		batch = new SpriteBatch();

		// TODO: Initialize managers properly
		cameraManager = new CameraManager();
		LevelManager.setupLevel(0);
		player = new Player();

		Gdx.graphics.setTitle("Drill to the Core");
	}

    /**
     * Resizes game window.
     *
     * @param width     Screen width.
     * @param height    Screen height.
     */
    @Override
    public void resize(int width, int height) {
        cameraManager.resizeHud(width, height);
    }

    /**
     * Renders game.
     */
    @Override
	public void render () {
	    //super.render();
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

    /**
     * Disposes all allocated resources.
     */
	@Override
	public void dispose () {
	    // Dispose last shown screen
		setScreen(null);
		batch.dispose();
		player.dispose();

		Debug.dispose();
		LevelManager.dispose();
    }
}