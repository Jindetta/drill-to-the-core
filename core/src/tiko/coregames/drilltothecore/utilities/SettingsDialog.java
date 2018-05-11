package tiko.coregames.drilltothecore.utilities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import tiko.coregames.drilltothecore.managers.LocalizationManager;
import tiko.coregames.drilltothecore.managers.SettingsManager;
import tiko.coregames.drilltothecore.managers.SoundManager;

import static tiko.coregames.drilltothecore.utilities.Constants.*;

/**
 * SettingsDialog class is containing all in-game settings in one dialog.
 *
 * @author  Joonas Lauhala <joonas.lauhala@cs.tamk.fi>
 *          Saku Karvinen <saku.karvinen@cs.tamk.fi>
 * @version 1.0
 * @since   2018-02-01
 */
public class SettingsDialog extends Table {
    /**
     * Overloads default constructor.
     *
     * @param sounds        SoundManager instance
     * @param settings      SettingsManager instance
     * @param localization  LocalizationManager instance
     * @param skin          Skin instance
     */
    public SettingsDialog(final SoundManager sounds, final SettingsManager settings, final LocalizationManager localization, Skin skin) {
        super(skin);

        Table volumeControls = new Table();
        volumeControls.columnDefaults(2);

        final Label soundVolumeLabel = new Label("", skin, "clear");
        final Slider soundVolume = new Slider(0, 100, 5, false, skin);
        soundVolume.setValue(settings.getIntegerIfExists("soundVolume", 1));
        soundVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundVolumeLabel.setText(String.format(localization.getValue("soundVolumeTitle"), soundVolume.getValue()));
                settings.setIntegerValue("soundVolume", Math.round(soundVolume.getValue()));
                settings.saveSettings();

                sounds.loadSoundSettings(settings);
                sounds.update();
            }
        });
        soundVolume.fire(new ChangeListener.ChangeEvent());

        final Label musicVolumeLabel = new Label("", skin, "clear");
        final Slider musicVolume = new Slider(0, 100, 5, false, skin);
        musicVolume.setValue(settings.getIntegerIfExists("musicVolume", 1));
        musicVolume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                musicVolumeLabel.setText(String.format(localization.getValue("musicVolumeTitle"), musicVolume.getValue()));
                settings.setIntegerValue("musicVolume", Math.round(musicVolume.getValue()));
                settings.saveSettings();

                sounds.loadSoundSettings(settings);
                sounds.update();
            }
        });
        musicVolume.fire(new ChangeListener.ChangeEvent());

        final CheckBox muteSounds = new CheckBox("", skin, "checkbox_sound2");
        muteSounds.setChecked(settings.getBooleanIfExists("soundMuted", false));
        muteSounds.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("soundMuted", muteSounds.isChecked());
                settings.saveSettings();

                sounds.loadSoundSettings(settings);
                sounds.update();
            }
        });

        final CheckBox muteMusic = new CheckBox("", skin, "checkbox_music2");
        muteMusic.setChecked(settings.getBooleanIfExists("musicMuted", false));
        muteMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                settings.setBooleanValue("musicMuted", muteMusic.isChecked());
                settings.saveSettings();

                sounds.loadSoundSettings(settings);
                sounds.update();
            }
        });

        volumeControls.add(soundVolumeLabel).pad(MENU_DEFAULT_PADDING).colspan(2).row();
        volumeControls.add(soundVolume).pad(MENU_DEFAULT_PADDING).right();
        volumeControls.add(muteSounds).pad(MENU_DEFAULT_PADDING).left().row();
        volumeControls.add(musicVolumeLabel).pad(MENU_DEFAULT_PADDING).colspan(2).row();
        volumeControls.add(musicVolume).pad(MENU_DEFAULT_PADDING).right();
        volumeControls.add(muteMusic).pad(MENU_DEFAULT_PADDING).left();

        final Label sensitivityLabelLeft = new Label("", skin, "clear");
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

        final Label sensitivityLabelRight = new Label("", skin, "clear");
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

        final Label sensitivityLabelUp = new Label("", skin, "clear");
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

        final Label sensitivityLabelDown = new Label("", skin, "clear");
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

        add(new ImageButton(skin, localization.getValue("settingsTitle"))).row();
        add(volumeControls).row();
        add(controlButtons).row();

        ImageButton backButton = new ImageButton(skin, localization.getValue("backButton"));
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisible(false);
            }
        });

        add(backButton).expand().align(Align.bottomLeft).pad(MENU_DEFAULT_PADDING);

        setBackground("LevelCompleteScreen");
        setVisible(false);
    }
}
