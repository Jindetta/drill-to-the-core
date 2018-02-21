package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public abstract class BaseObject extends Sprite implements Disposable {
    public BaseObject(String texture) {
        super(new Texture(texture));

        setSize(toWorldUnits(getWidth()), toWorldUnits(getHeight()));
        setOriginCenter();
    }

    @Override
    public void dispose() {
        getTexture().dispose();
    }
}