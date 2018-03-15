package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;

    private float totalFuel, fuelConsumptionRate;
    private Circle vision;

    public Player() {
        super("images/player.png");
        controller = new ControllerManager(this);

        controller.setXThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        controller.setYThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        Vector3 playerSpawn = LevelManager.getSpawnPoint("player");

        if (playerSpawn != null) {
            setPosition(playerSpawn.x, playerSpawn.y);
            vision = new Circle(playerSpawn.x, playerSpawn.y, getWidth() * 2);
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

    private Array<Vector2> getPointsFromVision() {
        Array<Vector2> points = new Array<>();

        for (float i = vision.y - vision.radius; i < vision.y + vision.radius; i++) {
            for (float j = vision.x; Math.pow(j - vision.x, 2) + Math.pow(i - vision.y, 2) <= Math.pow(vision.radius, 2); j--) {
                points.add(new Vector2(j, i));
            }
            for (float j = vision.x + 1; (j - vision.x) * (j - vision.x) + (i - vision.y) * (i - vision.y) <= Math.pow(vision.radius, 2); j++) {
                points.add(new Vector2(j, i));
            }
        }

        return points;
    }

    // DEBUG - Destroy tiles
    private void updateTileStatus() {
        vision.setPosition(getX(), getY());
        for (Vector2 vector : getPointsFromVision()) {
            TiledMapTileLayer.Cell cell = LevelManager.getCellFromPosition(vector.x, vector.y, "shroud");

            if (cell != null) {
                cell.setTile(null);
            }
        }

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