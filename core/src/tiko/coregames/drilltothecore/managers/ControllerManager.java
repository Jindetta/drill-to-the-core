package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import tiko.coregames.drilltothecore.objects.BaseObject;

public class ControllerManager {
    private float valueX, valueY;
    private float adjustedX, adjustedY;

    private float clampX, clampY;
    private float deadzoneX, deadzoneY;

    public ControllerManager() {
        valueX = 0;
        valueY = 0;

        adjustedX = 0;
        adjustedY = 0;

        clampX = 0;
        clampY = 0;
    }

    public void setLimits(float maxMovementX, float maxMovementY, float deadzoneX, float deadzoneY) {
        clampX = maxMovementX;
        clampY = maxMovementY;

        this.deadzoneX = deadzoneX;
        this.deadzoneY = deadzoneY;
    }

    public void updateController(BaseObject object, float delta) {
        // TODO: Changes need to be made
        float x = Gdx.input.getAccelerometerY();
        float y = Gdx.input.getAccelerometerZ();

        if (!MathUtils.isZero(x, deadzoneX)) {
            valueX += x;

            if (!MathUtils.isZero(clampX)) {
                valueX = MathUtils.clamp(valueX, -clampX, clampX);
            }
        } else {
            valueX = 0;
        }

        if (!MathUtils.isZero(y, deadzoneY)) {
            valueY += y;

            if (!MathUtils.isZero(clampY)) {
                valueY = MathUtils.clamp(valueY, -clampY, clampY);
            }
        } else {
            valueY = 0;
        }

        object.translate(valueX * delta, valueY * delta);
    }
}