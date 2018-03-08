package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.BaseObject;
import tiko.coregames.drilltothecore.objects.Player;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen {
    private Player player;

    public GameScreen(CoreSetup host) {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT), host);

        LevelManager.setupLevel(0);
        Rectangle playerSpawn = LevelManager.getSpawnPoint("player");

        if (playerSpawn != null) {
            player = new Player();
            player.setPosition(playerSpawn.x, playerSpawn.y);
        }
    }

    private void followObject(BaseObject object) {
        Camera camera = stage.getCamera();

        if (camera != null && object != null) {
            camera.position.x = object.getX();
            camera.position.y = object.getY();
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        LevelManager.applyCameraAndRender((OrthographicCamera) stage.getCamera());

        batch.begin();
        player.draw(batch, delta);
        batch.end();

        followObject(player);
    }

    @Override
    public void dispose() {
        super.dispose();
        LevelManager.dispose();
    }
}