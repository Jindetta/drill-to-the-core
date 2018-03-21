package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class ControllerManager {
    private Vector2 currentValue;

    private Vector2 minPositiveThreshold;
    private Vector2 minNegativeThreshold;
    private Vector2 baseline;

    private int calibrations;
    private float calibrationTime;

    private boolean invertedY;

    public ControllerManager() {
        reset();
        applySettings();
    }

    public void reset() {
        currentValue = new Vector2();

        minPositiveThreshold = new Vector2();
        minNegativeThreshold = new Vector2();

        baseline = new Vector2();
        calibrationTime = 1.5f;
        calibrations = 0;
    }

    public void applySettings() {
        SettingsManager settings = SettingsManager.getDefaultProfile();

        float sensitivityLeft = settings.getFloat("sensitivityLeft");
        float sensitivityRight = settings.getFloat("sensitivityRight");
        float sensitivityDown = settings.getFloat("sensitivityDown");
        float sensitivityUp = settings.getFloat("sensitivityUp");
        boolean isInverted = settings.getBoolean("invertedY");

        setYThreshold(sensitivityDown, sensitivityUp);
        setXThreshold(sensitivityLeft, sensitivityRight);
        setInvertedY(isInverted);
    }

    private boolean calibrationMode(float x, float y, float delta) {
        if (calibrationTime > 0) {
            calibrationTime -= delta;

            if (calibrationTime > 0) {
                baseline.add(x, y);
                calibrations++;
            } else {
                baseline.set(baseline.x / calibrations, baseline.y / calibrations);
                calibrations = 0;
            }
        }

        return calibrations > 0;
    }

    public void setXThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.x = Math.abs(positiveThreshold);
        minNegativeThreshold.x = -Math.abs(negativeThreshold);
    }

    public void setYThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.y = Math.abs(positiveThreshold);
        minNegativeThreshold.y = -Math.abs(negativeThreshold);
    }

    public void setInvertedY(boolean inverted) {
        setYThreshold(minNegativeThreshold.y, minPositiveThreshold.y);
        invertedY = inverted;
    }

    public void updateController(float delta) {
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            // Get value from accelerometer (Y = X)
            float x = Gdx.input.getAccelerometerY();
            // Get value from accelerometer (Z = Y)
            float y = Gdx.input.getAccelerometerZ();

            // TODO: Calibration needs testing
            if (calibrationMode(x, y, delta)) {
                return;
            }

            if (invertedY) {
                y = -y;
            }

            if (x > baseline.x + minPositiveThreshold.x || x < baseline.x + minNegativeThreshold.x) {
                currentValue.x = MathUtils.clamp(currentValue.x + x, -MAX_MOVEMENT_VALUE, MAX_MOVEMENT_VALUE);
            } else {
                currentValue.x = 0;
            }

            if (y > baseline.y + minPositiveThreshold.y || y < baseline.y + minNegativeThreshold.y) {
                currentValue.y = MathUtils.clamp(currentValue.y + y, -MAX_MOVEMENT_VALUE, MAX_MOVEMENT_VALUE);
            } else {
                currentValue.y = 0;
            }
        }
    }

    public float getCurrentX() {
        return currentValue.x;
    }

    public float getCurrentY() {
        return currentValue.y;
    }
}