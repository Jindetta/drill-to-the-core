package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.screens.SplashScreen;
import tiko.coregames.drilltothecore.utilities.Debug;

public class CoreSetup extends Game {
	private static SpriteBatch batch;
	private static CoreSetup self;

	public static SpriteBatch getBatch() {
		return batch;
	}

	public static void nextScreen(Screen screen) {
		self.setScreen(screen);
	}

    /**
     * Initializes game.
     */
	@Override
	public void create () {
		batch = new SpriteBatch();
		self = this;

		setScreen(new SplashScreen());

        Gdx.graphics.setTitle("Drill to the Core");
        Gdx.input.setCatchBackKey(true);
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