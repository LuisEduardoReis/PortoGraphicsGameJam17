package pt.kr_eddie.pggj17.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import pt.kr_eddie.pggj17.Assets;
import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Main.GameKey;
import pt.kr_eddie.pggj17.Util;
import pt.kr_eddie.pggj17.level.Level;

public class Player extends Entity {

	public static final float BIG_SCALE = 16;
	public static final float MEDIUM_SCALE = 4;
	public static final float SMALL_SCALE = 1;	
	
	public float targetScale;
	
	public Player(Level level) {
		super(level);
		
		this.colideWithLevel = true;
		this.applyGravity = true;
		
		this.hx = Main.TILE_SIZE*0.65f;
		this.hy = Main.TILE_SIZE*1f;
	}
	
	
	@Override
	public void update(float delta) {
		super.update(delta);
		
		// Logic		
		if(Gdx.input.isKeyPressed(Keys.Z)) targetScale += 2f*delta;
		if(Gdx.input.isKeyPressed(Keys.X)) targetScale -= 5f*delta;
		targetScale = Util.constrain(targetScale,1,100);		
		
		scale = Util.stepTo(scale, targetScale, 2.5f*delta);
		
		boolean tileUnderfoot = false;
		for(int xx = (int) Math.floor((x-hx*scale/2)/Main.TILE_SIZE); xx <= (int) Math.floor((x+hx*scale/2)/Main.TILE_SIZE) ; xx++)
			if (level.getTile(xx,(int) Math.floor((y-hy*scale/2-1)/Main.TILE_SIZE)).solid) {
				tileUnderfoot = true; break;
			};
		
		if (tileUnderfoot && Gdx.input.isKeyJustPressed(Main.keybindings.get(GameKey.UP))) vy = (float) (8*Main.TILE_SIZE*Math.sqrt(scale));
		//if (Gdx.input.isKeyPressed(Main.keybindings.get(GameKey.UP))) y += s;
		//if (Gdx.input.isKeyPressed(Main.keybindings.get(GameKey.DOWN))) y -= s;
		
		boolean front = true;
		float s = (float) (5*Main.TILE_SIZE*delta*Math.sqrt(scale));
		if (Gdx.input.isKeyPressed(Main.keybindings.get(GameKey.LEFT))) {
			x -= s;
			direction = -1;
			front = false;
		}
		if (Gdx.input.isKeyPressed(Main.keybindings.get(GameKey.RIGHT))) {
			x += s;
			direction = 1;
			front = false;
		}
		
		
		// Render
		if (!tileUnderfoot) sprite = Assets.player_air;
		else if (front) sprite = Assets.player_front;
		else sprite = Assets.player_side;
		
		anim_index = (int) Math.floor(3*t);
		anim_index %= sprite.frames.size();
	}

	@Override
	public void postupdate(float delta) {
		super.postupdate(delta);
	}
	
	@Override
	public void render(SpriteBatch batch) {		
		super.render(batch);
	}
	
}
