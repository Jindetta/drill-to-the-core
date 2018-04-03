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
    private Vector2 currentValue;

    private Vector2 positiveThreshold;
    private Vector2 negativeThreshold;
    private Vector3 baseline;

    private Vector2 calibrationX;
    private Vector2 calibrationY;

    private int calibrationIterations;
    private float calibrationTime;

    private boolean invertedX, invertedY;

    private int sensitivityLeft, sensitivityRight;
    private int sensitivityUp, sensitivityDown;

    private boolean requiresSpecialMovement;

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
            if (positiveThreshold == null) {
                positiveThreshold = new Vector2();
            } else {
                positiveThreshold.setZero();
            }

            if (negativeThreshold == null) {
                negativeThreshold = new Vector2();
            } else {
                negativeThreshold.setZero();
            }

            // FOR DEBUGGING PURPOSES ONLY
            calibrationX = new Vector2();
            calibrationY = new Vector2();

            if (baseline == null) {
                baseline = new Vector3();
            } else {
                baseline.setZero();
            }

            requiresSpecialMovement = false;
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
    private boolean calibrationMode(float x, float y, float z) {
        if (calibrationTime > 0) {
            calibrationTime -= Gdx.graphics.getDeltaTime();

            if (calibrationTime > 0) {
                calibrationIterations++;
                baseline.add(x, y, z);
            } else {
                x = baseline.x / calibrationIterations;
                y = baseline.y / calibrationIterations;
                z = baseline.z / calibrationIterations;
                calibrationIterations = 0;

                positiveThreshold.set(SENSITIVITY_MULTIPLIER * sensitivityRight, SENSITIVITY_MULTIPLIER * sensitivityUp);
                negativeThreshold.set(SENSITIVITY_MULTIPLIER * sensitivityLeft, SENSITIVITY_MULTIPLIER * sensitivityDown);

                baseline.set(x, y, z);
            }
        }

        return calibrationIterations > 0;
    }

    private void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    private void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    // Additional calibration method
    private int calibratedValueX(float x, float baseline) {
        if (x < baseline) {
            x = normalized(x, -MAX_SENSOR_VALUE, baseline);
            if (x > negativeThreshold.x) {
                return -1;
            }
        } else {
            x = normalized(x, baseline, MAX_SENSOR_VALUE);
            if (x > positiveThreshold.x) {
                return 1;
            }
        }

        return 0;
    }

    private int calibratedValueY(float y, float baseline) {
        if (y < baseline) {
            y = normalized(y, -MAX_SENSOR_VALUE, baseline);
            if (y > negativeThreshold.y) {
                return -1;
            }
        } else {
            y = normalized(y, baseline, MAX_SENSOR_VALUE);
            if (y > positiveThreshold.y) {
                return 1;
            }
        }

        return 0;
    }

    private float normalized(float value, float min, float max) {
        value = (value - min) / (max - min);
        return value < 0 ? 1 - value : value;
    }

    /**
     * Updates all values.
     */
    public void update() {
        if (baseline != null) {
            // X axis (landscape orientation)
            float x = Gdx.input.getAccelerometerY();
            // Y axis (landscape orientation)
            float y = Gdx.input.getAccelerometerZ();
            // Y axis (alternate axis)
            float z = Gdx.input.getAccelerometerX();

            if (calibrationMode(x, y, z)) {
                return;
            }

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
                z = invertedY ? -z : z;
            }

            if (Math.abs(x) > Math.abs(y)) {
                currentValue.set(calibratedValueX(x, baseline.x), 0);
            } else {
                if (Math.abs(baseline.y) > Math.abs(baseline.z)) {
                    currentValue.set(0, calibratedValueY(-z, baseline.z));
                } else {
                    currentValue.set(0, calibratedValueY(y, baseline.y));
                }
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
            calibrationX.set(baseline.x + negativeThreshold.x, baseline.x + positiveThreshold.x);
            calibrationY.set(baseline.y + negativeThreshold.y, baseline.y + positiveThreshold.y);

            return "\nBASELINE\nX: " + calibrationX.toString() + "\nY: " + calibrationY.toString();
        }

        return "";
    }
}