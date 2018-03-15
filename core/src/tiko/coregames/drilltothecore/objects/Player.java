package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;

    private float totalFuel, fuelConsumptionRate;
    private Circle playerView;

    public Player() {
        super("images/player.png");
        controller = new ControllerManager(this);

        controller.setXThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        controller.setYThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        Vector3 playerSpawn = LevelManager.getSpawnPoint("player");

        if (playerSpawn != null) {
            setPosition(playerSpawn.x, playerSpawn.y);
            playerView = new Circle(playerSpawn.x, playerSpawn.y, getWidth() * 2);
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

    public float getFuel() {
        return totalFuel;
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

    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = LevelManager.getCellFromPosition(x, y, "shroud");

        if (cell != null) {
            cell.setTile(null);
        }
    }

    private void updatePlayerView() {
        playerView.setPosition(getX(), getY());

        for (float y = playerView.y - playerView.radius; y < playerView.y + playerView.radius; y++) {
            for (float x = playerView.x; Math.pow(x - playerView.x, 2) + Math.pow(y - playerView.y, 2) <= Math.pow(playerView.radius, 2); x--) {
                clearShroudTile(x, y);
            }
            for (float x = playerView.x + 1; (x - playerView.x) * (x - playerView.x) + (y - playerView.y) * (y - playerView.y) <= Math.pow(playerView.radius, 2); x++) {
                clearShroudTile(x, y);
            }
        }
    }

    // DEBUG - Destroy tiles
    private void updateTileStatus() {
        TiledMapTileLayer.Cell[] cells = new TiledMapTileLayer.Cell[] {
            LevelManager.getCellFromPosition(getX() + getWidth() / 2, getY(), "ground"),
            LevelManager.getCellFromPosition(getX() + getWidth() / 2, getY() + getHeight(), "ground"),
            LevelManager.getCellFromPosition(getX(), getY() + getHeight() / 2, "ground"),
            LevelManager.getCellFromPosition(getX() + getWidth(), getY() + getHeight() / 2, "ground"),
        };

        updatePlayerView();

        for (TiledMapTileLayer.Cell cell : cells) {
            if (cell != null) {
                cell.setTile(null);
            }
        }
    }

    private boolean isDirectionAllowed(char direction) {
        switch (direction) {
            case 'L': return getX() > 0;
            case 'R': return getX() + getWidth() < TOTAL_TILES_WIDTH;
            case 'U': return getY() + getHeight() < TOTAL_TILES_HEIGHT;
            case 'D': return getY() > 0;
        }

        return false;
    }

    @Override
    public void move(float accelerometerX, float accelerometerY, float delta) {
        if (!consumeFuel(delta)) {
            Gdx.app.log(getClass().getSimpleName(), "Sorry, but you ran out of fuel");
            return;
        }

        if (isDirectionAllowed('R') && (accelerometerX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            translateX( PLAYER_MOVE_SPEED * delta);
            accelerometerX = PLAYER_MOVE_SPEED;
        }
        if (isDirectionAllowed('L') && (accelerometerX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            translateX(-PLAYER_MOVE_SPEED * delta);
            accelerometerX = -PLAYER_MOVE_SPEED;
        }

        // Allow only one axis movement
        if (accelerometerX == 0) {
            if (isDirectionAllowed('U') && (isAllowedToMoveUp() && (accelerometerY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP)))) {
                translateY(PLAYER_MOVE_SPEED * delta);
                accelerometerY = PLAYER_MOVE_SPEED;
            }
            if (isDirectionAllowed('D') && (accelerometerY < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
                translateY(-PLAYER_MOVE_SPEED * delta);
                accelerometerY = -PLAYER_MOVE_SPEED;
            }
        }

        updateTileStatus();
    }
}