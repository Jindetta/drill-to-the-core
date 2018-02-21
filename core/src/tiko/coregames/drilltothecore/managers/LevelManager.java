package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class LevelManager implements Disposable {
    private TiledMap tileMap;
    private TiledMapRenderer levelRenderer;

    public LevelManager(int level) {
        // Debug test
        tileMap = new TmxMapLoader().load("leveldata/tutorial.tmx");
        levelRenderer = new OrthogonalTiledMapRenderer(tileMap);
    }

    public Rectangle getPlayerSpawnRectangle() {
        try {
            RectangleMapObject object = (RectangleMapObject) tileMap.getLayers().get("spawns").getObjects().get("player");

            return object.getRectangle();
        } catch (Exception e) {
            return null;
        }
    }

    public void renderLevel(OrthographicCamera camera) {
        levelRenderer.setView(camera);
        levelRenderer.render();
    }

    @Override
    public void dispose() {
        tileMap.dispose();
    }
}