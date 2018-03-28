package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class ControllerManager {
    private Vector2 currentValue;

    private Vector2 minPositiveThreshold;
    private Vector2 minNegativeThreshold;
    private Vector3 baseline;

    private Vector2 calibrationX;
    private Vector2 calibrationY;

    private int calibrationIterations;
    private float calibrationTime;

    private boolean invertedX, invertedY;

    public ControllerManager() {
        reset();
    }

    public void reset() {
        if (currentValue == null) {
            currentValue = new Vector2();
        } else {
            currentValue.setZero();
        }

        if (minPositiveThreshold == null) {
            minPositiveThreshold = new Vector2();
        } else {
            minPositiveThreshold.setZero();
        }

        if (minNegativeThreshold == null) {
            minNegativeThreshold = new Vector2();
        } else {
            minNegativeThreshold.setZero();
        }

        // FOR DEBUGGING PURPOSES ONLY
        calibrationX = new Vector2();
        calibrationY = new Vector2();

        if (baseline == null) {
            baseline = new Vector3();
        } else {
            baseline.setZero();
        }

        calibrationTime = CONTROLLER_CALIBRATION_TIME;
        calibrationIterations = 0;

        applySettings();
    }

    private void applySettings() {
        SettingsManager settings = SettingsManager.getDefaultProfile();

        setYThreshold(settings.getFloat("sensitivityDown"), settings.getFloat("sensitivityUp"));
        setXThreshold(settings.getFloat("sensitivityLeft"), settings.getFloat("sensitivityRight"));

        setInvertedX(settings.getBoolean("invertedX"));
        setInvertedY(settings.getBoolean("invertedY"));
    }

    private boolean calibrationMode(float x, float y, float z) {
        if (calibrationTime > 0) {
            calibrationTime -= Gdx.graphics.getDeltaTime();

            if (calibrationTime > 0) {
                calibrationIterations++;
                baseline.add(x, y, z);
            } else {
                // Calibration rounding - test with chair
                x = baseline.x / calibrationIterations;
                y = baseline.y / calibrationIterations;
                z = baseline.z / calibrationIterations;
                calibrationIterations = 0;

                baseline.set(x, y, z);
            }
        }

        return calibrationIterations > 0;
    }

    private void setXThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.x = Math.abs(positiveThreshold);
        minNegativeThreshold.x = -Math.abs(negativeThreshold);
    }

    private void setYThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.y = Math.abs(positiveThreshold);
        minNegativeThreshold.y = -Math.abs(negativeThreshold);
    }

    private void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    private void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    // Additional calibration method
    private int calibratedValue(float value, float baseline, float positiveThreshold, float negativeThreshold) {
        positiveThreshold += baseline;
        negativeThreshold += baseline;

        if ((positiveThreshold + value) / 2 > positiveThreshold) {
            return 1;
        }

        if ((negativeThreshold + value) / 2 < negativeThreshold) {
            return -1;
        }

        return 0;
    }

    public void update() {
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            // Get value from accelerometer (Y = X)
            float x = Gdx.input.getAccelerometerY();
            // Get value from accelerometer (Z = Y)
            float y = Gdx.input.getAccelerometerZ();

            float z = Gdx.input.getAccelerometerX();

            // TODO: Calibration needs testing
            if (calibrationMode(x, y, z)) {
                return;
            }

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
            }

            if (calibratedValue(z, baseline.z, minPositiveThreshold.y, minNegativeThreshold.y) != 0) {
                currentValue.y = calibratedValue(y, baseline.y, minPositiveThreshold.y, minNegativeThreshold.y);
            } else {
                currentValue.y = 0;
            }

            currentValue.x = calibratedValue(x, baseline.x, minPositiveThreshold.x, minNegativeThreshold.x);
        }
    }

    public float getCurrentX() {
        return currentValue.x;
    }

    public float getCurrentY() {
        return currentValue.y;
    }

    @Override
    public String toString() {
        calibrationX.set(baseline.x + minNegativeThreshold.x, baseline.x + minPositiveThreshold.x);
        calibrationY.set(baseline.y + minNegativeThreshold.y, baseline.y + minPositiveThreshold.y);

        return "BASELINE\nX: " + calibrationX.toString() + "\nY: " + calibrationY.toString();
    }
}