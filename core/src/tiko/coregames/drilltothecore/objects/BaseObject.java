package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * BaseObject class will act as base of all game objects.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public abstract class BaseObject extends Sprite {
    private boolean visible;

    BaseObject(String file, AssetManager assets) {
        super(loadTextureAsset(file, assets));

        setOriginCenter();
        setVisible(true);
    }

    private static Texture loadTextureAsset(String file, AssetManager assets) {
        assets.load(file, Texture.class);
        assets.finishLoadingAsset(file);

        return assets.get(file, Texture.class);
    }

    /**
     * Gets object visibility.
     *
     * @return true if object is visible, otherwise false
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets object visibility.
     *
     * @param visible state
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Draws game object.
     *
     * @param batch     batch to draw to
     * @param delta     delta time
     */
    public void draw(SpriteBatch batch, float delta) {
        if (isVisible()) {
            super.draw(batch);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}