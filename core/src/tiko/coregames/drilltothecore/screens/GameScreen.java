package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.SoundManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Debug;

import java.util.Locale;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * GameScreen class will display game screen.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class GameScreen extends BaseScreen {
    private LevelManager map;
    private int levelIndex;

    private Window pauseWindow;
    private Player player;

    private float totalGameTime;

    private Table screenLayout;
    private Table powerupLayout;

    private Label displayScoreValue;
    private Label displayGameTime;

    private Label scoreValue, depthValue;

    private ProgressBar fuelValue;

    private SoundManager sounds;

    public GameScreen() {
        this(0, false);
    }

    public GameScreen(int level, boolean loadCurrent) {
        super();
        if (loadCurrent) {
            level = settings.getIntegerIfExists("currentLevel", 0);
        }

        sounds = new SoundManager(settings);

        levelIndex = level;
        resetLevel();
    }

    private void resetLevel() {
        map = new LevelManager(levelIndex);
        Vector3 playerSpawn = map.getSpawnPoint("player");

        clear();

        if (playerSpawn != null) {
            player = new Player(map, playerSpawn.x, playerSpawn.y, sounds);
        }
        totalGameTime = 0;

        createScreenLayout();
        createPauseWindow();
    }

    private void createScreenLayout() {
        screenLayout = new Table();
        screenLayout.columnDefaults(3);
        screenLayout.defaults().expand().uniform();
        screenLayout.add(createInformationLayer()).align(Align.topLeft).top();
        screenLayout.add(createDisplayLayer()).top();
        screenLayout.add(createFuelLayer()).align(Align.topRight).row();

        addActor(screenLayout);
    }

    private Table createDisplayLayer() {
        Table displayLayer = new Table();

        displayGameTime = new Label("", skin, "gametime");
        displayGameTime.setAlignment(Align.center);

        displayScoreValue = new Label("", skin, "collected");
        displayScoreValue.setAlignment(Align.center);
        displayScoreValue.setVisible(false);

        displayLayer.add(displayGameTime).padBottom(MENU_DEFAULT_PADDING).row();
        displayLayer.add(displayScoreValue);
        displayLayer.pack();

        return displayLayer;
    }

    private Table createInformationLayer() {
        Table infoLayer = new Table();

        Stack scoreLayer = new Stack(
            new ImageButton(skin, "score"),
            scoreValue = new Label("", skin, "clear")
        );

        scoreValue.setAlignment(Align.right);

        Stack depthLayer = new Stack(
            new ImageButton(skin, "depth"),
            depthValue = new Label("", skin, "clear")
        );

        depthValue.setAlignment(Align.right);

        powerupLayout = new Table();
        powerupLayout.defaults().left().size(125, 25);

        infoLayer.add(scoreLayer).size(125, 25).left().row();
        infoLayer.add(depthLayer).size(125, 25).left().row();
        infoLayer.add(powerupLayout);

        return infoLayer;
    }

    private void updateInformationStatus() {
        if (player.getRecentScoreString() != null) {
            SequenceAction sequence = Actions.sequence();
            sequence.addAction(Actions.alpha(1));
            sequence.addAction(Actions.fadeOut(2));
            sequence.addAction(Actions.visible(false));

            displayScoreValue.setText(player.getRecentScoreString());
            displayScoreValue.getActions().clear();
            displayScoreValue.addAction(sequence);
            displayScoreValue.setVisible(true);
        }
    }

    /**
     * Creates pause menu window.
     */
    private Window createPauseWindow() {
        pauseWindow = new Window("", skin, "pauseWindow");
        pauseWindow.setResizable(false);
        pauseWindow.setMovable(false);
        pauseWindow.setVisible(false);

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "settingsMenu":
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

        ImageButton continueButton = new ImageButton(skin, localization.getValue("continueGame"));
        continueButton.addListener(clickListener);

        ImageButton restartButton = new ImageButton(skin, localization.getValue("restartGame"));
        restartButton.addListener(clickListener);
        restartButton.setName("restart");

        ImageButton settingsButton = new ImageButton(skin, localization.getValue("settingsGame"));
        settingsButton.addListener(clickListener);
        settingsButton.setName("settingsMenu");

        ImageButton menuButton = new ImageButton(skin, localization.getValue("backButtonGame"));
        menuButton.addListener(clickListener);
        menuButton.setName("menu");

        pauseWindow.add(continueButton).row();
        pauseWindow.add(restartButton).padTop(MENU_DEFAULT_PADDING).row();
        pauseWindow.add(settingsButton).padTop(MENU_DEFAULT_PADDING).row();
        pauseWindow.add(menuButton).padTop(MENU_DEFAULT_PADDING).row();
        pauseWindow.setSize(250, 300);

        return pauseWindow;
    }

    /**
     * Creates fuel layer, which contains fuel meter and icon.
     */
    private Table createFuelLayer() {
        Table fuelLayer = new Table();

        ImageButton fuelImage = new ImageButton(skin, "fuel");
        fuelValue = new ProgressBar(0, PLAYER_FUEL_TANK_SIZE, 1, false, skin);
        fuelValue.setDisabled(true);

        fuelLayer.add(fuelImage).padRight(MENU_DEFAULT_PADDING).height(25);
        fuelLayer.add(fuelValue).height(25);
        fuelLayer.pack();

        return fuelLayer;
    }

    /**
     * Moves camera with player.
     */
    private void followPlayerObject() {
        OrthographicCamera camera = (OrthographicCamera) getCamera();

        float viewportWidth = camera.viewportWidth * camera.zoom / 2;
        float viewportHeight = camera.viewportHeight * camera.zoom / 2;

        camera.position.x = MathUtils.clamp((int) player.getCenterX(), viewportWidth, map.getMapWidth() - viewportWidth);
        camera.position.y = MathUtils.clamp((int) player.getY(), viewportHeight, map.getMapHeight() - viewportHeight);

        camera.update();
    }

    /**
     * Updates menu positioning.
     */
    private void updateDisplayInfo() {
        Camera camera = getCamera();

        pauseWindow.setPosition(
            camera.position.x - pauseWindow.getWidth() / 2,
            camera.position.y - pauseWindow.getHeight() / 2
        );

        displayGameTime.setText(
            String.format(
                Locale.ENGLISH,
                "%02d:%02d:%02d",
                (int) totalGameTime / (60 * 60),
                (int) totalGameTime / 60 % 60,
                (int) totalGameTime % 60
            )
        );

        fuelValue.setValue(player.getFuel());
        scoreValue.setText(String.valueOf(player.getTotalScore()));
        depthValue.setText(String.format("%.0f", player.getDrillDepth()));

        screenLayout.setPosition(
            (camera.position.x - camera.viewportWidth / 2) + SAFE_ZONE_SIZE,
            (camera.position.y - camera.viewportHeight / 2) + SAFE_ZONE_SIZE
        );

        screenLayout.setSize(
            camera.viewportWidth - SAFE_ZONE_SIZE * 2, camera.viewportHeight - SAFE_ZONE_SIZE * 2
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
        updateInformationStatus();
        updateDisplayInfo();

        act(delta);
        draw();

        Debug.setCustomDebugString(player.toString());

        if (player.isDepthGoalAchieved() || player.getFuel() <= 0) {
            Setup.nextScreen(new EndScreen("GAME ENDED", player.getTotalScore(),
                    0, player.getDrillDepth(), levelIndex));
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
        sounds.dispose();
        player.dispose();
        map.dispose();
    }
}