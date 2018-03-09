package tiko.coregames.drilltothecore.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import tiko.coregames.drilltothecore.objects.BaseObject;
import tiko.coregames.drilltothecore.utilities.Debugger;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public class ControllerManager implements Debugger {
    private BaseObject owner;

    private Vector2 currentValue;

    private Vector2 minPositiveThreshold;
    private Vector2 minNegativeThreshold;

    private Vector2 maxPositiveThreshold;
    private Vector2 maxNegativeThreshold;

    private boolean invertedY;

    public ControllerManager(BaseObject owner) {
        this.owner = owner;
        invertedY = false;

        reset();
    }

    public void reset() {
        currentValue = new Vector2();

        minPositiveThreshold = new Vector2();
        minNegativeThreshold = new Vector2();

        maxPositiveThreshold = new Vector2(DEFAULT_MAX_THRESHOLD, DEFAULT_MAX_THRESHOLD);
        maxNegativeThreshold = new Vector2(-DEFAULT_MAX_THRESHOLD, -DEFAULT_MAX_THRESHOLD);
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

    // TODO: Make use of inverted value if necessary
    public void setInvertedY(boolean inverted) {
        invertedY = inverted;
    }

    public void updateController(float delta) {
        // Get value from accelerometer (Y = X)
        float x = Gdx.input.getAccelerometerY();
        // Get value from accelerometer (Z = Y)
        float y = Gdx.input.getAccelerometerZ();

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

        owner.move(currentValue.x, currentValue.y, delta);
    }

    @Override
    public String getDebugTag() {
        return ControllerManager.class.getSimpleName();
    }
}