package pt.kr_eddie.pggj17.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;

import pt.kr_eddie.pggj17.level.Level;

public class Textbox extends Entity {

	String text;
	
	public Textbox(Level level, RectangleMapObject o) {
		super(level);
		
		hx = o.getRectangle().width;
		hy = o.getRectangle().height;
		setPosition(o.getRectangle().x + hx/2, o.getRectangle().y + hy/2);
		
		text = o.getProperties().containsKey("text") ? (String) o.getProperties().get("text") : null;		
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		test = false;
	}
	
	boolean test;
	@Override
	public void entityCollision(Entity other) {
		super.entityCollision(other);
		
		if (other instanceof Player) {
			level.game.speech = text;
			test = true;
		}
	}
	
	public void renderDebug(ShapeRenderer renderer) {
		if (test) return;
		renderer.setColor(0,1,0,1);
		renderer.rect((int)(x-(hx*scale)/2),(int)(y-(hy*scale)/2),hx*scale,hy*scale);
		
		/*
		renderer.line(tx,ty,bx,ty);
		renderer.line(tx,by,bx,by);
		
		renderer.setColor(1,0,1,1);
		
		renderer.line(rtx,rty,rbx,rty);
		renderer.line(rtx,rby,rbx,rby);*/
		
	}
}
