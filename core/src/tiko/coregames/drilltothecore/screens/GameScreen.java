package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import tiko.coregames.drilltothecore.CoreSetup;
import tiko.coregames.drilltothecore.managers.LevelManager;
import tiko.coregames.drilltothecore.objects.BaseObject;
import tiko.coregames.drilltothecore.objects.Player;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class GameScreen extends BaseScreen {
    private Player player;

    /**
     * Defines debug tag for this class.
     */
    private static final String DEBUG_TAG = GameScreen.class.getName();

    public GameScreen(CoreSetup host) {
        super(new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT), host);

        LevelManager.setupLevel(0);
        player = new Player();
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
        LevelManager.dispose();
        super.dispose();
    }
}