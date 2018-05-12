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
    /**
     * Stores current controller value.
     */
    private Vector2 currentValue;

    /**
     * Stores positive threshold data.
     */
    private Vector2 positiveThreshold;

    /**
     * Stores negative threshold data.
     */
    private Vector2 negativeThreshold;

    /**
     * Stores current baseline.
     */
    private Vector3 baseline;

    /**
     * Stores calibration iteration times.
     */
    private int calibrationIterations;

    /**
     * Stores calibration time.
     */
    private float calibrationTime;

    /**
     * Defines inverted states.
     */
    private boolean invertedX, invertedY;

    /**
     * Overloads default constructor.
     *
     * @param settings  SettingsManager instance
     */
    public ControllerManager(SettingsManager settings) {
        reset(settings);
    }

    /**
     * Resets all values.
     *
     * @param settings  SettingsManager instance
     */
    public void reset(SettingsManager settings) {
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

            if (baseline == null) {
                baseline = new Vector3();
            } else {
                baseline.setZero();
            }

            calibrationTime = CONTROLLER_CALIBRATION_TIME;
            calibrationIterations = 0;

            applySettings(settings);
        }

        if (currentValue == null) {
            currentValue = new Vector2();
        } else {
            currentValue.setZero();
        }
    }

    /**
     * Applies user settings.
     *
     * @param settings  SettingsManager instance
     */
    private void applySettings(SettingsManager settings) {
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

    /**
     * Inverts X-axis.
     *
     * @param inverted state
     */
    private void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    /**
     * Inverts Y-axis.
     *
     * @param inverted state
     */
    private void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    /**
     * Gets calibrated value.
     *
     * @param value     raw input value
     * @param baseline  baseline value
     * @param positive  positive threshold value
     * @param negative  negative threshold value
     * @param maxValue  maximum allowed value
     * @return calibrated output
     */
    private int calibratedValue(float value, float baseline, float positive, float negative, float maxValue) {
        if (value < MathUtils.lerp(baseline, baseline - maxValue, negative / 10)) {
            return -1;
        } else if (value > MathUtils.lerp(baseline, baseline + maxValue, positive / 10)) {
            return 1;
        }

        return 0;
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

    /**
     * Gets calibration status.
     *
     * @return true if calibration is active, otherwise false
     */
    public boolean isCalibrating() {
        return baseline != null && calibrationTime > 0;
    }

    /**
     * Updates raw values.
     *
     * @param x accelerometer x value
     * @param y accelerometer y value
     * @param z accelerometer z value
     */
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

    /**
     * Gets upward movement state.
     *
     * @return true if moving, otherwise false
     */
    public boolean isMovingUp() {
        return currentValue.y > 0 || Gdx.input.isKeyPressed(Input.Keys.UP);
    }

    /**
     * Gets downward movement state.
     *
     * @return true if moving, otherwise false
     */
    public boolean isMovingDown() {
        return currentValue.y < 0 || Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    /**
     * Gets left movement state.
     *
     * @return true if moving, otherwise false
     */
    public boolean isMovingLeft() {
        return currentValue.x < 0 || Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    /**
     * Gets right movement state.
     *
     * @return true if moving, otherwise false
     */
    public boolean isMovingRight() {
        return currentValue.x > 0 || Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }
}