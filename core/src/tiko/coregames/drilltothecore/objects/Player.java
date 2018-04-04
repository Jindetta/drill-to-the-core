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
import tiko.coregames.drilltothecore.managers.SettingsManager;

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

    public Player(LevelManager map, LocalizationManager localizer, float x, float y) {
        super("images/player_atlas.png");

        controller = new ControllerManager();

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

        createPlayerUnit(x, y);
        setDefaultOrientation();
        setInitialScoreValues();
        setMaxFuel();
    }

    // TODO: Improve
    private void createPlayerUnit(float x, float y) {
        SettingsManager settings = SettingsManager.getDefaultProfile();
        int index = settings.getInteger("playerColor");

        TextureRegion bladeRegion = new TextureRegion(getTexture(), BIG_TILE_SIZE * 3, BIG_TILE_SIZE);
        playerUnit = new TextureRegion(getTexture(), BIG_TILE_SIZE * 3, index * BIG_TILE_SIZE, BIG_TILE_SIZE, BIG_TILE_SIZE);
        animation = new Animation<>(1 / 15f, getFrames(bladeRegion, 3));
        keyFrameState = 0;

        playerView = new Circle(x + BIG_TILE_SIZE / 2, y + BIG_TILE_SIZE / 2, PLAYER_VIEW_RADIUS);
        setPosition(x, y);
    }

    private TextureRegion[] getFrames(TextureRegion region, int frameCount) {
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(region, i * BIG_TILE_SIZE, 0, BIG_TILE_SIZE, BIG_TILE_SIZE);
        }

        return frames;
    }

    private void setDefaultOrientation() {
        defaultOrientation = 270;
        nextOrientation = 0;
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
            nextOrientation = orientation - getPlayerOrientation();
        }
    }

    // TODO: Fix rotation
    private boolean startRotation(float delta) {
        float orientation = getPlayerOrientation();

        /*if (nextOrientation != 0) {
            if (Math.abs(nextOrientation ) == 180) {
                setRotation(nextOrientation);
                nextOrientation = 0;
            } else {
                setCurrentState(PLAYER_STATES.TURNING);

                rotate(PLAYER_ROTATION_SPEED * delta);

                nextOrientation = Math.max(nextOrientation - PLAYER_ROTATION_SPEED * delta, 0);
                return true;
            }
        }*/

        return false;
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

            if (!startRotation(delta)) {

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
            }

            if (valueX != 0 || valueY != 0 || currentState == PLAYER_STATES.TURNING) {
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
                frame.getRegionWidth() / 2 + BIG_TILE_SIZE,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation()
            );

            // TODO: Fix - method for orientation
            batch.draw(
                playerUnit,
                getX() + BIG_TILE_SIZE - 1 + MathUtils.cos(getRotation()),
                getY() + MathUtils.sin(getRotation()),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation()
            );
        }
    }

    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "shroud");

        if (cell != null && cell.getTile() != null) {
            int tileSize = map.getTileSetSize("shroud");
            double distance = Math.hypot(playerView.x - x, playerView.y - y);
            double newIndex = Math.ceil((playerView.radius - distance) / (playerView.radius / tileSize));
            TiledMapTile tile = map.getTileByIndex("shroud", (int) newIndex);

            if (tile == null || cell.getTile().getId() < tile.getId()) {
                if (tile == null) {
                    addBonusScore(0.01f);
                }

                cell.setTile(tile);
            }
        }
    }

    /**
     * Updates player view.
     */
    private void updatePlayerView() {
        playerView.setPosition(getX() + BIG_TILE_SIZE / 2, getY() + BIG_TILE_SIZE / 2);

        for (float y = playerView.y - playerView.radius; y < playerView.y + playerView.radius; y += SMALL_TILE_SIZE) {
            for (float x = playerView.x - playerView.radius; x < playerView.x + playerView.radius; x += SMALL_TILE_SIZE) {
                if (playerView.contains(x, y)) {
                    clearShroudTile(x, y);
                }
            }
        }
    }

    /**
     * Gets localized collectible name.
     *
     * @param key   Collectible identifier
     * @return      Localized string
     */
    private String getCollectibleName(String key) {
        try {
            return localizer.getValue(key);
        } catch (Exception e) {
            return localizer.getValue("collectible");
        }
    }

    /**
     * Processes all collectibles.
     *
     * @param tile  Collectible tile
     * @param key   Collectible identifier
     */
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
            case FUEL_CANISTER_REFILL_RANDOM:
                totalFuel = MathUtils.clamp(totalFuel * MathUtils.random(10), totalFuel, PLAYER_FUEL_TANK_SIZE);
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
        x -= BIG_TILE_SIZE / 2 - SMALL_TILE_SIZE;
        y -= BIG_TILE_SIZE / 2 - SMALL_TILE_SIZE;

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

        // TODO: Change "ground" destruction based on current heading
        for (float y = getY() + SMALL_TILE_SIZE; y < getY() + BIG_TILE_SIZE - SMALL_TILE_SIZE; y++) {
            for (float x = getX() + SMALL_TILE_SIZE; x < getX() + BIG_TILE_SIZE - SMALL_TILE_SIZE; x++) {
                updateCollectibleStatus(x, y);
                updateGroundStatus(x, y);
            }
        }

        drillSpeedReduction = collisionInterval > 0 && drillTimer <= 0 ? PLAYER_DRILL_SPEED_REDUCTION : 0;
    }

    /**
     * Updates all in-game timers.
     *
     * @param delta Time delta
     */
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
            case 'R': return getX() + BIG_TILE_SIZE < map.getMapWidth();
            case 'U': return getY() < map.getMapHeight() - BIG_TILE_SIZE * 4;
            case 'D': return getY() > 0;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format(
            "Current points: %d\nDepth reached: %.0f\nTotal fuel: %.2f\n" +
            "Radar power-up: %.1f\nSpeed power-up: %.1f\nDrill speed power-up: %.1f\n" +
            "Point power-up: %.1f\n\n%s",
            getTotalScore(),
            getDrillDepth(),
            getFuel(),
            viewTimer,
            speedTimer,
            drillTimer,
            collectibleTimer,
            controller.toString()
        );
    }
}