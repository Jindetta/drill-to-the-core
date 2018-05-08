package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import tiko.coregames.drilltothecore.managers.*;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * Player class will process all player actions.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class Player extends BaseObject {
    /**
     * Defines manager for sound effects.
     */
    private SoundManager soundEffects;

    /**
     * Defines manager for controls.
     */
    private ControllerManager controller;

    /**
     * Defines manager for level data.
     */
    private LevelManager map;

    /**
     * Defines current player state.
     */
    private STATES currentState;

    /**
     * Defines current maximum depth.
     */
    private float maximumDrillDepth;

    /**
     * Defines current fuel amount.
     */
    private float totalFuel;

    /**
     * Defines current fuel consumption rate.
     */
    private float fuelConsumptionRate;

    /**
     * Defines current bonus score.
     */
    private float bonusScore;

    /**
     * Defines current base score.
     */
    private long baseScore;

    /**
     * Defines score multiplier.
     */
    private float scoreMultiplier;

    /**
     * Defines current shroud opacity.
     */
    private float shroudOpacity;

    /**
     * Defines starting depth.
     */
    private float startingDepth;

    /**
     * Defines various multipliers.
     */
    private float collectibleMultiplier, speedMultiplier, drillSpeedReduction;

    /**
     * Defines various timers.
     */
    private float collectibleTimer, speedTimer, viewTimer, currentIdleTime, drillTimer, collisionInterval, fuelTimer;

    /**
     * Defines player view zone.
     */
    private Circle playerView;

    /**
     * Defines current frame time.
     */
    private float keyFrameState;

    /**
     * Defines animation info.
     */
    private Animation<TextureRegion> animation;

    /**
     * Defines region for player unit.
     */
    private TextureRegion playerUnit;

    /**
     * Defines region for player tracks.
     */
    private TextureRegion playerTracks;

    /**
     * Defines movement state (down).
     */
    private boolean isAllowedToMoveDown;

    /**
     * Defines movement state (up).
     */
    private boolean isAllowedToMoveUp;

    /**
     * Defines movement state (right).
     */
    private boolean isAllowedToMoveRight;

    /**
     * Defines movement state (left).
     */
    private boolean isAllowedToMoveLeft;

    /**
     * Stores recently added score.
     */
    private int recentlyAddedScore;

    /**
     * Stores rocket trail colors.
     */
    private float[] defaultRocketColor, boostedRocketColor;

    /**
     * Defines all possible states.
     */
    private enum STATES {
        IDLE, ACTIVE, DONE
    }

    /**
     * Stores particle effect.
     */
    private ParticleEffect effect;

    /**
     * Overloads default constructor.
     *
     * @param map       instance of LevelManager
     * @param x         x coordinate for player spawn
     * @param y         y coordinate for player spawn
     * @param sounds    instance of SoundManager
     */
    public Player(LevelManager map, float x, float y, SoundManager sounds, AssetManager assets) {
        super("images/player-atlas.png", assets);

        sounds.addSound("collect", "sounds/item-pickup.mp3", false);
        sounds.addSound("engine", "sounds/engine.mp3", true);
        soundEffects = sounds;
        this.map = map;

        defaultRocketColor = new float[] {
            0.7529412f, 0.3529412f, 0.007843138f,
            1f, 0.06666667f, 0.06666667f
        };

        boostedRocketColor = new float[] {
            0.105882354f, 0.20392157f, 0.7529412f,
            0.043137256f, 0.28627452f, 1f
        };

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

        assets.load("images/fx/player.fx", ParticleEffect.class);
        assets.finishLoadingAsset("images/fx/player.fx");

        effect = assets.get("images/fx/player.fx", ParticleEffect.class);
        effect.start();

        createPlayerUnit(x, y);
        setInitialScoreValues();
        setMaxFuel();
    }

    /**
     * Creates player unit at given coordinates.
     *
     * @param x     coordinate x
     * @param y     coordinate y
     */
    private void createPlayerUnit(float x, float y) {
        SettingsManager settings = SettingsManager.getActiveProfile(true);
        int index = settings.getIntegerIfExists("playerColor", 0);

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

    /**
     * Gets valid region for animation.
     *
     * @param region        region to convert
     * @param frameCount    frame count
     * @return single dimensional array
     */
    private TextureRegion[] getFrames(TextureRegion region, int frameCount) {
        TextureRegion[] frames = new TextureRegion[frameCount];

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new TextureRegion(region, i * BIG_TILE_SIZE, 0, BIG_TILE_SIZE, BIG_TILE_SIZE);
            frames[i].flip(false, true);
        }

        return frames;
    }

    /**
     * Sets maximum amount of fuel
     */
    public void setMaxFuel() {
        totalFuel = PLAYER_FUEL_TANK_SIZE;
    }

    /**
     * Consumes fuel.
     *
     * @param delta delta time
     * @return true if fuel has not run out, otherwise false
     */
    private boolean consumeFuel(float delta) {
        totalFuel -= PLAYER_FUEL_MIN_CONSUMPTION * (fuelConsumptionRate * delta);

        return totalFuel > 0;
    }

    /**
     * Sets initial score values.
     */
    private void setInitialScoreValues() {
        collectibleTimer = 0;
        collectibleMultiplier = 1;
        scoreMultiplier = 1;
        bonusScore = 0;
        baseScore = 0;
    }

    /**
     * Adds base score.
     *
     * @param value score to add
     */
    private void addBaseScore(float value) {
        baseScore += value;
    }

    /**
     * Adds bonus score.
     *
     * @param value score to add
     */
    public void addBonusScore(float value) {
        bonusScore += value;
    }

    /**
     * Increases maximum depth reached.
     */
    private void increaseMaximumDepth() {
        maximumDrillDepth = Math.max(maximumDrillDepth, startingDepth - getY());
        scoreMultiplier = Math.max(1, getDrillDepthMultiplier() * PLAYER_DRILL_DEPTH_MULTIPLIER);
    }

    /**
     * Gets total score rounded to closest integer.
     *
     * @return total score as integer
     */
    public int getTotalScore() {
        return Math.round(baseScore * scoreMultiplier + bonusScore);
    }

    private float getDrillDepthMultiplier() {
        return Math.min(maximumDrillDepth / startingDepth, 1);
    }

    /**
     * Gets maximum depth reached.
     *
     * @return maximum reached depth as integer
     */
    public float getDrillDepth() {
        return getDrillDepthMultiplier() * map.getVirtualDepth();
    }

    /**
     * Gets current fuel amount.
     *
     * @return current amount of fuel as integer
     */
    public float getFuel() {
        return Math.max(0, totalFuel);
    }

    /**
     * Resets calibration values.
     */
    public void resetCalibration() {
        controller.reset();
    }

    /**
     * Gets player movement speed.
     *
     * @return current movement speed
     */
    private float getMovementSpeed() {
        return PLAYER_MOVE_SPEED * (speedMultiplier - drillSpeedReduction);
    }

    /**
     * Sets player state.
     *
     * @param newState      new player states
     * @param ignoreStates  ignore states
     */
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

    /**
     * Gets recent score value from Player.
     *
     * @return formatted score string
     */
    public String getRecentScoreString() {
        if (recentlyAddedScore > 0) {
            return String.format("+%d", recentlyAddedScore);
        }

        return null;
    }

    /**
     * Gets radar view power-up timer.
     *
     * @return timer as string
     */
    public String getRadarViewTimer() {
        return viewTimer > 0 ? String.format("%.1f", viewTimer) : null;
    }

    /**
     * Gets speed power-up timer.
     *
     * @return timer as string
     */
    public String getSpeedTimer() {
        return speedTimer > 0 ? String.format("%.1f", speedTimer) : null;
    }

    /**
     * Gets drill power-up timer.
     *
     * @return timer as string
     */
    public String getDrillTimer() {
        return drillTimer > 0 ? String.format("%.1f", drillTimer) : null;
    }

    /**
     * Gets points power-up timer.
     *
     * @return timer as string
     */
    public String getPointsTimer() {
        return collectibleTimer > 0 ? String.format("%.1f", collectibleTimer) : null;
    }

    /**
     * Gets fuel power-up timer.
     *
     * @return timer as string
     */
    public String getFuelTimer() {
        return fuelTimer > 0 ? String.format("%.1f", fuelTimer) : null;
    }

    @Override
    public void draw(SpriteBatch batch, float delta) {
        // Update movement based on controller input
        controller.update();

        if (!controller.isCalibrating()) {
            // Set state to idle by default
            setCurrentState(STATES.IDLE, STATES.DONE);

            recentlyAddedScore = 0;

            if (consumeFuel(delta)) {
                float valueX = 0;
                float valueY = 0;

                checkMovementConditions();

                if (isAllowedToMoveUp && controller.isMovingUp()) {
                    valueY = 1;
                }
                if (isAllowedToMoveDown && controller.isMovingDown()) {
                    valueY = -1;
                }

                if (isAllowedToMoveRight && controller.isMovingRight()) {
                    valueX = 1;
                }
                if (isAllowedToMoveLeft && controller.isMovingLeft()) {
                    valueX = -1;
                }

                if (valueX != 0 || valueY != 0) {
                    setCurrentState(STATES.ACTIVE, STATES.DONE);
                    keyFrameState += delta;

                    soundEffects.playLongSound("engine");
                    rotateToPoint(valueX, valueY, delta);
                    increaseMaximumDepth();
                    updateTileStatus();
                } else {
                    soundEffects.pauseLongSound("engine");
                }
            }

            drawEffect(batch, delta);
            updateTimerStatus(delta);
        }

        draw(batch);
    }

    /**
     * Draws particle effect.
     *
     * @param batch batch to draw to
     * @param delta delta time
     */
    private void drawEffect(SpriteBatch batch, float delta) {
        ParticleEmitter smoke = effect.findEmitter("engine");
        smoke.getAngle().setHighMax(getRotation() + 50);
        smoke.getAngle().setHighMin(getRotation() - 50);
        smoke.getAngle().setLowMax(getRotation() + 50);
        smoke.getAngle().setLowMin(getRotation() - 50);

        ParticleEmitter rocket = effect.findEmitter("rocket");
        rocket.getAngle().setHighMax(getRotation() + 45);
        rocket.getAngle().setHighMin(getRotation() - 45);
        rocket.getAngle().setLowMax(getRotation() + 45);
        rocket.getAngle().setLowMin(getRotation() - 45);

        if (speedTimer > 0) {
            rocket.getTint().setColors(boostedRocketColor);
        } else {
            rocket.getTint().setColors(defaultRocketColor);
        }

        effect.setPosition(
            getCenterX() + MathUtils.cosDeg(getRotation()),
            getCenterY() + MathUtils.sinDeg(getRotation())
        );
        effect.draw(batch, delta);
    }

    /**
     * Gets player center (X).
     *
     * @return current position of player (center).
     */
    public float getCenterX() {
        return getX() + BIG_TILE_SIZE / 2;
    }

    /**
     * Gets player center (Y).
     *
     * @return current position of player (center).
     */
    public float getCenterY() {
        return getY() + BIG_TILE_SIZE / 2;
    }

    /**
     * Gets shortest rotation angle.
     *
     * @param currentAngle  current angle
     * @param newAngle      new angle
     * @return angle value
     */
    private int getShortestRotation(float currentAngle, float newAngle) {
        float rotateLeft = (360 - currentAngle) + newAngle;
        float rotateRight = currentAngle - newAngle;

        if(currentAngle < newAngle)  {
            if(newAngle > 0) {
                rotateLeft = newAngle - currentAngle;
                rotateRight = (360 - newAngle) + currentAngle;
            } else {
                rotateLeft = (360 - newAngle) + currentAngle;
                rotateRight = newAngle - currentAngle;
            }
        }

        return Math.round(rotateLeft <= rotateRight ? rotateLeft : -rotateRight) % 360;
    }

    /**
     * Rotates player to face given point.
     *
     * @param pointX    x point
     * @param pointY    y point
     * @param delta     delta time
     */
    private void rotateToPoint(float pointX, float pointY, float delta) {
        float angle = MathUtils.atan2(
                (getCenterY() - pointY) - getCenterY(),
                (getCenterX() - pointX) - getCenterX()
        ) * MathUtils.radiansToDegrees;

        if (angle < 0) {
            angle += 360;
        }

        float rotation = getRotation();

        if (rotation < 0) {
            rotation += 360;
        }

        int difference = getShortestRotation(rotation, angle);

        if (MathUtils.isEqual(Math.abs(difference), 180, 5)) {
            setRotation(angle);
        } else {
            float speed = -getMovementSpeed() * delta;
            rotation += difference * PLAYER_ROTATION_MULTIPLIER * delta;
            setRotation(rotation % 360);

            translate(
                speed * MathUtils.cosDeg(getRotation()),
                speed * MathUtils.sinDeg(getRotation())
            );
        }
    }

    @Override
    public void draw(Batch batch) {
        if (isVisible()) {
            TextureRegion frame = animation.getKeyFrame(keyFrameState, true);

            batch.draw(
                playerTracks, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() - 90
            );

            batch.draw(
                playerUnit, getX(), getY(),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() - 90
            );

            if (drillTimer > 0) {
                batch.setColor(Color.SKY);
            }
            batch.draw(
                frame,
                getX() + MathUtils.cosDeg(getRotation()),
                getY() - BIG_TILE_SIZE + MathUtils.sinDeg(getRotation()),
                frame.getRegionWidth() / 2,
                frame.getRegionHeight() / 2 + BIG_TILE_SIZE,
                frame.getRegionWidth(),
                frame.getRegionHeight(),
                getScaleX(), getScaleY(),
                getRotation() - 90
            );
            batch.setColor(Color.WHITE);
        }
    }

    /**
     * Clears shroud tile.
     *
     * @param x     tile x
     * @param y     tile y
     */
    private void clearShroudTile(float x, float y) {
        TiledMapTileLayer.Cell cell = map.getCellFromPosition(x, y, "shroud");

        if (cell != null && cell.getTile() != null) {
            final int TRANSPARENT_TILES = 4;

            int tileSize = map.getTileSetSize("shroud") + TRANSPARENT_TILES;
            double distance = Math.hypot(playerView.x - x, playerView.y - y);
            double newIndex = Math.ceil((playerView.radius - distance) / (playerView.radius / tileSize));
            TiledMapTile tile = map.getTileByIndex("shroud", (int) newIndex);

            if (tile == null || cell.getTile().getId() < tile.getId()) {
                if (newIndex >= tileSize - TRANSPARENT_TILES) {
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
        playerView.setPosition(getCenterX(), getCenterY());

        for (float y = playerView.y - playerView.radius; y < playerView.y + playerView.radius; y += SMALL_TILE_SIZE) {
            for (float x = playerView.x - playerView.radius; x < playerView.x + playerView.radius; x += SMALL_TILE_SIZE) {
                if (playerView.contains(x, y)) {
                    clearShroudTile(x, y);
                }
            }
        }
    }

    /**
     * Processes all collectibles.
     *
     * @param tile  collectible tile
     * @param key   collectible identifier
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

                if (baseValue > 0) {
                    recentlyAddedScore = Math.round(baseValue * collectibleMultiplier);
                }

                Float bonusValue = map.getFloat(tile, "bonus", 0f);

                addBonusScore(bonusValue);
                break;
        }

        soundEffects.playSound("collect");
    }

    /**
     * Updates collectible state.
     *
     * @param x     x index
     * @param y     y index
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

    /**
     * Updates ground status.
     *
     * @param x     ground tile x
     * @param y     ground tile y
     */
    private void updateGroundStatus(float x, float y) {
        TiledMapTileLayer.Cell groundCell = map.getCellFromPosition(x, y, "ground");

        if (groundCell != null && groundCell.getTile() != null) {
            addBonusScore(SCORE_GROUND_TILE_OPENED);
            groundCell.setTile(null);
            collisionInterval = .15f;
        }
    }

    /**
     * Updates map view.
     */
    private void updateTileStatus() {
        updatePlayerView();

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
                map.setShroudLayerOpacity(MathUtils.lerp(0, 1, shroudOpacity));
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

    /**
     * Checks movement conditions.
     */
    private void checkMovementConditions() {
        final float GROUND_LEVEL = map.getMapHeight() - BIG_TILE_SIZE * 4;

        isAllowedToMoveLeft = true;
        isAllowedToMoveRight = true;
        isAllowedToMoveDown = true;
        isAllowedToMoveUp = true;

        // Ground
        if (getY() + BIG_TILE_SIZE >= GROUND_LEVEL) {
            isAllowedToMoveUp = false;
            isAllowedToMoveLeft = false;
            isAllowedToMoveRight = false;
        }

        // Left side
        if (getX() + BIG_TILE_SIZE < 0) {
            setPosition(map.getMapWidth(), getY());
        }

        // Right side
        if (getX() > map.getMapWidth()) {
            setPosition(-BIG_TILE_SIZE, getY());
        }

        // Goal
        if (getY() + BIG_TILE_SIZE <= 0) {
            setCurrentState(STATES.DONE);
        }
    }

    /**
     * Checks if player has reached the depth goal.
     *
     * @return true if goal is reached, otherwise false
     */
    public boolean isDepthGoalAchieved() {
        return currentState == STATES.DONE;
    }
}