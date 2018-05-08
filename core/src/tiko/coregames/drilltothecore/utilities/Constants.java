package tiko.coregames.drilltothecore.utilities;

/**
 * Constants class will define all necessary values.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class Constants {
    /**
     * Defines virtual world width.
     */
    public static final float WORLD_WIDTH = 576;

    /**
     * Defines virtual world height.
     */
    public static final float WORLD_HEIGHT = 360;

    /**
     * Defines biggest tile size.
     */
    public static final int BIG_TILE_SIZE = 32;

    /**
     * Defines smallest tile size.
     */
    public static final int SMALL_TILE_SIZE = 8;

    /**
     * Defines controller calibration time.
     */
    public static final float CONTROLLER_CALIBRATION_TIME = 1.5f;

    /**
     * Defines maximum amount of profiles.
     */
    public static final int MAX_SAVED_PROFILES = 25;

    /**
     * Defines menu padding size.
     */
    public static final float MENU_DEFAULT_PADDING = 5;

    /**
     * Defines screen safe zone size.
     */
    public static final float SAFE_ZONE_SIZE = 5;

    /**
     * Defines default game speed.
     */
    public static final float GAME_SPEED_MODIFIER = 1;

    /**
     * Defines debug mode status.
     */
    public static final boolean DEBUG_MODE = true;

    /**
     * Defines level count.
     */
    public static final int LEVEL_COUNT = 5;

    /**
     * Defines player move speed.
     */
    public static final float PLAYER_MOVE_SPEED = 85;

    /**
     * Defines player rotation speed multiplier.
     */
    public static final float PLAYER_ROTATION_MULTIPLIER = 2.5f;

    /**
     * Defines player fuel tank size.
     */
    public static final float PLAYER_FUEL_TANK_SIZE = 150;

    /**
     * Defines minimum player fuel consumption rate.
     */
    public static final float PLAYER_FUEL_MIN_CONSUMPTION = 1.15f;

    /**
     * Defines player idle multiplier.
     */
    public static final float PLAYER_FUEL_IDLE_MULTIPLIER = .33f;

    /**
     * Defines player idle state delay.
     */
    public static final float PLAYER_IDLE_STATE_DELAY = 5;

    /**
     * Defines player drill depth multiplier.
     */
    public static final float PLAYER_DRILL_DEPTH_MULTIPLIER = 5;

    /**
     * Defines player movement speed multiplier.
     */
    public static final float PLAYER_MOVEMENT_SPEED_MULTIPLIER = 1.33f;

    /**
     * Defines player drill speed reduction.
     */
    public static final float PLAYER_DRILL_SPEED_REDUCTION = .2f;

    /**
     * Defines player view zone radius.
     */
    public static final float PLAYER_VIEW_RADIUS = 112;

    /**
     * Defines score for revealing a tile.
     */
    public static final float SCORE_TILE_REVEALED = .005f;

    /**
     * Defines score for picking a power-up.
     */
    public static final float SCORE_POWER_UP_PICKUP = 1.25f;

    /**
     * Defines score for opening a ground tile.
     */
    public static final float SCORE_GROUND_TILE_OPENED = 0.125f;

    /**
     * Defines compensation bonus for power up.
     */
    public static final float SCORE_POWER_UP_COMPENSATION = 1000;

    /**
     * Defines bonus score for completing the level.
     */
    public static final float SCORE_COMPLETION_BONUS = 5000;

    /**
     * Defines splash screen duration.
     */
    public static final float SINGLE_SPLASH_DURATION = 3;

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_POINT_MULTIPLIER = "pointBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_SPEED_MULTIPLIER = "speedBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_DRILL_MULTIPLIER = "drillBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_RADAR_EXTENDER = "radarBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_UNLIMITED_FUEL = "fuelBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_RANDOMIZED = "randomBoost";

    /**
     * Defines power-up identifier.
     */
    public static final String POWER_UP_NOTHING = "";

    /**
     * Defines fuel canister identifier.
     */
    public static final String FUEL_CANISTER_REFILL_100 = "bigFuel";

    /**
     * Defines fuel canister identifier.
     */
    public static final String FUEL_CANISTER_REFILL_50 = "mediumFuel";

    /**
     * Defines fuel canister identifier.
     */
    public static final String FUEL_CANISTER_REFILL_20 = "smallFuel";

    /**
     * Defines random power-up contents.
     */
    public static final String[] RANDOM_POWER_UPS = {
        POWER_UP_NOTHING, POWER_UP_POINT_MULTIPLIER, POWER_UP_RADAR_EXTENDER,
        POWER_UP_DRILL_MULTIPLIER, POWER_UP_SPEED_MULTIPLIER, POWER_UP_UNLIMITED_FUEL
    };
}