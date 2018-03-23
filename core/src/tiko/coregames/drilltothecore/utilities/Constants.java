package tiko.coregames.drilltothecore.utilities;

public class Constants {
    public static final float WORLD_WIDTH = 576;
    public static final float WORLD_HEIGHT = 360;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int HORIZONTAL_TILES = 32;
    public static final int VERTICAL_TILES = 64;

    public static final int TOTAL_TILES_WIDTH = TILE_WIDTH * HORIZONTAL_TILES;
    public static final int TOTAL_TILES_HEIGHT = TILE_HEIGHT * VERTICAL_TILES;

    public static final float MAX_MOVEMENT_VALUE = 10;
    public static final float CONTROLLER_CALIBRATION_TIME = 1.5f;
    public static final int MAX_SAVED_PROFILES = 25;

    public static final float MENU_PADDING_TOP = 15;

    public static final float SAFEZONE_SIZE = 5;
    public static final boolean DEBUG_MODE = true;

    public static final float PLAYER_MOVE_SPEED = 120;
    public static final float PLAYER_FUEL_TANK_SIZE = 300;
    public static final float PLAYER_FUEL_MIN_CONSUMPTION = 1f;
    public static final float PLAYER_VIEW_RADIUS = 75;

    public static final int PLAYER_ORIENTATION_UP = 0;
    public static final int PLAYER_ORIENTATION_UP_RIGHT = 45;
    public static final int PLAYER_ORIENTATION_RIGHT = 90;
    public static final int PLAYER_ORIENTATION_DOWN_RIGHT = 135;
    public static final int PLAYER_ORIENTATION_DOWN = 180;
    public static final int PLAYER_ORIENTATION_DOWN_LEFT = 225;
    public static final int PLAYER_ORIENTATION_LEFT = 270;
    public static final int PLAYER_ORIENTATION_UP_LEFT = 315;

    public static final float SINGLE_SPLASH_DURATION = 4;
}