package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.managers.CameraManager;
import tiko.coregames.drilltothecore.screens.GameScreen;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class CoreSetup extends Game {
	private SpriteBatch batch;

	// Debug
	private CameraManager cameraManager;

	/**
	 * Defines debug tag for this class.
	 */
	private static final String DEBUG_TAG = CoreSetup.class.getName();

	public SpriteBatch getBatch() {
	    return batch;
    }

    /**
     * Initializes game.
     */
	@Override
	public void create () {
		batch = new SpriteBatch();

		// TODO: Initialize managers properly
		cameraManager = new CameraManager();

		Gdx.graphics.setTitle("Drill to the Core");
		setScreen(new GameScreen(this));
	}

    /**
     * Renders game.
     */
    @Override
	public void render () {
	    super.render();

		batch.begin();
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

		Debug.dispose();
    }
}