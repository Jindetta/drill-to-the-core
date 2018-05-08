package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import tiko.coregames.drilltothecore.screens.SplashScreen;

/*
	TODO: Fix profiles at MainMenuScreen (and possibly elsewhere)
	TODO: Fix language button to work on any platform
	TODO: Fix CalibrationScreen (add title and proper layout)
	TODO: Fix SettingsScreen layout
	TODO: Fix frame drops (?)
*/

/**
 * Setup class will initialize and start the game.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class Setup extends Game {
	/**
	 * Defines reference to current game instance.
	 */
	private static Setup instance;

	/**
	 * Stores batch information.
	 */
    private SpriteBatch batch;

	/**
	 * Gets default SpriteBatch.
	 *
	 * @return batch of current game instancr
	 */
	public static SpriteBatch getBatch() {
		return instance.batch;
	}

	/**
	 * Displays next screen.
	 *
	 * @param screen    instance of Screen
	 */
	public static void nextScreen(Screen screen) {
		instance.setScreen(screen);
	}

	@Override
	public void create () {
		Gdx.graphics.setTitle("Drill to the Core");

		batch = new SpriteBatch();
		instance = this;

		setScreen(new SplashScreen());
	}

	@Override
	public void dispose () {
		setScreen(null);
		batch.dispose();
    }
}