package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tiko.coregames.drilltothecore.CoreSetup;

public class MainMenuScreen extends BaseScreen {
    private BitmapFont font;
    private GlyphLayout layout;

    public MainMenuScreen() {
        super(new ScreenViewport());

        font = new BitmapFont();
        layout = new GlyphLayout(font, "Start the game by pressing any key or touching the screen");
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        SpriteBatch batch = CoreSetup.getBatch();

        batch.begin();
        font.draw(batch, layout, (Gdx.graphics.getWidth() - layout.width) / 2, (Gdx.graphics.getHeight() - layout.height) / 2);
        batch.end();

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            CoreSetup.nextScreen(new GameScreen());
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        font.dispose();
    }
}