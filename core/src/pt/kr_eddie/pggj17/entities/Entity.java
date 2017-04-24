package pt.kr_eddie.pggj17.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Sprite;
import pt.kr_eddie.pggj17.Util;
import pt.kr_eddie.pggj17.level.Collision;
import pt.kr_eddie.pggj17.level.Level;
import pt.kr_eddie.pggj17.level.Tile;

public class Entity {

	public Level level;
	public float x,y, px,py, hx,hy, vx,vy;
	public float scale, pscale;
	
	public float bounciness;
	public float gravity;
	
	public boolean remove;
	
	public boolean colideWithLevel;
	public boolean applyGravity;
	
	public Sprite sprite;
	public boolean visible;
	public float alpha;
	public int direction;
	public int anim_index;
	
	
	public Entity(Level level) {
		this.level = level;
		level.addEntity(this);
		
		this.x = this.px = 0;
		this.y = this.py = 0;
		this.vx = 0;
		this.vy = 0;
		this.scale = this.pscale = 1;
		this.hx = Main.TILE_SIZE;
		this.hy = Main.TILE_SIZE;
		this.bounciness = 0;
		this.gravity = 15*Main.TILE_SIZE;
		this.remove = false;
		this.colideWithLevel = false;
		this.applyGravity = false;
		
		this.sprite = null;
		this.visible = true;
		this.alpha = 1f;
		this.direction = 1;
		this.anim_index = 0;
	}

	public void preupdate(float delta) {
		px = x; py = y; pscale = scale;
	}
	float t = 0; 
	public void update(float delta) {
		t += delta;
		if (applyGravity) vy = Util.stepTo(vy, -Main.TILE_SIZE*8*scale, gravity*delta);		
	}
	public void postupdate(float delta) {
		x += vx*delta;
		y += vy*delta;
	}

	public static Color color = new Color();
	public void render(SpriteBatch batch) {
		if (!visible) return;
		
		if(sprite != null) {			
			color.set(1,1,1,alpha);
			sprite.render(batch, anim_index, (int) x, (int) y, direction*scale, scale, 0, color);
		}
	}
	
	float tx,bx,ty,by,rtx,rbx,rty,rby;
	public void resolveScalling() {
		if (scale <= pscale) return;
		
		int ts = Main.TILE_SIZE;

		tx = px + hx*pscale/2;
		bx = px - hx*pscale/2;
		ty = py + hy*pscale/2;		
		by = py - hy*pscale/2;
		rtx = (float) (Math.ceil(tx/ts)*ts);
		rbx = (float) (Math.floor(bx/ts)*ts);
		rty = (float) (Math.ceil(ty/ts)*ts);		
		rby = (float) (Math.floor(by/ts)*ts);
		
		
						
		int xx,yy;
		// rty
		for(boolean clear = true; clear && rty < ty+(hy*(scale-pscale+1));) {
			yy = (int) Math.floor(rty/ts);
			for(xx = (int) (Math.floor(bx/ts)); xx <= Math.floor(tx/ts); xx++)
				if (level.getTile(xx,yy).solid) {clear = false; break;}
			if (clear) rty+=ts;
		}
		// rby
		for(boolean clear = true; clear && rby > by-(hy*(scale-pscale+1));) {
			yy = (int) Math.floor(rby/ts)-1;
			for(xx = (int) (Math.floor(bx/ts)); xx <= Math.floor(tx/ts); xx++)
				if (level.getTile(xx,yy).solid) {clear = false; break;}
			if (clear) rby -=ts;
		}		
		
		// rtx
		for(boolean clear = true; clear && rtx < tx+(hx*(scale-pscale));) {
			xx = (int) Math.floor(rtx/ts);
			for(yy = (int) (Math.floor(by/ts)); yy <= Math.floor(ty/ts); yy++)
				if (level.getTile(xx,yy).solid) {clear = false; break;}
			if (clear) rtx+=ts;
		}
		// rbx
		for(boolean clear = true; clear && rbx > bx-(hx*(scale-pscale));) {
			xx = (int) Math.floor(rbx/ts)-1;
			for(yy = (int) (Math.floor(by/ts)); yy <= Math.floor(ty/ts); yy++)
				if (level.getTile(xx,yy).solid) {clear = false; break;}
			if (clear) rbx -=ts;
		}
				
		float tyscale = Math.abs(rty-py)/(hy/2);
		float byscale = Math.abs(rby-py)/(hy/2);
		
		float txscale = Math.abs(rtx-px)/(hx/2);
		float bxscale = Math.abs(rbx-px)/(hx/2);
		
		if (tyscale < scale) { scale = tyscale; y -= 1;	}
		if (byscale < scale) { scale = byscale; y += 1;	}
		if (txscale < scale) { scale = txscale; x -= 1;	}
		if (bxscale < scale) { scale = bxscale; x += 1;	}
		
	}
	
	
	public static Vector2 v1 = new Vector2(), v2 = new Vector2();
	public void checkLevelCollision() {
		if (!colideWithLevel) return;
		
		int ts = Main.TILE_SIZE;
		
		while(true) {
		
			float dx = x - px, dy = y - py;
			int minXi = (int) Math.floor((Math.min(x, px)-(hx*scale)/2)/ts), maxXi = (int)Math.floor((Math.max(x, px)+(hx*scale)/2)/ts);
			int minYi = (int) Math.floor((Math.min(y, py)-(hy*scale)/2)/ts), maxYi = (int)Math.floor((Math.max(y, py)+(hy*scale)/2)/ts);
			
			float r = 1; Vector2 n = v1.set(0, 0);
			//Tile rt = Tile.AIR;
	
			for(int yi = minYi; yi <= maxYi; yi++) {
			for(int xi = minXi; xi <= maxXi; xi++) {
				Tile t = level.getTile(xi, yi);
				if (!t.solid) continue;
				
				float nr = Collision.sweepAABB(
						px-(hx*scale)/2, py-(hy*scale)/2, (hx*scale), (hy*scale), 
						xi*ts, yi*ts, ts, ts, 
						dx, dy, v2);
				if (nr < r) {
					r = nr;
					n.set(v2);
					//rt = t;
				}
			}}
			
			float ep = 0.001f;
			x = px + r*dx + ep*n.x;
			y = py + r*dy + ep*n.y;
			
			if (r == 1) break;
			else {
				//levelCollision(n.x,n.y,rt);
				if (n.x != 0) vx = -bounciness*vx;
				if (n.y != 0) vy = -bounciness*vy;
			}
			
			float BdotB = n.x*n.x + n.y*n.y;
			if (BdotB != 0) {
				px = x;
				py = y;
		
				float AdotB = (1-r)*(dx*n.x+dy*n.y);
				x += (1-r)*dx - (AdotB/BdotB)*n.x;
				y += (1-r)*dy - (AdotB/BdotB)*n.y;
			}
		}

		
	}

	public void renderDebug(ShapeRenderer renderer) {
		renderer.setColor(1,0,0,1);
		renderer.rect((int)(x-(hx*scale)/2),(int)(y-(hy*scale)/2),hx*scale,hy*scale);
		
		/*
		renderer.line(tx,ty,bx,ty);
		renderer.line(tx,by,bx,by);
		
		renderer.setColor(1,0,1,1);
		
		renderer.line(rtx,rty,rbx,rty);
		renderer.line(rtx,rby,rbx,rby);*/
		
	}
	
	public void setPosition(Vector2 v) {setPosition(v.x,v.y);}
	public void setPosition(float x, float y) {this.x = this.px = x; this.y = this.py = y;}

	public void entityCollision(Entity other) {}
}
