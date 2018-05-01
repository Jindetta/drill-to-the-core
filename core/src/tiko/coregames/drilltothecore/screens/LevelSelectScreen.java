package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;
import tiko.coregames.drilltothecore.managers.SoundManager;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

public class LevelSelectScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Image background;
    private Table gameMenu;

    public LevelSelectScreen() {
        backgroundTexture = new Texture("images/endscreen-background.png");

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "play":
                        Setup.nextScreen(new GameScreen());
                        break;
                    default:
                        Setup.nextScreen(new MainMenuScreen());
                        break;
                }
            }
        };

        ImageButton playButton = new ImageButton(skin, coreLocalization.getValue("play"));
        playButton.addListener(clickListener);
        playButton.setName("play");

        ImageButton exit = new ImageButton(skin, coreLocalization.getValue("return_small"));
        exit.addListener(clickListener);

        background = new Image(backgroundTexture);
        addActor(background);

        gameMenu = new Table();
        gameMenu.add(playButton).row();
        gameMenu.add(exit).padTop(MENU_PADDING_TOP);

        addActor(gameMenu);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        float centerX = (viewport.getWorldWidth() - gameMenu.getWidth()) / 2;
        float centerY = (viewport.getWorldHeight() - gameMenu.getHeight()) / 2;

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        gameMenu.setPosition(centerX, centerY);
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        super.dispose();
    }
}
