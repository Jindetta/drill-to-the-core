package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * ControllerManager will process all accelerometer input.
 * Calibration and sensitivity processing is done by this class.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class ControllerManager {
    private Vector2 previousValue;
    private Vector2 currentValue;

    private Vector2 minPositiveThreshold;
    private Vector2 minNegativeThreshold;
    private Vector2 baseline;

    private Vector2 calibrationX;
    private Vector2 calibrationY;

    private int calibrationIterations;
    private float calibrationTime;

    private boolean invertedX, invertedY;

    private int sensitivityLeft, sensitivityRight;
    private int sensitivityUp, sensitivityDown;

    /**
     * Instantiates class.
     */
    public ControllerManager() {
        reset();
    }

    /**
     * Resets all values.
     */
    public void reset() {
        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer)) {
            if (previousValue == null) {
                previousValue = new Vector2();
            } else {
                previousValue.setZero();
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
                baseline = new Vector2();
            } else {
                baseline.setZero();
            }

            calibrationTime = CONTROLLER_CALIBRATION_TIME;
            calibrationIterations = 0;

            applySettings();
        }

        if (currentValue == null) {
            currentValue = new Vector2();
        } else {
            currentValue.setZero();
        }
    }

    /**
     * Applies user settings.
     */
    private void applySettings() {
        SettingsManager settings = SettingsManager.getDefaultProfile();

        sensitivityUp = settings.getInteger("sensitivityUp");
        sensitivityDown = settings.getInteger("sensitivityDown");
        sensitivityRight = settings.getInteger("sensitivityRight");
        sensitivityLeft = settings.getInteger("sensitivityLeft");

        setInvertedX(settings.getBoolean("invertedX"));
        setInvertedY(settings.getBoolean("invertedY"));
    }

    /**
     * Calibrates accelerometer baseline.
     *
     * @param x     Accelerometer X value
     * @param y     Accelerometer Y value
     * @return      True if calibration is active
     */
    private boolean calibrationMode(float x, float y) {
        if (calibrationTime > 0) {
            calibrationTime -= Gdx.graphics.getDeltaTime();

            if (calibrationTime > 0) {
                calibrationIterations++;
                baseline.add(x, y);
            } else {
                // Calibration rounding - test with chair
                x = baseline.x / calibrationIterations;
                y = baseline.y / calibrationIterations;
                calibrationIterations = 0;

                setXThreshold(0.5f + x / 10, 0.5f + x / 10);
                setYThreshold(y / 10, y / 10);

                baseline.set(x, y);
            }
        }

        return calibrationIterations > 0;
    }

    private void setXThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.x = Math.abs(positiveThreshold * sensitivityLeft);
        minNegativeThreshold.x = -Math.abs(negativeThreshold * sensitivityRight);
    }

    private void setYThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.y = Math.abs(positiveThreshold * sensitivityUp);
        minNegativeThreshold.y = -Math.abs(negativeThreshold * sensitivityDown);
    }

    private void setInvertedX(boolean inverted) {
        setXThreshold(minNegativeThreshold.x, minPositiveThreshold.x);
        invertedX = inverted;
    }

    private void setInvertedY(boolean inverted) {
        setYThreshold(minNegativeThreshold.y, minPositiveThreshold.y);
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

    /**
     * Updates all values.
     */
    public void update() {
        if (baseline != null) {
            // Get value from accelerometer (Y = X)
            float x = Gdx.input.getAccelerometerY();
            // Get value from accelerometer (Z = Y)
            float y = Gdx.input.getAccelerometerZ();

            if (calibrationMode(x, y)) {
                return;
            }

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
            }

            if (x + previousValue.x <= MAX_SENSOR_VALUE && x - previousValue.x >= -MAX_SENSOR_VALUE) {
                currentValue.x = calibratedValue(x, baseline.x, minPositiveThreshold.x, minNegativeThreshold.x);
                previousValue.x = Math.abs(x);
            } else {
                currentValue.x = calibratedValue(previousValue.x, baseline.x, minPositiveThreshold.x, minNegativeThreshold.x);
            }

            if (y + previousValue.y <= MAX_SENSOR_VALUE && y - previousValue.y >= -MAX_SENSOR_VALUE) {
                currentValue.y = calibratedValue(y, baseline.y, minPositiveThreshold.y, minNegativeThreshold.y);
                previousValue.y = Math.abs(y);
            } else {
                currentValue.y = calibratedValue(previousValue.y, baseline.y, minPositiveThreshold.y, minNegativeThreshold.y);
            }
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
        if (baseline != null) {
            calibrationX.set(baseline.x + minNegativeThreshold.x, baseline.x + minPositiveThreshold.x);
            calibrationY.set(baseline.y + minNegativeThreshold.y, baseline.y + minPositiveThreshold.y);

            return "\nBASELINE\nX: " + calibrationX.toString() + "\nY: " + calibrationY.toString();
        }

        return "";
    }
}