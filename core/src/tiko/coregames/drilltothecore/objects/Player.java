package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;
    private float totalFuel, fuelConsumptionRate;

    public Player() {
        super("images/player.png");
        controller = new ControllerManager(this);

        controller.setXThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        controller.setYThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);

        Vector3 playerSpawn = LevelManager.getSpawnPoint("player");

        if (playerSpawn != null) {
            setPosition(playerSpawn.x, playerSpawn.y);
        }

        setMaxFuel();
    }

    public void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
        fuelConsumptionRate = PLAYER_FUEL_MIN_CONSUMPTION;
    }

    public boolean consumeFuel(float delta) {
        totalFuel = Math.max(0, totalFuel - fuelConsumptionRate * delta);

        return totalFuel > 0;
    }

    public void draw(SpriteBatch batch, float delta) {
        // Update movement based on controller input
        controller.updateController(delta);
        super.draw(batch);
    }

    private boolean isAllowedToMoveUp() {
        TiledMapTile tile = LevelManager.getTileFromPosition(getX(), getY(), "background");
        Boolean value = LevelManager.getBoolean(tile, "sky");

        return value == null || !value;
    }

    // DEBUG - Destroy tiles
    private void updateTileStatus() {
        TiledMapTileLayer.Cell[] cells = new TiledMapTileLayer.Cell[] {
            LevelManager.getCellFromPosition(getX() + getWidth() / 2, getY(), "ground"),
            LevelManager.getCellFromPosition(getX() + getWidth() / 2, getY() + getHeight(), "ground"),
            LevelManager.getCellFromPosition(getX(), getY() + getHeight() / 2, "ground"),
            LevelManager.getCellFromPosition(getX() + getWidth(), getY() + getHeight() / 2, "ground"),
        };

        for (TiledMapTileLayer.Cell cell : cells) {
            if (cell != null) {
                cell.setTile(null);
            }
        }
    }

    @Override
    public void move(float accelerometerX, float accelerometerY, float delta) {
        if (!consumeFuel(delta)) {
            Gdx.app.log(getClass().getSimpleName(), "Sorry, but you ran out of fuel");
            return;
        }

        if (accelerometerX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            translateX( PLAYER_MOVE_SPEED * delta);
            accelerometerX = PLAYER_MOVE_SPEED;
        }
        if (accelerometerX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            translateX(-PLAYER_MOVE_SPEED * delta);
            accelerometerX = -PLAYER_MOVE_SPEED;
        }

        // Allow only one axis movement
        if (accelerometerX != 0) {
            return;
        }

        if (isAllowedToMoveUp() && (accelerometerY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP))) {
            translateY(PLAYER_MOVE_SPEED * delta);
            accelerometerY = PLAYER_MOVE_SPEED;
        }
        if (accelerometerY < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            translateY(-PLAYER_MOVE_SPEED * delta);
            accelerometerY = -PLAYER_MOVE_SPEED;
        }

        updateTileStatus();
    }
}