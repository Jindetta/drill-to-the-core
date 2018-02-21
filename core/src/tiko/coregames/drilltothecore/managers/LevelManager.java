package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

public class LevelManager implements Disposable {
    private TiledMap levelData;
    private TiledMapRenderer levelRenderer;

    public LevelManager(int levelValue) {
        levelData = setupLevel(levelValue);
        levelRenderer = new OrthogonalTiledMapRenderer(levelData);
    }

    private TiledMap setupLevel(int value) {
        TmxMapLoader loader = new TmxMapLoader();
        StringBuilder path = new StringBuilder("leveldata/");

        switch (value) {
            default:
                path.append("tutorial.tmx");
                break;
        }

        return loader.load(path.toString());
    }

    // TILE HANDLING /////////////////////////

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

    @Override
    public void dispose() {
        levelData.dispose();
    }
}