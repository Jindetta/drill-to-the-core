package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen {
    private LevelManager map;

    private Window pauseWindow;
    private ProgressBar playerFuel;
    private Player player;

    public GameScreen() {
        map = new LevelManager(-1);
        Vector3 playerSpawn = map.getSpawnPoint("player");

        if (playerSpawn != null) {
            player = new Player(map);
            player.setPosition(playerSpawn.x, playerSpawn.y);

            createFuelMeter();
        }

        createPauseWindow();
    }

    private void createPauseWindow() {
        pauseWindow = new Window("Paused", skin);
        pauseWindow.setResizable(false);
        pauseWindow.setMovable(false);
        pauseWindow.setVisible(false);

        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseWindow.setVisible(false);
            }
        });
        pauseWindow.add(continueButton);

        addActor(pauseWindow);
    }

    private void createFuelMeter() {
        playerFuel = new ProgressBar(0, PLAYER_FUEL_TANK_SIZE, 0.1f, false, skin);
        playerFuel.setPosition(getWidth() - playerFuel.getWidth() - SAFEZONE_SIZE, getHeight() - playerFuel.getHeight() - SAFEZONE_SIZE);
        playerFuel.setValue(player.getFuel());
        playerFuel.setDisabled(true);

        addActor(playerFuel);
    }

    /**
     * Moves camera with player.
     */
    private void followPlayerObject() {
        OrthographicCamera camera = (OrthographicCamera) getCamera();

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
    public void show() {
        super.show();
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        Gdx.input.setCatchBackKey(false);
        super.hide();
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float fuelX = viewport.getWorldWidth() - playerFuel.getWidth() - SAFEZONE_SIZE;
        float fuelY = viewport.getWorldHeight() - playerFuel.getHeight() - SAFEZONE_SIZE;
        float centerX = (viewport.getWorldWidth() - pauseWindow.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - pauseWindow.getHeight()) / 2;

        playerFuel.setPosition(fuelX, fuelY);
        pauseWindow.setPosition(centerX, centerY);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (pauseWindow.isVisible()) {
            delta = 0;
        }

        SpriteBatch batch = CoreSetup.getBatch();
        OrthographicCamera worldCamera = (OrthographicCamera) getCamera();
        map.renderView(worldCamera);

        batch.begin();
        batch.setProjectionMatrix(worldCamera.combined);
        player.draw(batch, delta);
        batch.end();

        followPlayerObject();
        updateFuelMeter();

        act(delta);
        draw();
    }

    @Override
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            CoreSetup.nextScreen(new MainMenuScreen());
        }

        return super.keyDown(key);
    }

    @Override
    public void pause() {
        pauseWindow.setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();
        player.dispose();
        map.dispose();
    }
}