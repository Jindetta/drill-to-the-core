package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Setup;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen {
    private OrthographicCamera hudCamera;
    private Player player;

    public GameScreen() {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));
        hudCamera = new OrthographicCamera();

        LevelManager.setupLevel(0);
        player = new Player();
    }

    private void followPlayerObject() {
        Camera camera = stage.getCamera();

        if (camera != null) {
            camera.position.x = MathUtils.clamp(player.getX(), TOTAL_TILES_WIDTH / 2, TOTAL_TILES_WIDTH - TILE_WIDTH);
            camera.position.y = MathUtils.clamp(player.getY(), TOTAL_TILES_HEIGHT / 2, TOTAL_TILES_HEIGHT - TILE_HEIGHT);

            camera.update();
        }
    }

    @Override
    public void resize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        SpriteBatch batch = Setup.getBatch();
        OrthographicCamera worldCamera = (OrthographicCamera) stage.getCamera();
        LevelManager.applyCameraAndRender(worldCamera);

        batch.begin();
        // Apply world camera
        batch.setProjectionMatrix(worldCamera.combined);
        player.draw(batch, delta);

        // Apply HUD camera
        batch.setProjectionMatrix(hudCamera.combined);
        batch.end();

        worldCamera.zoom = 0.7f;
        followPlayerObject();
    }

    @Override
    public void dispose() {
        LevelManager.dispose();
        player.dispose();
        super.dispose();
    }
}