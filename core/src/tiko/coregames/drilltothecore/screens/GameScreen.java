package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen {
    private OrthographicCamera hudCamera;

    private LevelManager map;

    private ProgressBar playerFuel;
    private Player player;

    public GameScreen() {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT));
        hudCamera = new OrthographicCamera();

        map = new LevelManager(0);
        Vector3 playerSpawn = map.getSpawnPoint("player");

        if (playerSpawn != null) {
            player = new Player(map);
            player.setPosition(playerSpawn.x, playerSpawn.y);
        }

        playerFuel = new ProgressBar(0, PLAYER_FUEL_TANK_SIZE, 0.1f, false, skin);
        playerFuel.setPosition(Gdx.graphics.getWidth() - playerFuel.getPrefWidth() - SAFEZONE_SIZE, Gdx.graphics.getHeight() - playerFuel.getPrefHeight() - SAFEZONE_SIZE);
        playerFuel.setValue(player.getFuel());
        playerFuel.setDisabled(true);

        Gdx.input.setCatchBackKey(true);
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

    private void updateFuelMeter() {
        playerFuel.setValue(player.getFuel());
    }

    @Override
    public void resize(int width, int height) {
        playerFuel.setPosition(width - playerFuel.getPrefWidth() - SAFEZONE_SIZE, height - playerFuel.getPrefHeight() - SAFEZONE_SIZE);
        hudCamera.setToOrtho(false, width, height);
        super.resize(width, height);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        SpriteBatch batch = CoreSetup.getBatch();
        OrthographicCamera worldCamera = (OrthographicCamera) stage.getCamera();
        map.applyCameraAndRender(worldCamera);

        batch.begin();
        // Apply world camera
        batch.setProjectionMatrix(worldCamera.combined);
        player.draw(batch, delta);

        // Apply HUD camera
        batch.setProjectionMatrix(hudCamera.combined);
        playerFuel.draw(batch, 1);
        batch.end();

        // Temporary zoom
        worldCamera.zoom = 0.7f;
        followPlayerObject();
        updateFuelMeter();

        // DEBUG - Go back to menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isCatchBackKey()) {
            CoreSetup.nextScreen(new MainMenuScreen());
        }
    }

    @Override
    public void dispose() {
        map.dispose();
        player.dispose();
        super.dispose();
    }
}