package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.LocalizationManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Debug;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class GameScreen extends BaseScreen {
    private LocalizationManager localizer;
    private LevelManager map;

    private Window pauseWindow;
    private ProgressBar playerFuel;
    private Label collectedItem;
    private Player player;

    private float totalGameTime;

    public GameScreen() {
        map = new LevelManager(0);
        Vector3 playerSpawn = map.getSpawnPoint("player");
        localizer = new LocalizationManager("game");

        if (playerSpawn != null) {
            player = new Player(map, localizer, playerSpawn.x, playerSpawn.y);
            createFuelMeter();
        }

        createPauseWindow();
        createNotificationLabel();
        totalGameTime = 0;
    }

    private void createNotificationLabel() {
        collectedItem = new Label("", skin);
        collectedItem.setAlignment(Align.center);
        collectedItem.setVisible(false);

        addActor(collectedItem);
    }

    private void setNotificationActive() {
        if (player.getRecentlyCollected() != null) {
            SequenceAction sequence = Actions.sequence();
            sequence.addAction(Actions.alpha(1));
            sequence.addAction(Actions.fadeOut(3));
            sequence.addAction(Actions.visible(false));

            collectedItem.setText(player.getRecentlyCollected());
            collectedItem.getActions().clear();
            collectedItem.addAction(sequence);
            collectedItem.setVisible(true);
        }
    }

    /**
     * Creates pause menu window.
     */
    private void createPauseWindow() {
        pauseWindow = new Window(localizer.getValue("pause"), skin);
        pauseWindow.setResizable(false);
        pauseWindow.setMovable(false);
        pauseWindow.setVisible(false);

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "settings":
                        Setup.nextScreen(new SettingsScreen());
                        return;
                    case "menu":
                        Setup.nextScreen(new MainMenuScreen());
                        break;
                    default:
                        if (DEBUG_MODE) {
                            player.setMaxFuel();
                        }
                        break;
                }

                pauseWindow.setVisible(false);
                player.resetCalibration();
            }
        };

        TextButton continueButton = new TextButton(localizer.getValue("continue"), skin);
        continueButton.addListener(clickListener);

        TextButton settingsButton = new TextButton("Settings", skin);
        settingsButton.addListener(clickListener);
        settingsButton.setName("settings");

        TextButton menuButton = new TextButton(localizer.getValue("exit"), skin);
        menuButton.addListener(clickListener);
        menuButton.setName("menu");

        pauseWindow.add(continueButton).row();
        pauseWindow.add(settingsButton).padTop(MENU_PADDING_TOP).row();
        pauseWindow.add(menuButton).padTop(MENU_PADDING_TOP);

        addActor(pauseWindow);
    }

    /**
     * Creates fuel gauge.
     */
    private void createFuelMeter() {
        playerFuel = new ProgressBar(0, PLAYER_FUEL_TANK_SIZE, 0.5f, false, skin);
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

        camera.position.x = MathUtils.clamp((int) player.getX(), viewportWidth, map.getMapWidth() - viewportWidth);
        camera.position.y = MathUtils.clamp((int) player.getY(), viewportHeight, map.getMapHeight() - viewportHeight);

        camera.update();
    }

    /**
     * Updates menu positioning.
     */
    private void updateDisplayInfo() {
        Camera camera = getCamera();

        float viewportX = camera.position.x + camera.viewportWidth / 2;
        float viewportY = camera.position.y + camera.viewportHeight / 2;

        playerFuel.setPosition(
            viewportX - playerFuel.getWidth() - SAFE_ZONE_SIZE,
            viewportY - playerFuel.getHeight() - SAFE_ZONE_SIZE
        );

        playerFuel.setValue(player.getFuel());

        pauseWindow.setPosition(
            camera.position.x - pauseWindow.getWidth() / 2,
            camera.position.y - pauseWindow.getHeight() / 2
        );

        collectedItem.setPosition(
            camera.position.x - collectedItem.getWidth() / 2,
            camera.position.y - collectedItem.getHeight() / 2
        );
    }

    @Override
    public void hide() {
        Debug.setCustomDebugString("");
        super.hide();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!pauseWindow.isVisible()) {
            delta *= GAME_SPEED_MODIFIER;

            if (!player.isVisible()) {
                delta = 0;
                pause();
            }
        } else {
            delta = 0;
        }

        totalGameTime += delta;

        Camera worldCamera = getCamera();
        SpriteBatch batch = Setup.getBatch();

        map.renderView(batch, worldCamera);

        batch.begin();
        batch.setProjectionMatrix(worldCamera.combined);
        player.draw(batch, delta);
        batch.end();

        followPlayerObject();
        setNotificationActive();
        updateDisplayInfo();

        act(delta);
        draw();

        Debug.setCustomDebugString(toString());
    }

    @Override
    public boolean keyDown(int key) {
        switch (key) {
            case Input.Keys.S:
                player.collectItemByName(null, POWER_UP_SPEED_MULTIPLIER);
                break;
            case Input.Keys.D:
                player.collectItemByName(null, POWER_UP_DRILL_MULTIPLIER);
                break;
            case Input.Keys.R:
                player.collectItemByName(null, POWER_UP_RADAR_EXTENDER);
                break;
            case Input.Keys.C:
                player.collectItemByName(null, POWER_UP_POINT_MULTIPLIER);
                break;
            case Input.Keys.PLUS:
                player.collectItemByName(null, POWER_UP_RANDOMIZED);
                break;

            case Input.Keys.BACK:
            case Input.Keys.ESCAPE:
                pauseWindow.setVisible(!pauseWindow.isVisible());
                break;
        }

        return true;
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

    @Override
    public String toString() {
        return String.format(
            "Current playtime: %d:%02d:%02d\n%s",
            (int) totalGameTime / (60 * 60),
            (int) totalGameTime / 60 % 60,
            (int) totalGameTime % 60,
            player.toString()
        );
    }
}