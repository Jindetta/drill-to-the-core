package tiko.coregames.drilltothecore.utilities;

public class Constants {
    public static final float WORLD_WIDTH = 576;
    public static final float WORLD_HEIGHT = 360;

    public static final int BIG_TILE_SIZE = 32;
    public static final int SMALL_TILE_SIZE = 8;

    public static final float MAX_SENSOR_VALUE = 10;
    public static final float SENSITIVITY_MULTIPLIER = .8f;
    public static final float CONTROLLER_CALIBRATION_TIME = 1.5f;
    public static final int MAX_SAVED_PROFILES = 25;

    public static final float MENU_PADDING_TOP = 15;

    public static final float SAFE_ZONE_SIZE = 5;
    public static final float GAME_SPEED_MODIFIER = 1;
    public static final boolean DEBUG_MODE = true;

    public static final float PLAYER_MOVE_SPEED = 85;
    public static final float PLAYER_ROTATION_SPEED = 130;
    public static final float PLAYER_FUEL_TANK_SIZE = 150;
    public static final float PLAYER_FUEL_MIN_CONSUMPTION = 1f;
    public static final float PLAYER_FUEL_IDLE_MULTIPLIER = .33f;
    public static final float PLAYER_IDLE_STATE_DELAY = 5;
    public static final float PLAYER_DRILL_DEPTH_MULTIPLIER = 5;
    public static final float PLAYER_MOVEMENT_SPEED_MULTIPLIER = 1.33f;
    public static final float PLAYER_DRILL_SPEED_REDUCTION = .2f;
    public static final float PLAYER_VIEW_RADIUS = 112;

    public static final float SCORE_TILE_REVEALED = .005f;
    public static final float SCORE_POWER_UP_PICKUP = 1.25f;
    public static final float SCORE_GROUND_TILE_OPENED = .125f;

    public static final float SINGLE_SPLASH_DURATION = 3;

    public static final String POWER_UP_POINT_MULTIPLIER = "pointBoost";
    public static final String POWER_UP_SPEED_MULTIPLIER = "speedBoost";
    public static final String POWER_UP_DRILL_MULTIPLIER = "drillBoost";
    public static final String POWER_UP_RADAR_EXTENDER = "radarBoost";
    public static final String POWER_UP_UNLIMITED_FUEL = "fuelBoost";
    public static final String POWER_UP_ARMOR_REPAIR = "armorBoost";
    public static final String POWER_UP_RANDOMIZED = "randomBoost";
    public static final String POWER_UP_NOTHING = "";

    public static final String FUEL_CANISTER_REFILL_100 = "bigFuel";
    public static final String FUEL_CANISTER_REFILL_50 = "mediumFuel";
    public static final String FUEL_CANISTER_REFILL_20 = "smallFuel";

    public static final String[] RANDOM_POWER_UPS = {
        POWER_UP_NOTHING, POWER_UP_POINT_MULTIPLIER, POWER_UP_RADAR_EXTENDER,
        POWER_UP_DRILL_MULTIPLIER, POWER_UP_SPEED_MULTIPLIER, POWER_UP_UNLIMITED_FUEL
    };
}