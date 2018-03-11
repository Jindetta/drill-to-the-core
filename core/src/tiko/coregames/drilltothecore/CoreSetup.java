package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.screens.MainMenuScreen;
import tiko.coregames.drilltothecore.utilities.Debug;
import tiko.coregames.drilltothecore.utilities.Setup;

public class CoreSetup extends Game {
	private SpriteBatch batch;

    /**
     * Initializes game.
     */
	@Override
	public void create () {
		batch = new SpriteBatch();
		Setup.initialize(this, batch);

		setScreen(new MainMenuScreen());
        Gdx.graphics.setTitle("Drill to the Core");
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