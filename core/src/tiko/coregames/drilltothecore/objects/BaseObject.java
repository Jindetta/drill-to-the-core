package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class BaseObject extends Sprite implements Disposable {
    private boolean alive, visible;

    BaseObject(String texture) {
        super(new Texture(texture));

        setOriginCenter();
        setVisible(true);
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

    public void draw(SpriteBatch batch, float delta) {
        if (isVisible()) {
            super.draw(batch);
        }
    }

    @Override
    public void dispose() {
        getTexture().dispose();
    }
}