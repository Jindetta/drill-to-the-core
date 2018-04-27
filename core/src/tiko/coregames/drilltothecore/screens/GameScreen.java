package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Debug;

import java.util.Locale;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class GameScreen extends BaseScreen {
    private LevelManager map;

    private Window pauseWindow;
    private Label collectedItem;
    private Player player;

    private float totalGameTime;
    private Label gameTimer;

    private TiledDrawable playerFuel, fuelColor;

    public GameScreen() {
        resetLevel();
    }

    private void resetLevel() {
        map = new LevelManager(0);
        Vector3 playerSpawn = map.getSpawnPoint("player");

        clear();

        if (playerSpawn != null) {
            player = new Player(map, playerSpawn.x, playerSpawn.y);
            createFuelMeter();
        }

        createPauseWindow();
        createDisplayElements();
        totalGameTime = 0;
    }

    private void createDisplayElements() {
        // Notification popup for collected items
        collectedItem = new Label("", skin);
        collectedItem.setAlignment(Align.center);
        collectedItem.setColor(Color.GREEN);
        collectedItem.setVisible(false);

        addActor(collectedItem);

        // Game time
        gameTimer = new Label("", skin);
        gameTimer.setAlignment(Align.center);

        addActor(gameTimer);
    }

    private void setNotificationActive() {
        if (player.getRecentScoreString() != null) {
            SequenceAction sequence = Actions.sequence();
            sequence.addAction(Actions.alpha(1));
            sequence.addAction(Actions.fadeOut(3));
            sequence.addAction(Actions.visible(false));

            collectedItem.setText(player.getRecentScoreString());
            collectedItem.getActions().clear();
            collectedItem.addAction(sequence);
            collectedItem.setVisible(true);
        }
    }

    /**
     * Creates pause menu window.
     */
    private void createPauseWindow() {
        pauseWindow = new Window(coreLocalization.getValue("pause_title"), skin);
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
                    case "restart":
                        resetLevel();
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

        TextButton continueButton = new TextButton(coreLocalization.getValue("pause_continue"), skin);
        continueButton.addListener(clickListener);

        TextButton restartButton = new TextButton(coreLocalization.getValue("pause_restartLevel"), skin);
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        TextButton settingsButton = new TextButton(coreLocalization.getValue("pause_settings"), skin);
        settingsButton.addListener(clickListener);
        settingsButton.setName("settings");

        TextButton menuButton = new TextButton(coreLocalization.getValue("pause_exit"), skin);
        menuButton.addListener(clickListener);
        menuButton.setName("menu");

        pauseWindow.add(continueButton).row();
        pauseWindow.add(restartButton).padTop(MENU_PADDING_TOP).row();
        pauseWindow.add(settingsButton).padTop(MENU_PADDING_TOP).row();
        pauseWindow.add(menuButton).padTop(MENU_PADDING_TOP);

        addActor(pauseWindow);
    }

    /**
     * Creates fuel gauge.
     */
    private void createFuelMeter() {
        /*playerFuel = skin.getTiledDrawable("Gasmeter");
        updateFuelColor();*/
    }

    private void updateFuelColor() {
       /*float fuelAmount = player.getFuel() / PLAYER_FUEL_TANK_SIZE;

        if (fuelAmount >= .75f) {
            fuelColor = skin.getTiledDrawable("Gas_100");
        } else if (fuelAmount >= .50f) {
            fuelColor = skin.getTiledDrawable("Gas_75");
        } else if (fuelAmount >= .25f) {
            fuelColor = skin.getTiledDrawable("Gas_50");
        } else {
            fuelColor = skin.getTiledDrawable("Gas_25");
        }*/
    }

    /**
     * Moves camera with player.
     */
    private void followPlayerObject() {
        OrthographicCamera camera = (OrthographicCamera) getCamera();

        float viewportWidth = camera.viewportWidth * camera.zoom / 2;
        float viewportHeight = camera.viewportHeight * camera.zoom / 2;
        int originX = (int) player.getX() + BIG_TILE_SIZE / 2;
        int originY = (int) player.getY();

        camera.position.x = MathUtils.clamp(originX, viewportWidth, map.getMapWidth() - viewportWidth);
        camera.position.y = MathUtils.clamp(originY, viewportHeight, map.getMapHeight() - viewportHeight);

        camera.update();
    }

    /**
     * Updates menu positioning.
     */
    private void updateDisplayInfo() {
        Camera camera = getCamera();

        float viewportX = camera.position.x + camera.viewportWidth / 2;
        float viewportY = camera.position.y + camera.viewportHeight / 2;

        pauseWindow.setPosition(
            camera.position.x - pauseWindow.getWidth() / 2,
            camera.position.y - pauseWindow.getHeight() / 2
        );

        collectedItem.setPosition(
            camera.position.x - collectedItem.getWidth() / 2,
            viewportY - collectedItem.getPrefHeight() * 4
        );

        gameTimer.setText(
            String.format(
                Locale.ENGLISH,
                "%02d:%02d:%02d",
                (int) totalGameTime / (60 * 60),
                (int) totalGameTime / 60 % 60,
                (int) totalGameTime % 60
            )
        );

        gameTimer.setPosition(
            camera.position.x - collectedItem.getWidth() / 2,
            viewportY - gameTimer.getPrefHeight() / 2 - SAFE_ZONE_SIZE
        );

        updateFuelColor();
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

        Debug.setCustomDebugString(player.toString());

        if (player.isDepthGoalAchieved() || player.getFuel() <= 0) {
            Setup.nextScreen(new EndScreen("Game ended", player.getTotalScore(),
                    player.getBaseScore(), player.getDrillDepth()));
        }
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
            case Input.Keys.F:
                player.collectItemByName(null, POWER_UP_UNLIMITED_FUEL);
                break;
            case Input.Keys.PLUS:
                player.collectItemByName(null, POWER_UP_RANDOMIZED);
                break;
            case Input.Keys.MINUS:
                for (String id : RANDOM_POWER_UPS) {
                    player.collectItemByName(null, id);
                }
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
}