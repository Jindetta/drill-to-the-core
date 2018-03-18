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

    private Vector2 maxPositiveThreshold;
    private Vector2 maxNegativeThreshold;

    private Vector2 baseline;
    private int calibrations;
    private float calibrationTime;

    private boolean invertedY;

    public ControllerManager() {
        invertedY = false;
        reset();
    }

    public void reset() {
        currentValue = new Vector2();

        minPositiveThreshold = new Vector2();
        minNegativeThreshold = new Vector2();

        baseline = new Vector2();
        calibrationTime = 1.5f;
        calibrations = 1;

        maxPositiveThreshold = new Vector2(DEFAULT_MAX_THRESHOLD, DEFAULT_MAX_THRESHOLD);
        maxNegativeThreshold = new Vector2(-DEFAULT_MAX_THRESHOLD, -DEFAULT_MAX_THRESHOLD);
    }

    private void calibrate(float x, float y, float delta) {
        if (calibrationTime > 0) {
            calibrationTime -= delta;

            if (calibrationTime > 0) {
                baseline.add(x, y);
                calibrations++;
            } else {
                baseline.set(baseline.x / calibrations, baseline.y / calibrations);
                calibrations = 1;
            }
        }
    }

    private float getCalibratedX(float x) {
        return x < 0 ? x + baseline.x : x > 0 ? x - baseline.x : 0;
    }

    private float getCalibratedY(float y) {
        return y < 0 ? y + baseline.y : y > 0 ? y - baseline.y : 0;
    }

    public void setXThreshold(float positiveThreshold, float negativeThreshold) {
        positiveThreshold = Math.abs(positiveThreshold);
        negativeThreshold = -Math.abs(negativeThreshold);

        if (positiveThreshold < maxPositiveThreshold.x) {
            minPositiveThreshold.x = positiveThreshold;
        } else {
            minPositiveThreshold.x = 0;
        }

        if (negativeThreshold > maxNegativeThreshold.x) {
            minNegativeThreshold.x = negativeThreshold;
        } else {
            minNegativeThreshold.x = 0;
        }
    }

    public void setYThreshold(float positiveThreshold, float negativeThreshold) {
        positiveThreshold = Math.abs(positiveThreshold);
        negativeThreshold = -Math.abs(negativeThreshold);

        if (positiveThreshold < maxPositiveThreshold.y) {
            minPositiveThreshold.y = positiveThreshold;
        } else {
            minPositiveThreshold.y = 0;
        }

        if (negativeThreshold > maxNegativeThreshold.y) {
            minNegativeThreshold.y = negativeThreshold;
        } else {
            minNegativeThreshold.y = 0;
        }
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
            calibrate(x, y, delta);
            x = getCalibratedX(x);
            y = getCalibratedY(y);

            if (invertedY) {
                y = -y;
            }

            if (x > minPositiveThreshold.x || x < minNegativeThreshold.x) {
                currentValue.x += x;
            } else {
                currentValue.x = 0;
            }

            if (y > minPositiveThreshold.y || y < minNegativeThreshold.y) {
                currentValue.y += y;
            } else {
                currentValue.y = 0;
            }

            currentValue.x = MathUtils.clamp(currentValue.x, maxNegativeThreshold.x, maxPositiveThreshold.x);
            currentValue.y = MathUtils.clamp(currentValue.y, maxNegativeThreshold.y, maxPositiveThreshold.y);
        }
    }

    public float getCurrentX() {
        return currentValue.x;
    }

    public float getCurrentY() {
        return currentValue.y;
    }
}