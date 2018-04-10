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

    private STATES currentState;

    private float maximumDrillDepth;
    private float totalFuel, fuelConsumptionRate;
    private float baseScore, bonusScore;
    private float scoreMultiplier;
    private float shroudOpacity;

    private float startingDepth;

    private float collectibleMultiplier, speedMultiplier, drillSpeedReduction;
    private float collectibleTimer, speedTimer, viewTimer, currentIdleTime, drillTimer, collisionInterval, fuelTimer;

    private Circle playerView;

    private float keyFrameState;
    private Animation<TextureRegion> animation;

    private TextureRegion playerUnit;
    private TextureRegion playerTracks;

    private boolean isAllowedToMoveForward;
    private boolean isAllowedToMoveBackward;
    private boolean isAllowedToRotateRight;
    private boolean isAllowedToRotateLeft;

    private String recentlyCollectedItem;

    private enum STATES {
        IDLE, ACTIVE, JAMMED, IMMOBILIZED, DONE
    }

    public Player(LevelManager map, float x, float y) {
        super("images/player_atlas.png");

        this.map = map;
        localizer = new LocalizationManager("game");

        // TODO: Cleanup initialization
        drillSpeedReduction = 0;
        speedMultiplier = 1;
        collisionInterval = 0;
        drillTimer = 0;
        speedTimer = 0;
        viewTimer = 0;
        shroudOpacity = 1;
        fuelTimer = 0;

        controller = new ControllerManager();
        fuelConsumptionRate = PLAYER_FUEL_IDLE_MULTIPLIER;
        currentIdleTime = PLAYER_IDLE_STATE_DELAY;

        createPlayerUnit(x, y);
        setInitialScoreValues();
        setMaxFuel();
    }

    // TODO: Dirty AF - improve
    private void createPlayerUnit(float x, float y) {
        SettingsManager settings = SettingsManager.getDefaultProfile();
        int index = settings.getInteger("playerColor");

        TextureRegion bladeRegion = new TextureRegion(getTexture(), 0, BIG_TILE_SIZE * 2,BIG_TILE_SIZE * 3, BIG_TILE_SIZE  );
        playerTracks = new TextureRegion(getTexture(), BIG_TILE_SIZE * 1, BIG_TILE_SIZE , BIG_TILE_SIZE, BIG_TILE_SIZE);
        playerUnit = new TextureRegion(getTexture(), BIG_TILE_SIZE * 4, index * BIG_TILE_SIZE, BIG_TILE_SIZE, BIG_TILE_SIZE);
        playerUnit.flip(false, true);

        animation = new Animation<>(1 / 20f, getFrames(bladeRegion, 3));
        keyFrameState = 0;

        startingDepth = y;
        playerView = new Circle(x + BIG_TILE_SIZE / 2, y + BIG_TILE_SIZE / 2, PLAYER_VIEW_RADIUS);
        setPosition(x, y);
        setRotation(90);
    }

    private TextureRegion[] getFrames(TextureRegion region, int frameCount) {
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(region, i * BIG_TILE_SIZE, 0, BIG_TILE_SIZE, BIG_TILE_SIZE);
            frames[i].flip(false, true);
        }

        return frames;
    }

    public void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
    }

    private boolean consumeFuel(float delta) {
        totalFuel -= PLAYER_FUEL_MIN_CONSUMPTION * (fuelConsumptionRate * delta);

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
        maximumDrillDepth = Math.max(maximumDrillDepth, startingDepth - getY());

        //TODO: Remove this when score is shown at the end of the game
        scoreMultiplier = Math.max(1, getDrillDepthMultiplier() * PLAYER_DRILL_DEPTH_MULTIPLIER);
    }

    private int getTotalScore() {
        return Math.round(baseScore * scoreMultiplier + bonusScore);
    }

    private float getDrillDepthMultiplier() {
        return Math.min(maximumDrillDepth / startingDepth, 1);
    }

    private float getDrillDepth() {
        return getDrillDepthMultiplier() * map.getVirtualDepth();
    }

    public float getFuel() {
        return Math.max(0, totalFuel);
    }

    public void resetCalibration() {
        controller.reset();
    }

    private float getMovementSpeed() {
        return PLAYER_MOVE_SPEED * (speedMultiplier - drillSpeedReduction);
    }

    private float getRotationSpeed() {
        return PLAYER_ROTATION_SPEED * speedMultiplier;
    }

    private void setCurrentState(STATES newState, STATES... ignoreStates) {
        if (ignoreStates != null && ignoreStates.length > 0) {
            for (STATES state : ignoreStates) {
                if (currentState == state) {
                    return;
                }
            }
        }

        currentState = newState;
    }

    private void moveToAngle(float speed) {
        translate(
            speed * MathUtils.cosDeg(getRotation()),
            speed * MathUtils.sinDeg(getRotation())
        );
    }

    public String getRecentlyCollected() {
        return recentlyCollectedItem;
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        // Update movement based on controller input
        controller.update();

        if (!controller.isCalibrating()) {
            // Set state to idle by default
            setCurrentState(STATES.IDLE, STATES.JAMMED, STATES.IMMOBILIZED);

            recentlyCollectedItem = null;

            if (consumeFuel(delta) && currentState != STATES.IMMOBILIZED) {
                float valueX = controller.getCurrentX();
                float valueY = controller.getCurrentY();

                if (currentState != STATES.JAMMED) {
                    checkMovementConditions(delta);
                } else {
                    isAllowedToMoveForward = false;
                    isAllowedToRotateRight = false;
                    isAllowedToRotateLeft = false;
                }

                if (isAllowedToMoveBackward && (valueY > 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN))) {
                    moveToAngle(getMovementSpeed() * delta);
                    valueY = 1;
                }
                if (isAllowedToMoveForward && (valueY < 0 || Gdx.input.isKeyPressed(Input.Keys.UP))) {
                    moveToAngle(-getMovementSpeed() * delta);
                    valueY = -1;
                }

                if (isAllowedToRotateRight && (valueX > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
                    rotate(-getRotationSpeed() * delta);
                    valueX = 1;
                }
                if (isAllowedToRotateLeft && (valueX < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
                    rotate(getRotationSpeed() * delta);
                    valueX = -1;
                }

                if (valueX != 0 || valueY != 0) {
                    setCurrentState(STATES.ACTIVE);
                    keyFrameState += delta;

                    increaseMaximumDepth();
                    updateTileStatus();
                }
            }

            updateTimerStatus(delta);
        }

        draw(batch);
    }

    @Override
    public void draw(Batch batch) {
        if (isVisible()) {
            // TODO: Do code cleanup - this is messy AF
            TextureRegion frame = animation.getKeyFrame(keyFrameState, true);

            batch.draw(
                playerTracks, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() -90
            );

            batch.draw(
                playerUnit, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() -90
            );

            batch.draw(
                frame,
                getX() + MathUtils.cosDeg(getRotation()),
                getY() - BIG_TILE_SIZE + MathUtils.sinDeg(getRotation()),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2 + BIG_TILE_SIZE,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() -90
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
                if (newIndex >= tileSize / 2) {
                    addBonusScore(SCORE_TILE_REVEALED);
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

        // TODO: Too dirty - make code cleanup
        switch (key) {
            case POWER_UP_RANDOMIZED:
                collectItemByName(tile, RANDOM_POWER_UPS[MathUtils.random(0, RANDOM_POWER_UPS.length - 1)]);
                return;
            case POWER_UP_RADAR_EXTENDER:
                addBonusScore(SCORE_POWER_UP_PICKUP);
                viewTimer = 5;
                break;
            case POWER_UP_POINT_MULTIPLIER:
                addBonusScore(SCORE_POWER_UP_PICKUP);
                collectibleMultiplier = 1.5f;
                collectibleTimer = 30;
                break;
            case POWER_UP_DRILL_MULTIPLIER:
                addBonusScore(SCORE_POWER_UP_PICKUP);
                drillTimer = 15;
                drillSpeedReduction = 0;
                break;
            case POWER_UP_SPEED_MULTIPLIER:
                addBonusScore(SCORE_POWER_UP_PICKUP);
                speedMultiplier = PLAYER_MOVEMENT_SPEED_MULTIPLIER;
                speedTimer = 10;
                break;
            case POWER_UP_UNLIMITED_FUEL:
                fuelTimer = 7.5f;
                break;
            case FUEL_CANISTER_REFILL_20:
            case FUEL_CANISTER_REFILL_50:
            case FUEL_CANISTER_REFILL_100:
                Float amount = map.getFloat(tile, "amount", 0f);

                float multiplier = PLAYER_FUEL_TANK_SIZE * (amount / 100);
                totalFuel = MathUtils.clamp(totalFuel + multiplier, totalFuel, PLAYER_FUEL_TANK_SIZE);
                break;
            default:
                Integer baseValue = map.getInteger(tile, "value", 0);

                addBaseScore(baseValue * collectibleMultiplier);

                Float bonusValue = map.getFloat(tile, "bonus", 0f);

                addBonusScore(bonusValue);
                break;
        }

        if (tile != null) {
            recentlyCollectedItem = getCollectibleName(key);
        }
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

        // TODO: Make sure collectibles are picked up at the right time
        if (cell != null && cell.getTile() != null) {
            collectItemByName(cell.getTile(), null);
            cell.setTile(null);
        }
    }

    private void updateGroundStatus(float x, float y) {
        TiledMapTileLayer.Cell groundCell = map.getCellFromPosition(x, y, "ground");

        if (groundCell != null && groundCell.getTile() != null) {
            TiledMapTileLayer.Cell obstacleCell = map.getCellFromPosition(x, y, "obstacles");

            if (obstacleCell != null && obstacleCell.getTile() != null) {
                switch (map.getString(obstacleCell.getTile(), "id", "")) {
                    // TODO: Update to work with hard "rock" properly - and make constant for this type
                    case "rock":
                        setCurrentState(STATES.JAMMED);
                        controller.setSpecialMovement(true);
                        break;
                    // TODO: Update to work with this type - and make constant for this type
                    case "lava":
                        break;
                    // TODO: Update to work with this type - and make constant for this type
                    case "water":
                        break;
                    default:
                        obstacleCell.setTile(null);
                        updateGroundStatus(x, y);
                        return;
                }

                collisionInterval = .25f;
                return;
            }

            collisionInterval = .15f;
            addBaseScore(SCORE_GROUND_TILE_OPENED);
            groundCell.setTile(null);
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
        if (viewTimer > 0 || shroudOpacity < 1) {
            viewTimer = Math.max(viewTimer - delta, 0);

            if (viewTimer <= 0 && shroudOpacity < 1) {
                shroudOpacity = Math.min(1, shroudOpacity + delta);
                map.setShroudLayerOpacity(MathUtils.lerp(0, 1, Math.min(1, shroudOpacity)));
            } else {
                shroudOpacity = Math.max(0, shroudOpacity - delta);
                map.setShroudLayerOpacity(MathUtils.lerp(0, 1, shroudOpacity));
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

        if (currentState == STATES.IDLE) {
            currentIdleTime = Math.min(currentIdleTime + delta, PLAYER_IDLE_STATE_DELAY);
        } else {
            currentIdleTime = 0;
        }

        float baseMultiplier = (1 + PLAYER_FUEL_IDLE_MULTIPLIER) - currentIdleTime / PLAYER_IDLE_STATE_DELAY;
        fuelConsumptionRate = Math.max(PLAYER_FUEL_IDLE_MULTIPLIER, Math.min(baseMultiplier, 1));

        if (fuelTimer > 0) {
            fuelTimer = Math.max(fuelTimer - delta, 0);

            if (fuelTimer > 0) {
                fuelConsumptionRate = 0;
            }
        }

        collisionInterval = Math.max(collisionInterval - delta, 0);
    }

    // TODO: Change to disallow rotation to awkward directions and movement over border
    private void checkMovementConditions(float delta) {
        float sin = MathUtils.sinDeg(getRotation() + getRotationSpeed() * delta);
        float cos = MathUtils.cosDeg(getRotation() + getRotationSpeed() * delta);

        final float ROTATION = getRotationSpeed() * delta;
        final float GROUND_LEVEL = map.getMapHeight() - BIG_TILE_SIZE * 4;

        float originX = getX() + BIG_TILE_SIZE / 2;
        float originY = getY() + BIG_TILE_SIZE / 2;

        float radius = BIG_TILE_SIZE * 1.5f;

        isAllowedToRotateLeft = true;
        isAllowedToRotateRight = true;

        // Ground
        if (getY() >= GROUND_LEVEL) {
            if (originY + radius * cos >= GROUND_LEVEL) {
                isAllowedToRotateRight = false;
            }
            if (originY - radius * cos >= GROUND_LEVEL) {
                isAllowedToRotateLeft = false;
            }
        }

        // Left side
        if (getX() - BIG_TILE_SIZE * 2 <= 0) {
            if (originX + radius * MathUtils.cosDeg(getRotation() - ROTATION) <= 0) {
                isAllowedToRotateRight = false;
            }
            if (originX - radius * MathUtils.cosDeg(getRotation() + ROTATION) <= 0) {
                isAllowedToRotateLeft = false;
            }
        }

        // Right side
        if (getX() + BIG_TILE_SIZE * 2 >= map.getMapWidth()) {
            if (originX + radius * MathUtils.cosDeg(getRotation() - ROTATION) >= map.getMapWidth()) {
                isAllowedToRotateRight = false;
            }
            if (originX - radius * MathUtils.cosDeg(getRotation() + ROTATION) >= map.getMapWidth()) {
                isAllowedToRotateLeft = false;
            }
        }

        isAllowedToMoveForward = true;
        isAllowedToMoveBackward = true;
    }

    @Override
    // TODO: Remove before release
    public String toString() {
        return String.format(
            "Current points: %d\nDepth reached: %.0f m\nTotal fuel: %.2f\n" +
            "Radar power-up: %.1f\nSpeed power-up: %.1f\nDrill speed power-up: %.1f\n" +
            "Point power-up: %.1f\nFuel power-up: %.1f\n%s",
            getTotalScore(),
            getDrillDepth(),
            getFuel(),
            viewTimer,
            speedTimer,
            drillTimer,
            collectibleTimer,
            fuelTimer,
            controller.toString()
        );
    }
}