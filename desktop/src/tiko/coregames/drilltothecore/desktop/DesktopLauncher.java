package tiko.coregames.drilltothecore.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import tiko.coregames.drilltothecore.Setup;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = 1280;
		config.height = 720;
		config.addIcon("images/icon.png", Files.FileType.Internal);

		new LwjglApplication(new Setup(), config);
	}
}
