package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TideMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable;

public class LevelManager implements Disposable {
    private TiledMap tileMap;
    private TiledMapRenderer levelRenderer;

    public LevelManager(int level) {
        initializeLevel(level);

        levelRenderer = new OrthogonalTiledMapRenderer(tileMap);
    }

    private void initializeLevel(int level) {
        TideMapLoader loader = new TideMapLoader();
        StringBuilder path = new StringBuilder("leveldata/");

        switch (level) {
            default:
                path.append("tutorial.tmx");
                break;
        }

        tileMap = loader.load(path.toString());
    }

    public void renderLevel(OrthographicCamera camera, float delta) {
        levelRenderer.setView(camera);
        levelRenderer.render();
    }

    @Override
    public void dispose() {
        tileMap.dispose();
    }
}