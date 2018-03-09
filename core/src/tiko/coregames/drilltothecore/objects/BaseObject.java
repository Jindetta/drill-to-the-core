package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;
import tiko.coregames.drilltothecore.utilities.Debugger;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public abstract class BaseObject extends Sprite implements Disposable, Debugger {
    private boolean alive, visible;

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

    public static String getDebugTag() {
        return BaseObject.class.getSimpleName();
    }
}