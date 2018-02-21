package tiko.coregames.drilltothecore;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.ApplicationAdapter;

public class CoreSetup extends ApplicationAdapter {
	private SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();

		// TODO: Initialize managers
	}

	@Override
	public void render () {
	    float delta = Gdx.graphics.getDeltaTime();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		// TODO: Adapt to changes
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
    }
}