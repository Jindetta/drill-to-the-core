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
    private Vector2 previousValue;

    private Vector2 positiveThreshold;
    private Vector2 negativeThreshold;
    private Vector3 baseline;

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

            if (previousValue == null) {
                previousValue = new Vector2();
            } else {
                previousValue.setZero();
            }

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
    private boolean updateCalibrationValues(float x, float y, float z) {
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

                try {
                    validateCalibrationValues(x, positiveThreshold.x, negativeThreshold.x);

                    if (Math.abs(y) < Math.abs(z)) {
                        validateCalibrationValues(y, positiveThreshold.y, negativeThreshold.y);
                    } else {
                        validateCalibrationValues(z, positiveThreshold.y, negativeThreshold.y);
                    }

                    baseline.set(x, y, z);
                } catch (Exception e) {
                    calibrationTime = CONTROLLER_CALIBRATION_TIME;
                    baseline.setZero();

                    return true;
                }
            }
        }

        return calibrationIterations > 0;
    }

    private void validateCalibrationValues(float value, float positive, float negative) {
        value = normalized(value, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE);

        negative += SENSITIVITY_MULTIPLIER / 2;
        positive += SENSITIVITY_MULTIPLIER / 2;

        if (value - negative < 0 || value + positive > 1) {
            throw new IllegalArgumentException("Calibration values are invalidated");
        }
    }

    private void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    private void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    private int calibratedValue(float value, float baseline, float positive, float negative) {
        baseline = normalized(baseline, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE);
        value = normalized(value, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE);

        return value <= (baseline - negative) ? -1 : value >= (baseline + positive) ? 1 : 0;

        /*float positiveRange = Math.max(normalizedBase, 1 - normalizedBase);
        float negativeRange = 1 - positiveRange;*/
    }

    private float normalized(float value, float min, float max) {
        return (value - Math.min(min, max)) / (Math.max(min, max) - Math.min(min, max));
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

            if (updateCalibrationValues(x, y, z)) {
                return;
            }

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
                z = invertedY ? -z : z;
            }

            updateValues(x, Math.abs(baseline.y) < Math.abs(baseline.z) ? y : -z);
        }
    }

    private void updateValues(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            x = calibratedValue(x, baseline.x, positiveThreshold.x, negativeThreshold.x);

            if (x == 0 || !MathUtils.isZero(Math.abs(currentValue.x + x))) {
                currentValue.set(x, 0);
            }
        } else {
            y = calibratedValue(y, baseline.y, positiveThreshold.y, negativeThreshold.y);

            if (y == 0 || !MathUtils.isZero(Math.abs(currentValue.y + y))) {
                currentValue.set(0, y);
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
            return String.format(
                "\nBASELINE\nX: %.2f%% Y: %.2f%% Z: %.2f%%",
                normalized(baseline.x, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE) * 100,
                normalized(baseline.y, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE) * 100,
                normalized(baseline.z, -MAX_SENSOR_VALUE, MAX_SENSOR_VALUE) * 100
            );
        }

        return "";
    }
}