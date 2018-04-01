package tiko.coregames.drilltothecore.utilities;

public class Constants {
    public static final float WORLD_WIDTH = 576;
    public static final float WORLD_HEIGHT = 360;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final float MAX_SENSOR_VALUE = 10;
    public static final float CONTROLLER_CALIBRATION_TIME = 1.5f;
    public static final int MAX_SAVED_PROFILES = 25;

    public static final float MENU_PADDING_TOP = 15;

    public static final float SAFE_ZONE_SIZE = 5;
    public static final float GAME_SPEED_MODIFIER = 1;
    public static final boolean DEBUG_MODE = true;

    public static final float PLAYER_MOVE_SPEED = 85;
    public static final float PLAYER_ROTATION_SPEED = 130;
    public static final float PLAYER_FUEL_TANK_SIZE = 90;
    public static final float PLAYER_FUEL_MIN_CONSUMPTION = 1f;
    public static final float PLAYER_FUEL_IDLE_MULTIPLIER = .25f;
    public static final float PLAYER_IDLE_STATE_DELAY = 5;
    public static final float PLAYER_MOVEMENT_SPEED_MULTIPLIER = 1.33f;
    public static final float PLAYER_DRILL_SPEED_REDUCTION = .2f;
    public static final float PLAYER_VIEW_RADIUS = 75;

    public static final int PLAYER_ORIENTATION_UP = 0;
    public static final int PLAYER_ORIENTATION_UP_RIGHT = 45;
    public static final int PLAYER_ORIENTATION_RIGHT = 90;
    public static final int PLAYER_ORIENTATION_DOWN_RIGHT = 135;
    public static final int PLAYER_ORIENTATION_DOWN = 180;
    public static final int PLAYER_ORIENTATION_DOWN_LEFT = 225;
    public static final int PLAYER_ORIENTATION_LEFT = 270;
    public static final int PLAYER_ORIENTATION_UP_LEFT = 315;

    public static final float SINGLE_SPLASH_DURATION = 3;

    public static final String POWER_UP_POINT_MULTIPLIER = "pointMultiplier";
    public static final String POWER_UP_SPEED_MULTIPLIER = "speedMultiplier";
    public static final String POWER_UP_DRILL_MULTIPLIER = "drillMultiplier";
    public static final String POWER_UP_RADAR_EXTENDER = "radarPowerUp";
    public static final String POWER_UP_RANDOMIZED = "randomPowerUp";
    public static final String POWER_UP_NOTHING = "";

    public static final String FUEL_CANISTER_REFILL_100 = "bigFuel";
    public static final String FUEL_CANISTER_REFILL_50 = "mediumFuel";
    public static final String FUEL_CANISTER_REFILL_20 = "smallFuel";

    public static final String[] RANDOM_POWER_UPS = {
        POWER_UP_NOTHING, POWER_UP_POINT_MULTIPLIER, POWER_UP_RADAR_EXTENDER,
        POWER_UP_DRILL_MULTIPLIER, POWER_UP_SPEED_MULTIPLIER
    };

    public enum PLAYER_STATES {
        IDLE, ACTIVE, TURNING, JAMMED, DESTROYED
    }
}