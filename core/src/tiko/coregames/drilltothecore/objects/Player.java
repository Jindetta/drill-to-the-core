package tiko.coregames.drilltothecore.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends BaseObject {
    public Player() {
        super("images/player.png");
    }

    // For debugging purposes
    private void updateMovement(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            translateX(16 * delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            translateX(-16 * delta);
        }
    }

    public void draw(SpriteBatch batch, float delta) {
        updateMovement(delta);
        super.draw(batch);
    }
}