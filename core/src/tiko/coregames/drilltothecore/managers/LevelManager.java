package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

/**
 * Handles everything related to levels.
 */
public abstract class LevelManager {
    /**
     * Stores level data.
     */
    private static TiledMap levelData;

    /**
     * Renders level data.
     */
    private static TiledMapRenderer levelRenderer;

    /**
     * Destroys level data.
     */
    private static void destroyLevel() {
        if (levelData != null) {
            levelData.dispose();
        }
    }

    /**
     * Loads specific level and sets it up
     *
     * @param value         Level identifier.
     */
    public static void setupLevel(int value) {
        TmxMapLoader loader = new TmxMapLoader();
        StringBuilder path = new StringBuilder("leveldata/");

        // Choose level or tutorial if invalid value is given
        switch (value) {
            default:
                path.append("tutorial.tmx");
                break;
        }

        // Destroy existing level if there is any
        destroyLevel();
        // Load new level
        levelData = loader.load(path.toString());
        // Setup level renderer
        levelRenderer = new OrthogonalTiledMapRenderer(levelData);
    }

    public static void applyCameraAndRender(OrthographicCamera camera) {
        if (camera != null) {
            levelRenderer.setView(camera);
        }

        levelRenderer.render();
    }

    // TILE HANDLING /////////////////////////

    /**
     * Gets spawn point from "spawn" layer.
     *
     * @param name          Layer name.
     * @return              Return point as Rectangle.
     */
    public static Vector3 getSpawnPoint(String name) {
        try {
            // Get "spawn" layer
            MapLayer layer = levelData.getLayers().get("spawns");

            if (layer != null) {
                // Get object from layer and cast it as rectangle shaped object
                Rectangle point = ((RectangleMapObject) layer.getObjects().get(name)).getRectangle();

                return new Vector3(point.x, point.y, 0);
            }
        } catch (Exception e) {
            Gdx.app.log(LevelManager.class.getSimpleName(), "Could not load specified spawn point.");
        }

        // Invalid value - return null
        return null;
    }

    /**
     * Gets "Tile" from given coordinates.
     *
     * @param x             X index.
     * @param y             Y index.
     * @param name          Layer name.
     * @return              Returns Tile instance.
     */
    public static TiledMapTile getTileFromPosition(float x, float y, String name) {
        try {
            return getCellFromPosition(x, y, name).getTile();
        } catch (Exception e) {
            Gdx.app.log(LevelManager.class.getSimpleName(), "Could not find tile.");
        }

        // Invalid value - return null
        return null;
    }

    public static TiledMapTileLayer.Cell getCellFromPosition(float x, float y, String name) {
        try {
            TiledMapTileLayer layer = (TiledMapTileLayer) levelData.getLayers().get(name);

            if (layer != null) {
                int tileX = (int) (x / layer.getTileWidth());
                int tileY = (int) (y / layer.getTileHeight());

                return layer.getCell(tileX, tileY);
            }
        } catch (Exception e) {
            Gdx.app.log(LevelManager.class.getSimpleName(), "Could not find cell.");
        }

        // Invalid value - return null
        return null;
    }

    /**
     * Gets property from given tile.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @param type          Property type.
     * @param <T>           Return type.
     * @return              Return property value or null.
     */
    private static <T> T getProperty(TiledMapTile tile, String property, Class<T> type) {
        if (tile != null && property != null) {
            MapProperties properties = tile.getProperties();

            if (properties != null && properties.containsKey(property)) {
                return properties.get(property, type);
            }
        }

        return null;
    }

    /**
     * Gets "String" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as String.
     */
    public static String getString(TiledMapTile tile, String property) {
        return getProperty(tile, property, String.class);
    }

    /**
     * Gets "Boolean" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Boolean.
     */
    public static Boolean getBoolean(TiledMapTile tile, String property) {
        return getProperty(tile, property, Boolean.class);
    }

    /**
     * Gets "Integer" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Integer.
     */
    public static Integer getInteger(TiledMapTile tile, String property) {
        return getProperty(tile, property, Integer.class);
    }

    /**
     * Gets "Float" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Float.
     */
    public static Float getFloat(TiledMapTile tile, String property) {
        return getProperty(tile, property, Float.class);
    }

    /**
     * Disposes all level data.
     */
    public static void dispose() {
        destroyLevel();
    }
}