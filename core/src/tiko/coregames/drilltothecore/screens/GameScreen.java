package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;

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

    /**
     * Moves camera with player.
     */
    private void followPlayerObject() {
        OrthographicCamera camera = (OrthographicCamera) stage.getCamera();

        float viewportWidth = camera.viewportWidth * camera.zoom / 2;
        float viewportHeight = camera.viewportHeight * camera.zoom / 2;

        camera.position.x = MathUtils.clamp(player.getX(), viewportWidth, TOTAL_TILES_WIDTH - viewportWidth);
        camera.position.y = MathUtils.clamp(player.getY(), viewportHeight, TOTAL_TILES_HEIGHT - viewportHeight);

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        SpriteBatch batch = CoreSetup.getBatch();
        OrthographicCamera worldCamera = (OrthographicCamera) stage.getCamera();
        LevelManager.applyCameraAndRender(worldCamera);

        batch.begin();
        // Apply world camera
        batch.setProjectionMatrix(worldCamera.combined);
        player.draw(batch, delta);

        // Apply HUD camera
        batch.setProjectionMatrix(hudCamera.combined);
        batch.end();

        // Temporary zoom
        worldCamera.zoom = 0.7f;
        followPlayerObject();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            CoreSetup.nextScreen(new MainMenuScreen());
        }
    }

    @Override
    public void dispose() {
        LevelManager.dispose();
        player.dispose();
        super.dispose();
    }
}