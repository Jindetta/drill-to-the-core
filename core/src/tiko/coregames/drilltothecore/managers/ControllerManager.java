package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * ControllerManager will process all accelerometer input.
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
        SettingsManager settings = SettingsManager.getActiveProfile(true);

        int sensitivityUp = MathUtils.clamp(settings.getInteger("sensitivityUp"), 1, 10);
        int sensitivityDown = MathUtils.clamp(settings.getInteger("sensitivityDown"), 1, 10);
        int sensitivityRight = MathUtils.clamp(settings.getInteger("sensitivityRight"), 1, 10);
        int sensitivityLeft = MathUtils.clamp(settings.getInteger("sensitivityLeft"), 1, 10);

        positiveThreshold.set(sensitivityRight, sensitivityUp);
        negativeThreshold.set(sensitivityLeft, sensitivityDown);

        setInvertedX(settings.getBoolean("invertedX"));
        setInvertedY(settings.getBoolean("invertedY"));
    }

    /**
     * Calibrates accelerometer baseline.
     *
     * @param x     Accelerometer X value
     * @param y     Accelerometer Y value
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

            baseline.set(x, y, z);
        }
    }

    private void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    private void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    private int calibratedValue(float value, float baseline, float positive, float negative, float maxValue) {
        if (value < MathUtils.lerp(baseline, baseline - maxValue, negative / 10)) {
            return -1;
        } else if (value > MathUtils.lerp(baseline, baseline + maxValue, positive / 10)) {
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
            } else {
                updateValues(invertedX ? -x : x, invertedY ? -y : y, invertedY ? z : -z);
            }
        }
    }

    public boolean isCalibrating() {
        return baseline != null && calibrationTime > 0;
    }

    private void updateValues(float x, float y, float z) {
        if (Math.abs(baseline.y) > Math.abs(baseline.z)) {
            currentValue.set(
                calibratedValue(x, baseline.x, positiveThreshold.x, negativeThreshold.x, 5),
                calibratedValue(z, baseline.z, positiveThreshold.y, negativeThreshold.y, 4)
            );
        } else {
            currentValue.set(
                calibratedValue(x, baseline.x, positiveThreshold.x, negativeThreshold.x, 5),
                calibratedValue(y, baseline.y, positiveThreshold.y, negativeThreshold.y, 4)
            );
        }
    }

    public boolean isMovingUp() {
        return currentValue.y > 0 || Gdx.input.isKeyPressed(Input.Keys.UP);
    }

    public boolean isMovingDown() {
        return currentValue.y < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    public boolean isMovingLeft() {
        return currentValue.x < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    public boolean isMovingRight() {
        return currentValue.x > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
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