package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.LocalizationManager;
import tiko.coregames.drilltothecore.utilities.Debug;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class Player extends BaseObject {
    private ControllerManager controller;
    private LocalizationManager localizer;
    private LevelManager map;

    private float maximumDrillDepth;
    private float totalFuel, fuelConsumptionRate;
    private float baseScore, bonusScore;
    private float scoreMultiplier;
    private Circle playerView;

    private int nextOrientation;
    private int defaultOrientation;
    private String playerOrientation;

    public Player(LevelManager map, LocalizationManager localizer, float x, float y) {
        super("images/player.png");
        controller = new ControllerManager();
        playerView = new Circle(x + getWidth() / 2, y + getHeight() / 2, PLAYER_VIEW_RADIUS);
        setPosition(x, y);

        this.map = map;
        this.localizer = localizer;
        playerOrientation = "L";

        setMaxFuel();
        setInitialScoreValues();
    }

    private void setDefaultOrientation() {
        defaultOrientation = 270;
        nextOrientation = 0;
    }

    private void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
        fuelConsumptionRate = PLAYER_FUEL_MIN_CONSUMPTION;
    }

    private boolean consumeFuel(float delta) {
        totalFuel = Math.max(0, totalFuel - fuelConsumptionRate * delta);

        return totalFuel > 0;
    }

    private void setInitialScoreValues() {
        scoreMultiplier = 1;
        bonusScore = 0;
        baseScore = 0;
    }

    private void addBaseScore(float value) {
        baseScore += value;
    }

    private void addBonusScore(float value) {
        bonusScore += value;
    }

    private void increaseScoreMultiplier() {
        maximumDrillDepth = Math.max(maximumDrillDepth, TOTAL_TILES_HEIGHT - getY());
        scoreMultiplier = maximumDrillDepth / 100f;
    }

    public int getTotalScore() {
        return Math.round(baseScore * scoreMultiplier + bonusScore);
    }

    public int getPlayerOrientation() {
        int rotation = defaultOrientation + Math.round(Math.abs(getRotation()) % 360);
        boolean isStraightAngle = rotation % 90 == 0;

        if (rotation >= 0 && rotation < 90) {
            return isStraightAngle ? PLAYER_ORIENTATION_UP : PLAYER_ORIENTATION_UP_RIGHT;
        } else if (rotation >= 90 && rotation < 180) {
            return isStraightAngle ? PLAYER_ORIENTATION_RIGHT : PLAYER_ORIENTATION_DOWN_RIGHT;
        } else if (rotation >= 180 && rotation < 270) {
            return isStraightAngle ? PLAYER_ORIENTATION_DOWN : PLAYER_ORIENTATION_DOWN_LEFT;
        } else {
            return isStraightAngle ? PLAYER_ORIENTATION_LEFT : PLAYER_ORIENTATION_UP_LEFT;
        }
    }

    public float getDrillDepth() {
        return maximumDrillDepth;
    }

    public void setNewOrientation(int orientation) {
        nextOrientation = orientation;
    }

    public boolean isRotatingToAxis(float delta) {
        if (nextOrientation != 0 || getPlayerOrientation() % 90 != 0) {
            rotate(45 * delta);

            return true;
        }

        return false;
    }

    public float getFuel() {
        return totalFuel;
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        if (consumeFuel(delta)) {
            // Update movement based on controller input
            controller.updateController(delta);

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

            increaseScoreMultiplier();
            updateTileStatus();
        }

        super.draw(batch);
    }

    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "shroud");

        if (cell != null && cell.getTile() != null) {
            addBonusScore(0.01f);
            cell.setTile(null);
        }
    }

    private void updatePlayerView() {
        playerView.setPosition(getX() + getWidth() / 2, getY() + getHeight() / 2);

        // TODO: Change to half circle view based on current heading
        for (float y = playerView.y - playerView.radius; y < playerView.y + playerView.radius; y++) {
            for (float x = playerView.x - playerView.radius; x < playerView.x + playerView.radius; x++) {
                if (playerView.contains(x, y)) {
                    clearShroudTile(x, y);
                }
            }
        }
    }

    private String getCollectibleName(String key) {
        try {
            return localizer.getValue(key);
        } catch (Exception e) {
            return localizer.getValue("collectible");
        }
    }

    private void updateCollectibleStatus(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "collectibles");

        if (cell != null && cell.getTile() != null) {
            String key = map.getString(cell.getTile(), "id");
            Integer value = map.getInteger(cell.getTile(), "value");

            if (value != null) {
                addBaseScore(value);
                Gdx.app.log(getClass().getSimpleName(), "Collected: " + getCollectibleName(key));
            }

            cell.setTile(null);
        }
    }

    // DEBUG - Destroy tiles
    private void updateTileStatus() {
        updatePlayerView();

        // TODO: Change "ground" destruction based on current heading
        for (float y = getY(); y < getY() + getHeight(); y++) {
            for (float x = getX(); x < getX() + getWidth(); x++) {
                TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "ground");

                if (cell != null && cell.getTile() != null) {
                    // 1 point per 8 tiles
                    addBaseScore(0.125f);
                    cell.setTile(null);
                }

                updateCollectibleStatus(x - getWidth() / 2, y);
            }
        }
    }

    private boolean isDirectionAllowed(char direction) {
        switch (direction) {
            case 'L': return getX() > 0;
            case 'R': return getX() + getWidth() < TOTAL_TILES_WIDTH;
            case 'U': return getY() + getHeight() < TOTAL_TILES_HEIGHT - TILE_HEIGHT * 3;
            case 'D': return getY() > 0;
        }

        return false;
    }

    // TODO: Simplify
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