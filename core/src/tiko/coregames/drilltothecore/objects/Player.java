package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.managers.LevelManager;

public class Player extends BaseObject {
    public Player() {
        super("images/player.png");
        Rectangle playerSpawn = LevelManager.getSpawnPoint("player");

        if (playerSpawn != null) {
            setPosition(playerSpawn.x, playerSpawn.y);
        }
    }

    // For debugging purposes
    private void updateMovement(float delta) {
        final float DEBUG_SPEED = 48;

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            translateX( DEBUG_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            translateX(-DEBUG_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            translateY(DEBUG_SPEED * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            translateY(-DEBUG_SPEED * delta);
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        updateMovement(delta);
        super.draw(batch);
    }
}