package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.*;
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

    private float nextOrientation;
    private float defaultOrientation;

    private float keyFrameState;
    private Animation<TextureRegion> animation;

    private TextureRegion playerUnit;

    private final TiledMapTile[] VIEW_TILES;

    public Player(LevelManager map, LocalizationManager localizer, float x, float y) {
        super("images/player_atlas.png");

        controller = new ControllerManager();

        startingDepth = y;

        this.map = map;
        this.localizer = localizer;
        VIEW_TILES = map.getViewTiles();

        drillSpeedReduction = 0;
        speedMultiplier = 1;
        collisionInterval = 0;
        drillTimer = 0;
        speedTimer = 0;
        viewTimer = 0;

        fuelConsumptionRate = PLAYER_FUEL_IDLE_MULTIPLIER;
        currentIdleTime = PLAYER_IDLE_STATE_DELAY;

        // TODO: Make use of AnimationSet (for the blade)
        TextureRegion bladeRegion = new TextureRegion(getTexture(), TILE_WIDTH * 3, TILE_HEIGHT);
        playerUnit = new TextureRegion(getTexture(), TILE_WIDTH * 3, 0, TILE_WIDTH, TILE_HEIGHT);
        animation = new Animation<>(1 / 15f, getFrames(bladeRegion, 3));
        keyFrameState = 0;

        playerView = new Circle(x + TILE_WIDTH / 2, y + TILE_HEIGHT / 2, PLAYER_VIEW_RADIUS);
        setPosition(x, y);

        setDefaultOrientation();
        setInitialScoreValues();
        setMaxFuel();
    }

    private TextureRegion[] getFrames(TextureRegion region, int frameCount) {
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(region, i * TILE_WIDTH, 0, TILE_WIDTH, TILE_HEIGHT);
        }

        return frames;
    }

    private void setDefaultOrientation() {
        defaultOrientation = 270;
        nextOrientation = 270;
    }

    public void setMaxFuel() {
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

    public float getPlayerOrientation() {
        float rotation = Math.abs(defaultOrientation + getRotation()) % 360;
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

    private void setNewOrientation(float orientation) {
        if (currentState != PLAYER_STATES.TURNING) {
            nextOrientation = orientation;
        }
    }

    // TODO: Fix rotation
    private void startRotation(float delta) {
        float orientation = getPlayerOrientation();

        if (nextOrientation != orientation) {
            float difference = nextOrientation - orientation;

            if (MathUtils.isEqual(Math.abs(difference), 180)) {
                setRotation(difference);
            } else {
                setCurrentState(PLAYER_STATES.TURNING);

                if (nextOrientation > orientation) {
                    rotate(PLAYER_ROTATION_SPEED % difference * delta);
                } else if (nextOrientation < orientation) {
                    rotate(-PLAYER_ROTATION_SPEED % difference * delta);
                }
            }
        }
    }

    public float getFuel() {
        return totalFuel;
    }

    public void resetCalibration() {
        controller.reset();
    }

    private float getMovementSpeed() {
        return PLAYER_MOVE_SPEED * (speedMultiplier - drillSpeedReduction);
    }

    private void setCurrentState(PLAYER_STATES newState, PLAYER_STATES... ignoreStates) {
        if (ignoreStates != null && ignoreStates.length > 0) {
            for (PLAYER_STATES state : ignoreStates) {
                if (currentState == state) {
                    return;
                }
            }
        }

        currentState = newState;
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        // Update movement based on controller input
        controller.update();
        // Set state to idle by default
        setCurrentState(PLAYER_STATES.IDLE);

        if (consumeFuel(delta)) {
            float valueX = controller.getCurrentX();
            float valueY = controller.getCurrentY();

            if (isDirectionAllowed('U') && (valueY > 0 || Gdx.input.isKeyPressed(Input.Keys.UP))) {
                translateY(getMovementSpeed() * delta);
                setNewOrientation(PLAYER_ORIENTATION_UP);
                valueY = 1;
            }
            if (isDirectionAllowed('D') && (valueY < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
                translateY(-getMovementSpeed() * delta);
                setNewOrientation(PLAYER_ORIENTATION_DOWN);
                valueY = -1;
            }

            // Allow only one axis movement
            if (valueY == 0) {
                if (isDirectionAllowed('R') && (valueX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
                    translateX(getMovementSpeed() * delta);
                    setNewOrientation(PLAYER_ORIENTATION_RIGHT);
                    valueX = 1;
                }
                if (isDirectionAllowed('L') && (valueX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
                    translateX(-getMovementSpeed() * delta);
                    setNewOrientation(PLAYER_ORIENTATION_LEFT);
                    valueX = -1;
                }
            }

            startRotation(delta);

            if (valueX != 0 || valueY != 0) {
                setCurrentState(PLAYER_STATES.ACTIVE, PLAYER_STATES.TURNING);
                keyFrameState += delta;

                increaseMaximumDepth();
                updateTileStatus();
            }
        }

        updateTimerStatus(delta);
        draw(batch);
    }

    @Override
    public void draw(Batch batch) {
        if (isVisible()) {
            TextureRegion frame = animation.getKeyFrame(keyFrameState, true);

            batch.draw(
                frame, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation()
            );

            // TODO: Fix overlapping "unit" and "blade"
            batch.draw(
                playerUnit, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation()
            );
        }
    }

    private int calculateDistance(float x, float y, int value) {
        double distance = Math.sqrt(Math.pow(playerView.x - x, 2) + Math.pow(playerView.y - y, 2));

        if (distance / 3 > playerView.radius / VIEW_TILES.length) {
            return (int) (distance / (playerView.radius / value));
        }

        return -1;
    }

    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "shroud");

        if (cell != null && cell.getTile() != null) {
            Integer value = map.getInteger(cell.getTile(), "view", VIEW_TILES.length);
            int tileIndex = calculateDistance(x, y, value);

            if (tileIndex >= 0) {
                cell.setTile(VIEW_TILES[tileIndex % value]);
            } else {
                addBonusScore(0.01f);
                cell.setTile(null);
            }
        }
    }

    private void updatePlayerView() {
        playerView.setPosition(getX() + TILE_WIDTH / 2, getY() + TILE_HEIGHT / 2);

        for (float y = playerView.y - playerView.radius; y < playerView.y + playerView.radius; y += 8) {
            for (float x = playerView.x - playerView.radius; x < playerView.x + playerView.radius; x += 8) {
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

    public void collectItemByName(TiledMapTile tile, String key) {
        if (key == null) {
            key = map.getString(tile, "id", POWER_UP_NOTHING);
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

    /**
     * Updates collectible state.
     *
     * @param x     X index
     * @param y     Y index
     */
    private void updateCollectibleStatus(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "collectibles");

        if (cell != null && cell.getTile() != null) {
            collectItemByName(cell.getTile(), null);
            cell.setTile(null);
        }
    }

    private void updateGroundStatus(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "ground");

        if (cell != null && cell.getTile() != null) {
            // 1 point per 8 tiles
            addBaseScore(0.125f);
            cell.setTile(null);

            collisionInterval = .15f;
        }
    }

    /**
     * Updates map view.
     */
    private void updateTileStatus() {
        updatePlayerView();

        float x, y;

        // TODO: Change "ground" destruction based on current heading
        switch (Math.round(getPlayerOrientation())) {
            case PLAYER_ORIENTATION_DOWN:
                for (x = getX(); x < getX() + TILE_WIDTH; x++) {
                    updateGroundStatus(x, getY() + TILE_HEIGHT);
                }
                break;
            case PLAYER_ORIENTATION_UP:
                for (x = getX(); x < getX() + TILE_WIDTH; x++) {
                    updateGroundStatus(x, getY());
                }
                break;
            case PLAYER_ORIENTATION_LEFT:
                for (y = getY(); y < getY() + TILE_HEIGHT; y++) {
                    updateGroundStatus(getX() + TILE_WIDTH, y);
                }
                break;
            case PLAYER_ORIENTATION_RIGHT:
                for (y = getY(); y < getY() + TILE_HEIGHT; y++) {
                    updateGroundStatus(getX(), y);
                }
                break;
            default:
                updateGroundStatus(getX() + TILE_WIDTH / 2, getY() + TILE_HEIGHT / 2);
                break;
        }

        /*for (float y = getY(); y < getY() + TILE_WIDTH; y++) {
            for (float x = getX(); x < getX() + TILE_HEIGHT; x++) {
                updateGroundStatus(x, y);
                updateCollectibleStatus(x - TILE_WIDTH / 2, y - TILE_HEIGHT / 2);
            }
        }*/

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
            case 'R': return getX() + TILE_WIDTH < map.getMapWidth();
            case 'U': return getY() < map.getMapHeight() - TILE_HEIGHT * 4;
            case 'D': return getY() > 0;
        }

        return false;
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
            "Radar power-up: %s\nSpeed power-up: %s\nDrill speed power-up: %s\n" +
            "Point power-up: %s\n\nRotation: %.2f (%.2f)\n%s",
            getTotalScore(),
            getDrillDepth(),
            getFuel(),
            formatPowerUp(viewTimer),
            formatPowerUp(speedTimer),
            formatPowerUp(drillTimer),
            formatPowerUp(collectibleTimer),
            getPlayerOrientation(),
            nextOrientation,
            controller.toString()
        );
    }
}