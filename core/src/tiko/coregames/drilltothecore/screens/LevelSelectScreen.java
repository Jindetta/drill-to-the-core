package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import tiko.coregames.drilltothecore.Setup;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * LevelSelectScreen class will display level selection screen.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class LevelSelectScreen extends BaseScreen {
    private Texture backgroundTexture;
    private Image background;

    private Table screenLayout;

    public LevelSelectScreen() {
        backgroundTexture = new Texture("images/endscreen-background.png");

        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String name = event.getListenerActor().getName();

                switch (name == null ? "" : name) {
                    case "1":
                        Setup.nextScreen(new GameScreen(0, false));
                        break;
                    case "2":
                        Setup.nextScreen(new GameScreen(1, false));
                        break;
                    case "3":
                        Setup.nextScreen(new GameScreen(2, false));
                        break;
                    case "4":
                        Setup.nextScreen(new GameScreen(3, false));
                        break;
                    case "5":
                        Setup.nextScreen(new GameScreen(4, false));
                        break;
                    case "play":
                        Setup.nextScreen(new GameScreen());
                        break;
                    case "continue":
                        Setup.nextScreen(new GameScreen(0, true));
                        break;
                    default:
                        Setup.nextScreen(new MainMenuScreen());
                        break;
                }
            }
        };

        background = new Image(backgroundTexture);
        addActor(background);

        screenLayout = new Table();
        screenLayout.defaults().expand().uniform();

        Table levelSelection = new Table();

        for (int i = 1; i <= LEVEL_COUNT; i++) {
            ImageButton level = new ImageButton(skin, "number_" + i);
            level.addListener(clickListener);
            level.setName(String.valueOf(i));

            levelSelection.add(level).pad(MENU_DEFAULT_PADDING);
        }

        screenLayout.add(new ImageButton(skin, localization.getValue("levelSelectTitle"))).row();

        if (settings.hasValue("currentLevel")) {
            ImageButton continueButton = new ImageButton(skin, localization.getValue("continueGame"));
            continueButton.addListener(clickListener);
            continueButton.setName("continue");

            screenLayout.add(continueButton).row();
        } else {
            ImageButton playButton = new ImageButton(skin, localization.getValue("playMenu"));
            playButton.addListener(clickListener);
            playButton.setName("play");

            screenLayout.add(playButton).row();
        }

        screenLayout.add(levelSelection).row();

        ImageButton backButton = new ImageButton(skin, localization.getValue("backButton"));
        backButton.addListener(clickListener);

        screenLayout.add(backButton).align(Align.bottomLeft).pad(MENU_DEFAULT_PADDING);

        addActor(screenLayout);
    }

    @Override
    public void resize(int width, int height) {
        Viewport viewport = getViewport();
        viewport.update(width, height, true);

        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        screenLayout.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        screenLayout.setPosition(0, 0);
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        super.dispose();
    }
}
