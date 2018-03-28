package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import tiko.coregames.drilltothecore.managers.ControllerManager;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.LocalizationManager;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class Player extends BaseObject {
    private ControllerManager controller;
    private LocalizationManager localizer;
    private LevelManager map;

    private PLAYER_STATES currentState;

    private float maximumDrillDepth;
    private float totalFuel, fuelConsumptionRate;
    private float baseScore, bonusScore;
    private float scoreMultiplier;

    private float startingDepth;

    private float collectibleMultiplier, speedMultiplier, drillSpeedReduction;
    private float collectibleTimer, speedTimer, viewTimer, currentIdleTime, drillTimer, collisionInterval;

    private Circle playerView;

    private int nextOrientation;
    private int defaultOrientation;

    public Player(LevelManager map, LocalizationManager localizer, float x, float y) {
        super("images/player.png");
        controller = new ControllerManager();
        playerView = new Circle(x + getWidth() / 2, y + getHeight() / 2, PLAYER_VIEW_RADIUS);
        setPosition(x, y);

        startingDepth = y;

        this.map = map;
        this.localizer = localizer;

        drillSpeedReduction = 0;
        speedMultiplier = 1;
        collisionInterval = 0;
        drillTimer = 0;
        speedTimer = 0;
        viewTimer = 0;

        fuelConsumptionRate = PLAYER_FUEL_IDLE_MULTIPLIER;
        currentIdleTime = PLAYER_IDLE_STATE_DELAY;

        currentState = PLAYER_STATES.IDLE;
        setInitialScoreValues();
        setMaxFuel();
    }

    private void setDefaultOrientation() {
        defaultOrientation = 270;
        nextOrientation = 0;
    }

    private void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
    }

    private boolean consumeFuel(float delta) {
        totalFuel = Math.max(0, totalFuel - PLAYER_FUEL_MIN_CONSUMPTION * (fuelConsumptionRate * delta));

        return totalFuel > 0;
    }

    private void setInitialScoreValues() {
        collectibleTimer = 0;
        collectibleMultiplier = 1;
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

    private void increaseMaximumDepth() {
        maximumDrillDepth = Math.max(maximumDrillDepth, Math.round(startingDepth - getY()));

        //DEBUG
        scoreMultiplier = maximumDrillDepth / 150f;
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
        int rotateTo;
        if (getPlayerOrientation() < nextOrientation) {
            rotateTo = 45;
        } else  {
            rotateTo = -45;
        }

        if (nextOrientation != getPlayerOrientation()) {
            rotate(rotateTo * (5* delta));
        }
        if (getPlayerOrientation() == 359) {
            rotate(-359);
        }


        return false;
    }

    public float getFuel() {
        return totalFuel;
    }

    public void resetCalibration() {
        controller.reset();
        Gdx.app.log(getClass().getSimpleName(), "Calibration is reset");
    }

    private float getMovementSpeed() {
        return PLAYER_MOVE_SPEED * (speedMultiplier - drillSpeedReduction);
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        // Update movement based on controller input
        controller.update();

        if (consumeFuel(delta)) {
            float valueX = controller.getCurrentX();
            float valueY = controller.getCurrentY();

            if (isDirectionAllowed('R') && (valueX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
                translateX(getMovementSpeed() * delta);
                rotateSprite("R", delta);
                valueX = 1;
            }
            if (isDirectionAllowed('L') && (valueX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
                translateX(-getMovementSpeed() * delta);
                rotateSprite("L", delta);
                valueX = -1;
            }

            // Allow only one axis movement
            if (valueX == 0) {
                if (isDirectionAllowed('U') && (valueY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP))) {
                    translateY(getMovementSpeed() * delta);
                    rotateSprite("U", delta);
                    valueY = 1;
                }
                if (isDirectionAllowed('D') && (valueY < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
                    translateY(-getMovementSpeed() * delta);
                    rotateSprite("D", delta);
                    valueY = -1;
                }
            }

            if (valueX != 0 || valueY != 0) {
                currentState = PLAYER_STATES.ACTIVE;
                increaseMaximumDepth();
                updateTileStatus();
            } else {
                currentState = PLAYER_STATES.IDLE;
            }
        }

        updateTimerStatus(delta);
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

    private void collectItemByName(TiledMapTile tile, String key) {
        if (key == null) {
            key = map.getString(tile, "id", "");
        }

        switch (key) {
            case POWER_UP_RANDOMIZED:
                collectItemByName(tile, RANDOM_POWER_UPS[MathUtils.random(0, RANDOM_POWER_UPS.length - 1)]);
                return;
            case POWER_UP_RADAR_EXTENDER:
                playerView.setRadius(PLAYER_VIEW_RADIUS * 1.5f);
                viewTimer = 5;
                break;
            case POWER_UP_POINT_MULTIPLIER:
                collectibleMultiplier = 1.5f;
                collectibleTimer = 30;
                break;
            case POWER_UP_DRILL_MULTIPLIER:
                drillTimer = 15;
                drillSpeedReduction = 0;
                break;
            case POWER_UP_SPEED_MULTIPLIER:
                speedMultiplier = PLAYER_MOVEMENT_SPEED_MULTIPLIER;
                speedTimer = 10;
                break;
            case FUEL_CANISTER_REFILL_20:
            case FUEL_CANISTER_REFILL_50:
            case FUEL_CANISTER_REFILL_100:
                Float amount = map.getFloat(tile, "amount", 0f);

                float multiplier = PLAYER_FUEL_TANK_SIZE * (amount / 100);
                totalFuel = MathUtils.clamp(totalFuel + multiplier, totalFuel, PLAYER_FUEL_TANK_SIZE);
                break;
            default:
                Integer value = map.getInteger(tile, "value", 0);

                addBaseScore(value * collectibleMultiplier);
                break;
        }

        Gdx.app.log(getClass().getSimpleName(), getCollectibleName(key));
    }

    private void updateCollectibleStatus(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "collectibles");

        if (cell != null && cell.getTile() != null) {
            collectItemByName(cell.getTile(), null);
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

                    collisionInterval = .15f;
                }

                updateCollectibleStatus(x - getWidth() / 2, y - getHeight() / 2);
            }
        }

        drillSpeedReduction = collisionInterval > 0 && drillTimer <= 0 ? PLAYER_DRILL_SPEED_REDUCTION : 0;
    }

    private void updateTimerStatus(float delta) {
        if (viewTimer > 0) {
            viewTimer = Math.max(viewTimer - delta, 0);

            if (MathUtils.isZero(viewTimer)) {
                playerView.radius = PLAYER_VIEW_RADIUS;
            }
        }

        if (collectibleTimer > 0) {
            collectibleTimer = Math.max(collectibleTimer - delta, 0);

            if (MathUtils.isZero(collectibleTimer)) {
                collectibleMultiplier = 1;
            }
        }

        if (speedTimer > 0) {
            speedTimer = Math.max(speedTimer - delta, 0);

            if (MathUtils.isZero(speedTimer)) {
                speedMultiplier = 1;
            }
        }

        if (drillTimer > 0) {
            drillTimer = Math.max(drillTimer - delta, 0);
        }

        if (currentState == PLAYER_STATES.IDLE) {
            currentIdleTime = Math.min(currentIdleTime + delta, PLAYER_IDLE_STATE_DELAY);
        } else {
            currentIdleTime = 0;
        }

        float baseMultiplier = (1 + PLAYER_FUEL_IDLE_MULTIPLIER) - currentIdleTime / PLAYER_IDLE_STATE_DELAY;
        fuelConsumptionRate = Math.max(PLAYER_FUEL_IDLE_MULTIPLIER, Math.min(baseMultiplier, 1));

        collisionInterval = Math.max(collisionInterval - delta, 0);
    }

    private boolean isDirectionAllowed(char direction) {
        switch (direction) {
            case 'L': return getX() > 0;
            case 'R': return getX() + getWidth() < map.getMapWidth();
            case 'U': return getY() + getHeight() < map.getMapHeight() - TILE_HEIGHT * 3;
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
            if (getPlayerOrientation() <= 180) {
                setNewOrientation(PLAYER_ORIENTATION_UP);
            } else {
                setNewOrientation(359);
            }
        }
        if (direction.equals("R")) {
            setNewOrientation(PLAYER_ORIENTATION_DOWN);
        }
        if (direction.equals("D")) {
            setNewOrientation(PLAYER_ORIENTATION_RIGHT);
        }
        if (direction.equals("U")) {
            if (getPlayerOrientation() == 0) {
                rotate(350);
            }
            setNewOrientation(PLAYER_ORIENTATION_LEFT);
        }
        isRotatingToAxis(delta);

    }


    // DEBUG METHOD
    private String formatPowerUp(float timer) {
        if (timer > 0) {
            return String.format("%.2f", timer);
        }

        return "N/A";
    }

    @Override
    public String toString() {
        return String.format(
            "Current points: %d\nDepth reached: %.0f\nTotal fuel: %.2f\n" +
            "Radar power-up: %s\nSpeed power-up: %s\nDrill speed power-up: %s\nPoint power-up: %s\n\n%s",
            getTotalScore(),
            getDrillDepth(),
            getFuel(),
            formatPowerUp(viewTimer),
            formatPowerUp(speedTimer),
            formatPowerUp(drillTimer),
            formatPowerUp(collectibleTimer),
            controller.toString()
        );
    }
}