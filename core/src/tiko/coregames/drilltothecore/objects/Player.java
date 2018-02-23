package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;

public class Player extends BaseObject {
    private ControllerManager controller;

    public Player() {
        super("images/player.png");
        Rectangle playerSpawn = LevelManager.getSpawnPoint("player");
        controller = new ControllerManager();
        controller.setLimits(48, 48, 2, 2);

        if (playerSpawn != null) {
            setPosition(playerSpawn.x, playerSpawn.y);
        }
    }

    // For debugging purposes
    private void updateMovement(float delta) {
        final float DEBUG_SPEED = 48;

        controller.updateController(this, delta);
    }

    public void draw(SpriteBatch batch, float delta) {
        updateMovement(delta);
        super.draw(batch);
    }
}