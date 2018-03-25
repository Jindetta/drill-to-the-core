package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class ControllerManager {
    private Vector2 currentValue;

    private Vector2 minPositiveThreshold;
    private Vector2 minNegativeThreshold;
    private Vector2 baseline;

    private Vector2 calibrationX;
    private Vector2 calibrationY;

    private Matrix4 rotationMatrix;
    private Vector3 rotationVector;

    private int calibrationIterations;
    private float calibrationTime;

    private boolean invertedX, invertedY;

    public ControllerManager() {
        reset();
    }

    public void reset() {
        currentValue = new Vector2();

        minPositiveThreshold = new Vector2();
        minNegativeThreshold = new Vector2();

        // FOR DEBUGGING PURPOSES ONLY
        calibrationX = new Vector2();
        calibrationY = new Vector2();

        baseline = new Vector2();
        calibrationTime = CONTROLLER_CALIBRATION_TIME;
        calibrationIterations = 0;

        applySettings();
    }

    public void applySettings() {
        SettingsManager settings = SettingsManager.getDefaultProfile();

        setYThreshold(settings.getFloat("sensitivityDown"), settings.getFloat("sensitivityUp"));
        setXThreshold(settings.getFloat("sensitivityLeft"), settings.getFloat("sensitivityRight"));

        setInvertedX(settings.getBoolean("invertedX"));
        setInvertedY(settings.getBoolean("invertedY"));
    }

    // Additional calibration method - Needs more testing
    private void createRotationMatrix() {
        Vector3 values = new Vector3(
            Gdx.input.getAccelerometerX(),
            Gdx.input.getAccelerometerY(),
            Gdx.input.getAccelerometerZ()
        );

        Quaternion quaternion = new Quaternion();
        quaternion.setFromCross(new Vector3(0, 0, 1), values.nor());

        rotationMatrix = new Matrix4(Vector3.Zero, quaternion, new Vector3(1, 1, 1)).inv();
    }

    // Additional calibration method - Needs more testing
    private void setInputCalibration() {
        float x = Gdx.input.getAccelerometerX();
        float y = Gdx.input.getAccelerometerY();
        float z = Gdx.input.getAccelerometerZ();

        if (rotationVector == null) {
            rotationVector = new Vector3(x, y, z);
        } else {
            rotationVector.set(x, y, z);
        }

        rotationVector.mul(rotationMatrix);
    }

    private boolean calibrationMode(float x, float y, float delta) {
        if (calibrationTime > 0) {
            calibrationTime -= delta;

            if (calibrationTime > 0) {
                // Threshold integration - test with chair
                x += (minPositiveThreshold.x + minNegativeThreshold.x) / 2;
                y += (minPositiveThreshold.y + minNegativeThreshold.y) / 2;
                calibrationIterations++;

                baseline.add(x, y);
            } else {
                // Calibration rounding - test with chair
                x = Math.round(baseline.x / calibrationIterations * 100) / 100;
                y = Math.round(baseline.y / calibrationIterations * 100) / 100;
                calibrationIterations = 0;

                baseline.set(x, y);
            }
        }

        return calibrationIterations > 0;
    }

    public void setXThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.x = Math.abs(positiveThreshold);
        minNegativeThreshold.x = -Math.abs(negativeThreshold);
    }

    public void setYThreshold(float positiveThreshold, float negativeThreshold) {
        minPositiveThreshold.y = Math.abs(positiveThreshold);
        minNegativeThreshold.y = -Math.abs(negativeThreshold);
    }

    public void setInvertedX(boolean inverted) {
        invertedX = inverted;
    }

    public void setInvertedY(boolean inverted) {
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

            if (invertedX || invertedY) {
                x = invertedX ? -x : x;
                y = invertedY ? -y : y;
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

    @Override
    public String toString() {
        calibrationX.set(baseline.x + minNegativeThreshold.x, baseline.x + minPositiveThreshold.x);
        calibrationY.set(baseline.y + minNegativeThreshold.y, baseline.y + minPositiveThreshold.y);

        return "BASELINE\nX: " + calibrationX.toString() + "\nY: " + calibrationY.toString();
    }
}