package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public abstract class BaseObject extends Sprite implements Disposable {
    /**
     * Defines debug tag for this class.
     */
    private static final String DEBUG_TAG = BaseObject.class.getName();

    public BaseObject(String texture) {
        super(new Texture(texture));

        setSize(toWorldUnits(getWidth()), toWorldUnits(getHeight()));
        setOriginCenter();
    }

    @Override
    public void dispose() {
        getTexture().dispose();
    }

    abstract public void move(float valueX, float valueY, float delta);
}