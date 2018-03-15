package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Disposable;

import static tiko.coregames.drilltothecore.utilities.Utilities.*;

public abstract class BaseObject extends Sprite implements Disposable {
    private boolean alive, visible;

    public BaseObject(String texture) {
        super(new Texture(texture));

        setSize(toWorldUnits(getWidth()), toWorldUnits(getHeight()));
        setOriginCenter();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void dispose() {
        getTexture().dispose();
    }

    abstract public void move(float valueX, float valueY, float delta);
}