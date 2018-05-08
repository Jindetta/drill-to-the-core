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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.managers.SoundManager;
import tiko.coregames.drilltothecore.objects.Player;

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
    /**
     * Defines map information.
     */
    private LevelManager map;

    /**
     * Defines current level index.
     */
    private int levelIndex;

    /**
     * Defines pause window.
     */
    private Table pauseWindow;

    /**
     * Defines player object.
     */
    private Player player;

    /**
     * Defines total time played.
     */
    private float totalGameTime;

    /**
     * Defines screen layout.
     */
    private Table screenLayout;

    /**
     * Defines layout for power-ups.
     */
    private Table powerUpLayout;

    /**
     * Defines label for score popup.
     */
    private Label displayScorePopup;

    /**
     * Defines label for game time.
     */
    private Label displayGameTime;

    /**
     * Defines label for score.
     */
    private Label scoreValue;

    /**
     * Defines label for depth.
     */
    private Label depthValue;

    private Label radarBoostValue, speedBoostValue, drillBoostValue, pointBoostValue, fuelBoostValue;
    private Stack radarLayer, speedLayer, drillLayer, pointLayer, fuelLayer;

    /**
     * Defines progress bar for fuel.
     */
    private ProgressBar fuelValue;

    /**
     * Defines manager for sounds.
     */
    private SoundManager sounds;

    public GameScreen() {
        this(0, false);
    }

    /**
     * Overloads default constructor.
     *
     * @param level         level index
     * @param loadCurrent   load from settings
     */
    public GameScreen(int level, boolean loadCurrent) {
        super();
        if (loadCurrent) {
            level = settings.getIntegerIfExists("currentLevel", 0);
        }

        sounds = new SoundManager(settings, assets);

        levelIndex = level;
        resetLevel();
    }

    /**
     * Resets level data.
     */
    private void resetLevel() {
        map = new LevelManager(levelIndex, assets);
        Vector3 playerSpawn = map.getSpawnPoint("player");

        clear();

        if (playerSpawn != null) {
            player = new Player(map, playerSpawn.x, playerSpawn.y, sounds, assets);
        }
        totalGameTime = 0;

        createScreenLayout();
        createPauseWindow();
    }

    /**
     * Creates screen layout.
     */
    private void createScreenLayout() {
        powerUpLayout = new Table();
        powerUpLayout.defaults().size(125, 25);

        screenLayout = new Table();
        screenLayout.columnDefaults(3);
        screenLayout.defaults().expand().uniform();
        screenLayout.add(createInformationLayer()).align(Align.topLeft).top();
        screenLayout.add(createDisplayLayer()).top();
        screenLayout.add(createFuelLayer()).align(Align.topRight).row();
        screenLayout.add(powerUpLayout).align(Align.bottomLeft);

        addActor(screenLayout);
    }

    /**
     * Creates layer for game time and score popup.
     *
     * @return table containing elements
     */
    private Table createDisplayLayer() {
        Table displayLayer = new Table();

        displayGameTime = new Label("", skin, "menu");
        displayGameTime.setAlignment(Align.center);

        displayScorePopup = new Label("", skin, "collected");
        displayScorePopup.setAlignment(Align.center);
        displayScorePopup.setVisible(false);

        displayLayer.add(displayGameTime).padBottom(MENU_DEFAULT_PADDING).row();
        displayLayer.add(displayScorePopup);
        displayLayer.pack();

        return displayLayer;
    }

    /**
     * Creates information layer for scores, depth etc.
     *
     * @return table containing elements
     */
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

        infoLayer.defaults().left().size(125, 25);
        infoLayer.add(scoreLayer).row();
        infoLayer.add(depthLayer);

        return infoLayer;
    }

    /**
     * Updates information.
     */
    private void updateInformationStatus() {
        if (player.getRecentScoreString() != null) {
            SequenceAction sequence = Actions.sequence();
            sequence.addAction(Actions.alpha(1));
            sequence.addAction(Actions.fadeOut(2));
            sequence.addAction(Actions.visible(false));

            displayScorePopup.setText(player.getRecentScoreString());
            displayScorePopup.getActions().clear();
            displayScorePopup.addAction(sequence);
            displayScorePopup.setVisible(true);
        }

        powerUpLayout.clear();

        if (player.getRadarViewTimer() != null) {
            if (radarBoostValue == null) {
                radarBoostValue = new Label("", skin, "clear");
                radarBoostValue.setAlignment(Align.right);

                radarLayer = new Stack(new ImageButton(skin, "radar"), radarBoostValue);
            }

            radarBoostValue.setText(player.getRadarViewTimer());
            powerUpLayout.add(radarLayer).row();
        }

        if (player.getSpeedTimer() != null) {
            if (speedBoostValue == null) {
                speedBoostValue = new Label("", skin, "clear");
                speedBoostValue.setAlignment(Align.right);

                speedLayer = new Stack(new ImageButton(skin, "speed"), speedBoostValue);
            }

            speedBoostValue.setText(player.getSpeedTimer());
            powerUpLayout.add(speedLayer).row();
        }

        if (player.getDrillTimer() != null) {
            if (drillBoostValue == null) {
                drillBoostValue = new Label("", skin, "clear");
                drillBoostValue.setAlignment(Align.right);

                drillLayer = new Stack(new ImageButton(skin, "drill"), drillBoostValue);
            }

            drillBoostValue.setText(player.getDrillTimer());
            powerUpLayout.add(drillLayer).row();
        }

        if (player.getPointsTimer() != null) {
            if (pointBoostValue == null) {
                pointBoostValue = new Label("", skin, "clear");
                pointBoostValue.setAlignment(Align.right);

                pointLayer = new Stack(new ImageButton(skin, "points"), pointBoostValue);
            }

            pointBoostValue.setText(player.getPointsTimer());
            powerUpLayout.add(pointLayer).row();
        }

        if (player.getFuelTimer() != null) {
            if (fuelBoostValue == null) {
                fuelBoostValue = new Label("", skin, "clear");
                fuelBoostValue.setAlignment(Align.right);

                fuelLayer = new Stack(new ImageButton(skin, "fuelBoost"), fuelBoostValue);
            }

            fuelBoostValue.setText(player.getFuelTimer());
            powerUpLayout.add(fuelLayer);
        }
    }

    /**
     * Creates pause menu window.
     */
    private void createPauseWindow() {
        pauseWindow = new Table(skin);
        pauseWindow.setVisible(false);
        pauseWindow.defaults().expandY().center();

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
                }

                pauseWindow.setVisible(false);
                player.resetCalibration();
            }
        };

        ImageButton pausedTitle = new ImageButton(skin, localization.getValue("pausedTitle"));

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

        pauseWindow.add(pausedTitle).top().row();
        pauseWindow.add(continueButton).row();
        pauseWindow.add(restartButton).row();
        pauseWindow.add(settingsButton).row();
        pauseWindow.add(menuButton).row();
        pauseWindow.setBackground("LevelCompleteScreen");
        pauseWindow.setSize(250, 300);

        addActor(pauseWindow);
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
                " %02d:%02d:%02d ",
                (int) totalGameTime / (60 * 60),
                (int) totalGameTime / 60 % 60,
                (int) totalGameTime % 60
            )
        );

        fuelValue.setValue(player.getFuel());
        scoreValue.setText(String.valueOf(player.getTotalScore()));
        depthValue.setText(String.format(Locale.ENGLISH, "%.0f", player.getDrillDepth()));

        screenLayout.setPosition(
            camera.position.x - camera.viewportWidth / 2 + SAFE_ZONE_SIZE,
            camera.position.y - camera.viewportHeight / 2 + SAFE_ZONE_SIZE
        );

        screenLayout.setSize(
            camera.viewportWidth - SAFE_ZONE_SIZE * 2, camera.viewportHeight - SAFE_ZONE_SIZE * 2
        );
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

        if (player.isDepthGoalAchieved() || player.getFuel() <= 0) {
            if (player.isDepthGoalAchieved()) {
                player.addBonusScore(SCORE_COMPLETION_BONUS);

                if (player.getFuelTimer() != null) {
                    player.addBonusScore(SCORE_POWER_UP_COMPENSATION);
                }

                if (player.getPointsTimer() != null) {
                    player.addBonusScore(SCORE_POWER_UP_COMPENSATION);
                }

                if (player.getDrillTimer() != null) {
                    player.addBonusScore(SCORE_POWER_UP_COMPENSATION);
                }

                if (player.getSpeedTimer() != null) {
                    player.addBonusScore(SCORE_POWER_UP_COMPENSATION);
                }

                if (player.getRadarViewTimer() != null) {
                    player.addBonusScore(SCORE_POWER_UP_COMPENSATION);
                }
            }

            Setup.nextScreen(new EndScreen(player, totalGameTime, levelIndex));
        }
    }

    @Override
    public boolean keyDown(int key) {
        switch (key) {
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
        sounds.pauseLongSound("engine");
    }

    @Override
    public void dispose() {
        super.dispose();
        map.dispose();
    }
}