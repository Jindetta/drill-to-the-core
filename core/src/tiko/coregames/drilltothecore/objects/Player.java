package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.SettingsManager;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;
    private LevelManager map;

    private float totalFuel, fuelConsumptionRate;
    private Circle playerView;

    private String playerOrientation = "L";

    public Player(LevelManager map) {
        super("images/player.png");
        controller = new ControllerManager();
        playerView = new Circle(getX(), getY(), getWidth() * PLAYER_VIEW_MULTIPLIER);

        SettingsManager settings = SettingsManager.getUserProfiles();

        float sensitivityX = settings.getFloat("sensitivityX");
        float sensitivityY = settings.getFloat("sensitivityY");
        boolean invertedY = settings.getBoolean("invertedY");

        controller.setXThreshold(sensitivityX, sensitivityX);
        controller.setYThreshold(sensitivityY, sensitivityY);
        controller.setInvertedY(invertedY);

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
            controller.updateController();

            float accelerometerX = controller.getCurrentX();
            float accelerometerY = controller.getCurrentY();

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
                if (isDirectionAllowed('U') && (accelerometerY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP))) {
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

        super.draw(batch);
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
            for (float x = playerView.x - playerView.radius; x < playerView.x + playerView.radius; x++) {
                if (playerView.contains(x, y)) {
                    clearShroudTile(x, y);
                }
            }
        }
    }

    // DEBUG - Destroy tiles
    private void updateTileStatus() {
        updatePlayerView();

        TiledMapTileLayer.Cell cell = map.getCellFromPosition(getX() + getWidth() / 2, getY() + getHeight() / 2, "ground");

        if (cell != null) {
            cell.setTile(null);
        }
    }

    private boolean isDirectionAllowed(char direction) {
        switch (direction) {
            case 'L':
                return getX() > 0;
            case 'R':
                return getX() + getWidth() < TOTAL_TILES_WIDTH;
            case 'U':
                if (getY() + getHeight() < TOTAL_TILES_HEIGHT) {
                    TiledMapTile tile = map.getTileFromPosition(getX(), getY(), "background");
                    Boolean value = map.getBoolean(tile, "sky");

                    return value == null || !value;
                }

                break;
            case 'D':
                return getY() > 0;
        }

        return false;
    }

    private void rotateSprite(String direction, float delta) {

        /*
         * Rotates the sprite to the direction it moves to.
         *
         * String Direction is sent from the move method and this method records the direction
         * currently moving to on string called DrillPointsTo.
         */
        if (direction.equals("L")) {
            if (playerOrientation.equals("R")) {
                flip(true, false);
            }
            if (playerOrientation.equals("DL")) {
                rotate90(true);
            }
            if (playerOrientation.equals("DR")) {
                rotate90(false);
                flip(true, false);
            }
            if (playerOrientation.equals("UL")) {
                rotate90(false);
            }
            if (playerOrientation.equals("UR")) {
                rotate90(true);
                flip(true, false);
            }
            playerOrientation = "L";
        }
        if (direction.equals("R")) {
            if (playerOrientation.equals("L")) {
                flip(true, false);
            }
            if (playerOrientation.equals("DR")) {
                rotate90(false);
            }
            if (playerOrientation.equals("DL")) {
                rotate90(true);
                flip(true, false);
            }
            if (playerOrientation.equals("UR")) {
                rotate90(true);
            }
            if (playerOrientation.equals("UL")) {
                rotate90(false);
                flip(true, false);
            }
            playerOrientation ="R";
        }
        if (direction.equals("D")) {
            if (playerOrientation.equals("L")) {
                rotate90(false);
                playerOrientation = "DL";
            }
            if (playerOrientation.equals("R")) {
                rotate90(true);
                playerOrientation = "DR";
            }
            if (playerOrientation.equals("UL")) {
                rotate(180);
                playerOrientation = "DL";
            }
            if (playerOrientation.equals("UR")) {
                rotate(180);
                playerOrientation = "DR";
            }
        }
        if (direction.equals("U")) {
            if (playerOrientation.equals("L")) {
                rotate90(true);
                playerOrientation = "UL";
            }
            if (playerOrientation.equals("R")) {
                rotate90(false);
                playerOrientation = "UR";
            }
            if (playerOrientation.equals("DL")) {
                rotate(180);
                playerOrientation = "UL";
            }
            if (playerOrientation.equals("DR")) {
                rotate (180);
                playerOrientation = "UR";
            }

        }

    }
}