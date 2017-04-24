package pt.kr_eddie.pggj17.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

import pt.kr_eddie.pggj17.Assets;
import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Main.GameKey;
import pt.kr_eddie.pggj17.Util;
import pt.kr_eddie.pggj17.level.Level;

public class Potion extends Entity {
	
	public float value;
	public float player_scale;
	public String name;
	public boolean respawn;

	public Potion(Level level, TiledMapTileMapObject o, int i) {
		super(level);
		
		this.sprite = Assets.potion_sprites[i];
		
		this.scale = o.getScaleX();
		
		setPosition(o.getX() + hx*scale/2, o.getY() + hy*scale/2);
		
		this.value = 0;
		this.respawn = o.getProperties().containsKey("respawn");
		
		switch(i) {
		case 0: 
		default: 
			this.player_scale = Player.BIG_SCALE; 
			this.name = "poção verde";
			break;
		case 1:
			this.player_scale = Player.MEDIUM_SCALE; 
			this.name = "poção rosa";
			break;
		case 2:
			this.player_scale = Player.SMALL_SCALE; 
			this.name = "poção azul";
			break;
		}		
	}

	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		if (respawn) value = Util.stepTo(value, 0, delta);
		
		alpha = Util.constrain(1f - value, 0, 1f);
	}
	
	
	@Override
	public void entityCollision(Entity o) {
		super.entityCollision(o);
		
		if (o instanceof Player) {
			Player p = (Player) o;
			if (value == 0 && p.targetScale != player_scale) {
				if (Gdx.input.isKeyJustPressed(Main.keybindings.get(GameKey.USE))) {
					p.targetScale = player_scale;
					Assets.powerup.play();
					value = 10;
				}
				level.game.tooltip = "Carrega \"X\" para beber a " + name;
			}
		}
	}
}
