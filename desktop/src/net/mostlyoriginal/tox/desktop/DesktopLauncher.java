package net.mostlyoriginal.tox.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.mostlyoriginal.tox.ToxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tox";
        config.width = 480;
        config.height = 800;
        config.resizable=false;
        config.foregroundFPS = 30;
        config.addIcon("icon128x128.png", Files.FileType.Internal);
        config.addIcon("icon32x32.png", Files.FileType.Internal);
        config.addIcon("icon16x16.png", Files.FileType.Internal);
		new LwjglApplication(new ToxGame(), config);
	}
}
