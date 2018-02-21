package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;

public class ControllerManager {
    private float valueX, valueY;
    private float adjustedX, adjustedY;

    public ControllerManager() {
        valueX = 0;
        valueY = 0;

        adjustedX = 0;
        adjustedY = 0;
    }

    public void updateController() {
        // TODO: Changes need to be made
        valueX += Gdx.input.getAccelerometerY();
        valueY += Gdx.input.getAccelerometerZ();
    }
}