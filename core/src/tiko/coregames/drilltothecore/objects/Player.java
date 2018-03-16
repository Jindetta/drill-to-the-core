package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;
    private LevelManager map;

    private float totalFuel, fuelConsumptionRate;
    private Circle playerView;

    private String DrillPointsTO = "L";

    public Player(LevelManager map) {
        super("images/player.png");
        controller = new ControllerManager(this);
        playerView = new Circle(getX(), getY(), getWidth() * 2);

        controller.setXThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);
        controller.setYThreshold(DEFAULT_MIN_THRESHOLD, DEFAULT_MIN_THRESHOLD);

        this.map = map;
        setMaxFuel();
    }

    private void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
        fuelConsumptionRate = PLAYER_FUEL_MIN_CONSUMPTION;
    }

    private boolean consumeFuel(float delta) {
        totalFuel = Math.max(0, totalFuel - fuelConsumptionRate * delta);

        return totalFuel > 0;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        updateTileStatus();
    }

    public float getFuel() {
        return totalFuel;
    }

    public void draw(SpriteBatch batch, float delta) {
        if (consumeFuel(delta)) {
            // Update movement based on controller input
            controller.updateController(delta);
        }

        super.draw(batch);
    }

    private boolean isAllowedToMoveUp() {
        TiledMapTile tile = map.getTileFromPosition(getX(), getY(), "background");
        Boolean value = map.getBoolean(tile, "sky");

        return value == null || !value;
    }

    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "shroud");

        if (cell != null) {
            cell.setTile(null);
        }
    }

    private void updatePlayerView() {
        playerView.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);

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
            map.getCellFromPosition(getX() + getWidth() / 2, getY(), "ground"),
            map.getCellFromPosition(getX() + getWidth() / 2, getY() + getHeight(), "ground"),
            map.getCellFromPosition(getX(), getY() + getHeight() / 2, "ground"),
            map.getCellFromPosition(getX() + getWidth(), getY() + getHeight() / 2, "ground"),
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

    public void rotateSprite(String direction, float delta) {
        /**
         * Rotates the sprite to the direction it moves to.
         *
         * String Direction is sent from the move method and this method records the direction
         * currently moving to on string called DrillPointsTo.
         */
        if (direction.equals("L")) {
            if (DrillPointsTO.equals("R")) {
                flip(true, false);
            }
            if (DrillPointsTO.equals("DL")) {
                rotate90(true);
            }
            if (DrillPointsTO.equals("DR")) {
                rotate90(false);
                flip(true, false);
            }
            if (DrillPointsTO.equals("UL")) {
                rotate90(false);
            }
            if (DrillPointsTO.equals("UR")) {
                rotate90(true);
                flip(true, false);
            }
            DrillPointsTO = "L";
        }
        if (direction.equals("R")) {
            if (DrillPointsTO.equals("L")) {
                flip(true, false);
            }
            if (DrillPointsTO.equals("DR")) {
                rotate90(false);
            }
            if (DrillPointsTO.equals("DL")) {
                rotate90(true);
                flip(true, false);
            }
            if (DrillPointsTO.equals("UR")) {
                rotate90(true);
            }
            if (DrillPointsTO.equals("UL")) {
                rotate90(false);
                flip(true, false);
            }
            DrillPointsTO ="R";
        }
        if (direction.equals("D")) {
            if (DrillPointsTO.equals("L")) {
                rotate90(false);
                DrillPointsTO = "DL";
            }
            if (DrillPointsTO.equals("R")) {
                rotate90(true);
                DrillPointsTO = "DR";
            }
            if (DrillPointsTO.equals("UL")) {
                flip(false, true);
                DrillPointsTO = "DL";
            }
            if (DrillPointsTO.equals("UR")) {
                flip(false, true);
                DrillPointsTO = "DR";
            }
        }
        if (direction.equals("U")) {
            if (DrillPointsTO.equals("L")) {
                rotate90(true);
                DrillPointsTO = "UL";
            }
            if (DrillPointsTO.equals("R")) {
                rotate90(false);
                DrillPointsTO = "UR";
            }
            if (DrillPointsTO.equals("DL")) {
                flip(false, true);
                DrillPointsTO = "UL";
            }
            if (DrillPointsTO.equals("DR")) {
                flip(false, true);
                DrillPointsTO = "UR";
            }

        }

    }

    @Override
    public void move(float accelerometerX, float accelerometerY, float delta) {
        if (isDirectionAllowed('R') && (accelerometerX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
            translateX( PLAYER_MOVE_SPEED * delta);
            accelerometerX = PLAYER_MOVE_SPEED;
            rotateSprite("R", delta);
        }
        if (isDirectionAllowed('L') && (accelerometerX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            translateX(-PLAYER_MOVE_SPEED * delta);
            accelerometerX = -PLAYER_MOVE_SPEED;
            rotateSprite("L", delta);
        }

        // Allow only one axis movement
        if (accelerometerX == 0) {
            if (isDirectionAllowed('U') && (isAllowedToMoveUp() && (accelerometerY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP)))) {
                translateY(PLAYER_MOVE_SPEED * delta);
                accelerometerY = PLAYER_MOVE_SPEED;
                rotateSprite("U", delta);
            }
            if (isDirectionAllowed('D') && (accelerometerY < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
                translateY(-PLAYER_MOVE_SPEED * delta);
                accelerometerY = -PLAYER_MOVE_SPEED;
                rotateSprite("D", delta);
            }
        }

        updateTileStatus();
    }
}