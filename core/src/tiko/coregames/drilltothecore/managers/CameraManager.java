package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class CameraManager {
    private OrthographicCamera worldCamera;
    private OrthographicCamera hudCamera;

    public CameraManager() {
        hudCamera = new OrthographicCamera();
        worldCamera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);

        worldCamera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
    }

    public void resizeHud(int width, int height) {
        hudCamera.setToOrtho(false, width, height);
    }

    public void applyWorldCamera(SpriteBatch batch) {
        batch.setProjectionMatrix(worldCamera.combined);
        worldCamera.update();
    }

    public void applyHudCamera(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        hudCamera.update();
    }
}