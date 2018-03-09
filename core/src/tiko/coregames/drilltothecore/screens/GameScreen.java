package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.Player;
import tiko.coregames.drilltothecore.utilities.Debugger;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen implements Debugger {
    private Player player;

    public GameScreen(CoreSetup host) {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT), host);

        LevelManager.setupLevel(0);
        player = new Player();
    }

    private void followObject() {
        Camera camera = stage.getCamera();

        if (camera != null) {
            camera.position.x = player.getX();
            camera.position.y = player.getY();

            camera.update();
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        LevelManager.applyCameraAndRender((OrthographicCamera) stage.getCamera());

        batch.begin();
        player.draw(batch, delta);
        batch.end();

        followObject();
    }

    @Override
    public void dispose() {
        LevelManager.dispose();
        player.dispose();
        super.dispose();
    }

    public static String getDebugTag() {
        return GameScreen.class.getSimpleName();
    }
}