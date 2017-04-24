package pt.kr_eddie.pggj17.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import pt.kr_eddie.pggj17.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Main.V_WIDTH * Main.SCALE;
		config.height = Main.V_HEIGHT * Main.SCALE;
		//config.resizable = false;
		
		new LwjglApplication(new Main(), config);
	}
}
