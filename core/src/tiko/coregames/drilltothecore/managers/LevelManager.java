package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import tiko.coregames.drilltothecore.Setup;

/**
 * Handles everything related to levels.
 */
public class LevelManager implements Disposable {
    /**
     * Stores level data.
     */
    private TiledMap levelData;

    /**
     * Stores map size
     */
    private int mapWidth, mapHeight, mapDepth;

    /**
     * Stores background color
     */
    private Texture backgroundImage;

    /**
     * Renders level data.
     */
    private OrthogonalTiledMapRenderer levelRenderer;

    public LevelManager(int levelValue) {
        TmxMapLoader loader = new TmxMapLoader();
        StringBuilder path = new StringBuilder("leveldata/");

        // Choose level or tutorial if invalid value is given
        switch (levelValue) {
            default:
                path.append("tutorial.tmx");
                break;
        }

        // Destroy existing level if there is any
        destroyLevel();
        // Load new level
        levelData = loader.load(path.toString());

        if (levelRenderer == null) {
            levelRenderer = new OrthogonalTiledMapRenderer(levelData, Setup.getBatch());
        } else {
            levelRenderer.setMap(levelData);
        }

        MapProperties properties = levelData.getProperties();

        int width = properties.get("width", Integer.class);
        int height = properties.get("height", Integer.class);
        int tileWidth = properties.get("tilewidth", Integer.class);
        int tileHeight = properties.get("tileheight", Integer.class);

        mapDepth = properties.get("depth", 1000, Integer.class);
        mapHeight = height * tileHeight;
        mapWidth = width * tileWidth;

        try {
            backgroundImage = new Texture(properties.get("background", String.class));
        } catch (Exception e) {
            backgroundImage = null;
        }
    }

    /**
     * Destroys level data.
     */
    private void destroyLevel() {
        if (levelData != null) {
            if (backgroundImage != null) {
                backgroundImage.dispose();
            }

            levelData.dispose();
        }
    }

    public TiledMapTile getTileByIndex(String tileSet, int index) {
        try {
            TiledMapTileSet tiles = levelData.getTileSets().getTileSet(tileSet);
            Integer offset = tiles.getProperties().get("firstgid", Integer.class);

            if (offset != null && index >= 0 && index < tiles.size()) {
                return tiles.getTile(offset + index);
            }
        } catch (Exception e) {

        }

        return null;
    }

    public int getTileSetSize(String tileSet) {
        if (tileSet != null) {
            TiledMapTileSet tiles = levelData.getTileSets().getTileSet(tileSet);

            if (tiles != null) {
                return tiles.size();
            }
        }

        return 0;
    }

    /**
     * Renders map and background.
     *
     * @param batch     Batch to draw to
     * @param camera    Camera to use
     */
    public void renderView(SpriteBatch batch, Camera camera) {
        if (backgroundImage != null) {
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            batch.draw(backgroundImage, 0, 0, mapWidth, mapHeight);
            batch.end();
        }

        levelRenderer.setView((OrthographicCamera) camera);
        levelRenderer.render();
    }

    // TILE HANDLING /////////////////////////

    /**
     * Gets spawn point from "spawn" layer.
     *
     * @param name          Layer name.
     * @return              Return point as Rectangle.
     */
    public Vector3 getSpawnPoint(String name) {
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

    public int getDepth() {
        return mapDepth;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    /**
     * Gets "Tile" from given coordinates.
     *
     * @param x             X index.
     * @param y             Y index.
     * @param name          Layer name.
     * @return              Returns Tile instance.
     */
    public TiledMapTile getTileFromPosition(float x, float y, String name) {
        try {
            return getCellFromPosition(x, y, name).getTile();
        } catch (Exception e) {
            Gdx.app.log(LevelManager.class.getSimpleName(), "Could not find tile.");
        }

        // Invalid value - return null
        return null;
    }

    public TiledMapTileLayer.Cell getCellFromPosition(float x, float y, String name) {
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
    private <T> T getProperty(TiledMapTile tile, String property, Class<T> type) {
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
    public String getString(TiledMapTile tile, String property, String defaultValue) {
        String value = getProperty(tile, property, String.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets "Boolean" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Boolean.
     */
    public Boolean getBoolean(TiledMapTile tile, String property, Boolean defaultValue) {
        Boolean value = getProperty(tile, property, Boolean.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets "Integer" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Integer.
     */
    public Integer getInteger(TiledMapTile tile, String property, Integer defaultValue) {
        Integer value = getProperty(tile, property, Integer.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Gets "Float" property.
     *
     * @param tile          Tile to read the property from.
     * @param property      Property name.
     * @return              Property as Float.
     */
    public Float getFloat(TiledMapTile tile, String property, Float defaultValue) {
        Float value = getProperty(tile, property, Float.class);
        return value != null ? value : defaultValue;
    }

    /**
     * Disposes all level data.
     */
    @Override
    public void dispose() {
        levelRenderer.dispose();
        destroyLevel();
    }
}