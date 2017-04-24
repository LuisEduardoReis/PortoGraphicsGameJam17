package pt.kr_eddie.pggj17;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Input.Keys;

import pt.kr_eddie.pggj17.screens.GameScreen;

public class Main extends Game {
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 3;
	public static final int TILE_SIZE = 32;
	
	
	public static boolean PAUSE = false;
	public static boolean DEBUG = false;
	
	
	public enum GameKey {
		UP,DOWN,LEFT,RIGHT, USE
	}
	
	public static HashMap<GameKey, Integer> keybindings;
	
	static {
		keybindings = new HashMap<GameKey, Integer>();
		keybindings.put(GameKey.UP, Keys.UP);
		keybindings.put(GameKey.DOWN, Keys.DOWN);
		keybindings.put(GameKey.LEFT, Keys.LEFT);
		keybindings.put(GameKey.RIGHT, Keys.RIGHT);
		
		keybindings.put(GameKey.USE, Keys.DOWN);
	}
	
	@Override
	public void create () {
		Assets.loadAssets();
		
		setScreen(new GameScreen(this));		
	}

}
