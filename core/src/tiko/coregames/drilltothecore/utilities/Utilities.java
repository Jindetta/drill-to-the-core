package tiko.coregames.drilltothecore.utilities;

public class Utilities {
    public static final float WORLD_WIDTH = 8f;
    public static final float WORLD_HEIGHT = 7f;
    public static final float WORLD_SCALE = 100f;

    public static float toWorldUnits(float value) {
        return value / WORLD_SCALE;
    }

    public static int toPixelUnits(float value) {
        return Math.round(value * WORLD_SCALE);
    }
}