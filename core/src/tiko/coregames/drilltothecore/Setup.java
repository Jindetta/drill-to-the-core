package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import tiko.coregames.drilltothecore.screens.SplashScreen;
import tiko.coregames.drilltothecore.utilities.Debug;

/**
 * Setup class will initialize and start the game.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class Setup extends Game {
	private static Setup instance;
    private SpriteBatch batch;

	public static SpriteBatch getBatch() {
		return instance.batch;
	}

	public static void nextScreen(Screen screen) {
		instance.setScreen(screen);
	}

    /**
     * Initializes game.
     */
	@Override
	public void create () {
        Debug.initialize();
		batch = new SpriteBatch();
		instance = this;

		setScreen(new SplashScreen());

        Gdx.graphics.setTitle("Drill to the Core");
	}

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Debug.resize(width, height);
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
		setScreen(null);
		batch.dispose();
		Debug.dispose();
    }
}