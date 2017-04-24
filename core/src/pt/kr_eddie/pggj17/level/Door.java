package pt.kr_eddie.pggj17.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Main.GameKey;

public class Door {

	Level level;
	public float x,y,w,h;
	public boolean active;
	public String name, targetLevel, targetSpawn, key;
	public boolean onTouch;
	
	public static int idCount = 1;
	
	public Door(Level level, RectangleMapObject o) {
		this.level = level;
		
		name = o.getName();
		if (name == null || name.equals("")) name = "door" + (idCount++);
		targetLevel = o.getProperties().containsKey("targetLevel") ? (String) o.getProperties().get("targetLevel") : null;
		targetSpawn = o.getProperties().containsKey("targetSpawn") ? (String) o.getProperties().get("targetSpawn") : null;
		key = o.getProperties().containsKey("key") ? (String) o.getProperties().get("key") : null;
		
		onTouch = o.getProperties().containsKey("onTouch");
		
		x = o.getRectangle().x;
		y = o.getRectangle().y;
		w = o.getRectangle().width;
		h = o.getRectangle().height;	
		
		active = false;
	}
	
	public void inside() {
		if (targetLevel == null && targetSpawn == null) {
			level.game.tooltip = "Fechada";
		} 
		else if (key == null || level.keys.contains(key)) {
			if (onTouch || Gdx.input.isKeyJustPressed(Main.keybindings.get(GameKey.USE))) {
				if (targetLevel == null && targetSpawn != null) level.gotoLevel(level.name, targetSpawn);
				if (targetLevel != null) level.gotoLevel(targetLevel, targetSpawn);
			}	
			else level.game.tooltip = "Carrega \"Seta para Baixo\" para entrar";

		} else {
			level.game.tooltip = "Esta porta precisa da " + key + ".";
		}
		
	}
}
