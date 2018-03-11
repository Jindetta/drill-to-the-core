package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import tiko.coregames.drilltothecore.CoreSetup;

public class Utilities {
    public static final float WORLD_WIDTH = 768f;
    public static final float WORLD_HEIGHT = 512f;
    public static final float WORLD_SCALE = 1f;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int HORIZONTAL_TILES = 24;
    public static final int VERTICAL_TILES = 58;

    public static final int TOTAL_TILES_WIDTH = TILE_WIDTH * HORIZONTAL_TILES;
    public static final int TOTAL_TILES_HEIGHT = TILE_HEIGHT * VERTICAL_TILES;

    public static final float SAFEZONE_SIZE = 5;
    public static final boolean DEBUG_MODE = true;

    public static final float DEFAULT_MIN_THRESHOLD = 2;
    public static final float DEFAULT_MAX_THRESHOLD = 7.5f;

    public static final float PLAYER_MOVE_SPEED = 120;
    public static final float PLAYER_FUEL_TANK_SIZE = 100;
    public static final float PLAYER_FUEL_MIN_CONSUMPTION = 0.6f;

    public static float toWorldUnits(float value) {
        return value / WORLD_SCALE;
    }

    public static int toPixelUnits(float value) {
        return Math.round(value * WORLD_SCALE);
    }
}