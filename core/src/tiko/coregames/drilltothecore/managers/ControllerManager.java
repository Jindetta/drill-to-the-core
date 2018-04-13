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

    private boolean requiresSpecialMovement;
    private boolean gamingXRActive;

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
    protected void applySettings() {
        SettingsManager settings = SettingsManager.getDefaultProfile();

        gamingXRActive = settings.isGamingXREnabled();

        int sensitivityUp = MathUtils.clamp(settings.getInteger("sensitivityUp"), 1, 10);
        int sensitivityDown = MathUtils.clamp(settings.getInteger("sensitivityDown"), 1, 10);
        int sensitivityRight = MathUtils.clamp(settings.getInteger("sensitivityRight"), 1, 10);
        int sensitivityLeft = MathUtils.clamp(settings.getInteger("sensitivityLeft"), 1, 10);

        positiveThreshold.set(SENSITIVITY_MULTIPLIER * sensitivityRight, SENSITIVITY_MULTIPLIER * sensitivityUp);
        negativeThreshold.set(SENSITIVITY_MULTIPLIER * sensitivityLeft, SENSITIVITY_MULTIPLIER * sensitivityDown);

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
    private void updateCalibrationValues(float x, float y, float z) {
        calibrationTime -= Gdx.graphics.getDeltaTime();

        if (calibrationTime > 0) {
            calibrationIterations++;
            baseline.add(x, y, z);
        } else {
            x = baseline.x / calibrationIterations;
            y = baseline.y / calibrationIterations;
            z = baseline.z / calibrationIterations;
            calibrationIterations = 0;

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
            }
        }
    }

    private void validateCalibrationValues(float value, float positive, float negative) {
        if (value - negative <= -MAX_SENSOR_VALUE || value + positive >= MAX_SENSOR_VALUE) {
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
        if (value < MathUtils.lerp(0, -9, negative / 10)) {
            return -1;
        } else if (value > baseline + positive) {
            return 1;
        }

        return 0;
    }

    private float normalized(float value, float min, float max) {
        return (value - min) / (max - min);
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

            if (isCalibrating()) {
                updateCalibrationValues(x, y, z);
                return;
            }

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
                z = invertedY ? -z : z;
            }

            x += baseline.x < 0 ? Math.abs(baseline.x) : -baseline.x;
            y += baseline.y < 0 ? Math.abs(baseline.y) : -baseline.y;
            z += baseline.z < 0 ? Math.abs(baseline.z) : -baseline.z;

            updateValues(x, y, -z);
        }
    }

    public boolean isCalibrating() {
        return baseline != null && calibrationTime > 0;
    }

    private void updateValues(float x, float y, float z) {
        if (Math.abs(baseline.y) > Math.abs(baseline.z)) {
            currentValue.set(
                calibratedValue(x, baseline.x, positiveThreshold.x, negativeThreshold.x),
                calibratedValue(z, baseline.z, positiveThreshold.y, negativeThreshold.y)
            );
        } else {
            currentValue.set(
                calibratedValue(x, baseline.x, positiveThreshold.x, negativeThreshold.x),
                calibratedValue(y, baseline.y, positiveThreshold.y, negativeThreshold.y)
            );
        }
    }

    public float getCurrentX() {
        return currentValue.x;
    }

    public float getCurrentY() {
        return currentValue.y;
    }

    public void setSpecialMovement(boolean value) {
        requiresSpecialMovement = value;
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