package tiko.coregames.drilltothecore.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import tiko.coregames.drilltothecore.Setup;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * CalibrationScreen class will display calibration settings.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class CalibrationScreen extends BaseScreen {
    /**
     * Defines background image.
     */
    private Image background;

    /**
     * Defines screen layout.
     */
    private Table screenLayout;

    public CalibrationScreen() {
        assets.load("images/settings-background.png", Texture.class);
        assets.finishLoadingAsset("images/settings-background.png");

        background = new Image(assets.get("images/settings-background.png", Texture.class));
        addActor(background);

        final Label sensitivityLabelLeft = new Label("", skin);
        final Slider sensitivityLeft = new Slider(1, 10, 1, false, skin);
        sensitivityLeft.setValue(settings.getIntegerIfExists("sensitivityLeft", 1));
        sensitivityLeft.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelLeft.setText(localization.getFormatted("sensitivityLeft", sensitivityLeft.getValue()));
                settings.setIntegerValue("sensitivityLeft", Math.round(sensitivityLeft.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityLeft.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelRight = new Label("", skin);
        final Slider sensitivityRight = new Slider(1, 10, 1, false, skin);
        sensitivityRight.setValue(settings.getIntegerIfExists("sensitivityRight", 1));
        sensitivityRight.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelRight.setText(localization.getFormatted("sensitivityRight", sensitivityRight.getValue()));
                settings.setIntegerValue("sensitivityRight", Math.round(sensitivityRight.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityRight.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelUp = new Label("", skin);
        final Slider sensitivityUp = new Slider(1, 10, 1, false, skin);
        sensitivityUp.setValue(settings.getIntegerIfExists("sensitivityUp", 1));
        sensitivityUp.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelUp.setText(localization.getFormatted("sensitivityUp", sensitivityUp.getValue()));
                settings.setIntegerValue("sensitivityUp", Math.round(sensitivityUp.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityUp.fire(new ChangeListener.ChangeEvent());

        final Label sensitivityLabelDown = new Label("", skin);
        final Slider sensitivityDown = new Slider(1, 10, 1, false, skin);
        sensitivityDown.setValue(settings.getIntegerIfExists("sensitivityDown", 1));
        sensitivityDown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sensitivityLabelDown.setText(localization.getFormatted("sensitivityDown", sensitivityDown.getValue()));
                settings.setIntegerValue("sensitivityDown", Math.round(sensitivityDown.getValue()));
                settings.saveSettings();
            }
        });
        sensitivityDown.fire(new ChangeListener.ChangeEvent());

        final CheckBox invertedX = new CheckBox(localization.getValue("invertX"), skin);
        invertedX.setChecked(settings.getBooleanIfExists("invertedX", false));
        invertedX.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedX", invertedX.isChecked());
                settings.saveSettings();
            }
        });

        final CheckBox invertedY = new CheckBox(localization.getValue("invertY"), skin);
        invertedY.setChecked(settings.getBooleanIfExists("invertedY", false));
        invertedY.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setBooleanValue("invertedY", invertedY.isChecked());
                settings.saveSettings();
            }
        });

        screenLayout = new Table();
        screenLayout.defaults().expand();

        Table controlButtons = new Table();
        controlButtons.defaults().expandX().uniform();

        controlButtons.add(sensitivityLabelLeft);
        controlButtons.add(sensitivityLabelRight).row();
        controlButtons.add(sensitivityLeft).padTop(MENU_DEFAULT_PADDING);
        controlButtons.add(sensitivityRight).padTop(MENU_DEFAULT_PADDING).row();

        controlButtons.add(sensitivityLabelUp);
        controlButtons.add(sensitivityLabelDown).row();
        controlButtons.add(sensitivityUp).padTop(MENU_DEFAULT_PADDING);
        controlButtons.add(sensitivityDown).padTop(MENU_DEFAULT_PADDING).row();

        controlButtons.add(invertedX);
        controlButtons.add(invertedY);

        ImageButton titleButton = new ImageButton(skin, localization.getValue("calibrateTitle"));

        ImageButton backButton = new ImageButton(skin, localization.getValue("backButton"));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CalibrationScreen.this.keyDown(Input.Keys.BACK);
            }
        });

        screenLayout.add(titleButton).row();
        screenLayout.add(controlButtons).row();
        screenLayout.add(backButton).align(Align.bottomLeft).pad(SAFE_ZONE_SIZE);

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
    public boolean keyDown(int key) {
        if (key == Input.Keys.ESCAPE || key == Input.Keys.BACK) {
            Setup.nextScreen(new SettingsScreen());
        }

        return super.keyDown(key);
    }
}