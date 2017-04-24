package pt.kr_eddie.pggj17.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;

import pt.kr_eddie.pggj17.Assets;
import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Main.GameKey;
import pt.kr_eddie.pggj17.level.Level;

public class Key extends Entity {


	public String name;

	public Key(Level level, TiledMapTileMapObject o) {
		super(level);
		
		int i = o.getProperties().containsKey("i") ? Integer.parseInt((String) o.getProperties().get("i")) : 0;
		switch(i) {
		case 0:
		default:
			this.sprite = Assets.key_sprites[i];
			this.name = "chave vermelha";
		}
		
		this.scale = o.getScaleX();
		
		setPosition(o.getX() + hx*scale/2, o.getY() + hy*scale/2);
	}
	
	
	@Override
	public void entityCollision(Entity other) {
		super.entityCollision(other);
		
		if (other instanceof Player) {
			if (Gdx.input.isKeyJustPressed(Main.keybindings.get(GameKey.USE))) {
				level.keys.add(name);
				Assets.pickup.play();
				remove = true;
			}
			level.game.tooltip = "Carrega \"Seta para Baixo\" para apanhar a " + name;
		}

	}
}
