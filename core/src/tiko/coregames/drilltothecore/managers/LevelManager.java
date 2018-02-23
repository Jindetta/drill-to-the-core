package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Handles everything related to levels.
 */
public abstract class LevelManager {
    private static TiledMap levelData;
    private static TiledMapRenderer levelRenderer;

    private static final String DEBUG_TAG = "levelManager";

    /**
     * Destroys level data if there is any
     */
    private static void destroyLevel() {
        if (levelData != null) {
            levelData.dispose();
        }
    }

    /**
     * Loads specific level and sets it up
     *
     * @param value of level to load
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
     * @param name of object
     * @return object rectangle
     */
    public static Rectangle getSpawnPoint(String name) {
        try {
            // Get "spawn" layer
            MapLayer layer = levelData.getLayers().get("spawns");

            if (layer != null) {
                // Get object from layer and cast it as rectangle shaped object
                RectangleMapObject object = (RectangleMapObject) layer.getObjects().get(name);

                // Return point
                return object.getRectangle();
            }
        } catch (Exception e) {
            Gdx.app.log(DEBUG_TAG, "Could not load specified spawn point.");
        }

        // Invalid value - return null
        return null;
    }

    private <T> T getProperty(TiledMapTileLayer tile, String property, Class<T> type) {
        if (tile != null && property != null) {
            MapProperties properties = tile.getProperties();

            if (properties != null && properties.containsKey(property)) {
                return properties.get(property, type);
            }
        }

        return null;
    }

    public String getString(TiledMapTileLayer tile, String property) {
        return getProperty(tile, property, String.class);
    }

    public Boolean getBoolean(TiledMapTileLayer tile, String property) {
        return getProperty(tile, property, Boolean.class);
    }

    public Integer getInteger(TiledMapTileLayer tile, String property) {
        return getProperty(tile, property, Integer.class);
    }

    public Float getFloat(TiledMapTileLayer tile, String property) {
        return getProperty(tile, property, Float.class);
    }

    public static void dispose() {
        levelData.dispose();
    }
}