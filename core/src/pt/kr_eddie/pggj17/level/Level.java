package pt.kr_eddie.pggj17.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import pt.kr_eddie.pggj17.Util;
import pt.kr_eddie.pggj17.entities.Entity;
import pt.kr_eddie.pggj17.entities.Key;
import pt.kr_eddie.pggj17.entities.Player;
import pt.kr_eddie.pggj17.entities.Potion;
import pt.kr_eddie.pggj17.entities.Textbox;
import pt.kr_eddie.pggj17.screens.GameScreen;

public class Level {
	public GameScreen game;
	public String name;
	
	public Player player;
	public ArrayList<Entity> entities;

	ArrayList<Entity> newEntities;
	
	public Vector2 cameraPos;
	public float cameraZoom;
	
	HashMap<String, Vector2> spawns;
	ArrayList<Door> doors;
	public HashSet<String> keys;
	
	public boolean levelChange;
	public String targetLevel, targetSpawn;
	
	
	TiledMap map;
	public OrthogonalTiledMapRenderer tileRenderer;
	int map_width, map_height;
	Tile[] tiles;

	public String music;
	
	public Level(GameScreen game, String name) {
		this.game = game;
		this.name = name;
		
		
		entities = new ArrayList<Entity>();
		newEntities = new ArrayList<Entity>();
		
		cameraPos = new Vector2();
		cameraZoom = 1;
		
		spawns = new HashMap<String, Vector2>();
		doors = new ArrayList<Door>();
		keys = new HashSet<String>();
		
		levelChange = false;
		targetLevel = null; targetSpawn = null;
		
		map = new TmxMapLoader(new InternalFileHandleResolver()).load("levels/"+name+".tmx"); 
		tileRenderer = new OrthogonalTiledMapRenderer(map, game.batch);
		
		map_width = (Integer) map.getProperties().get("width");
		map_height = (Integer) map.getProperties().get("height");
		
		music = map.getProperties().containsKey("music") ? (String) map.getProperties().get("music") : "start";
		
		
		// Tiles
		tiles = new Tile[map_width*map_height];
		
		for(int i = 0; i < tiles.length; i++) tiles[i] = Tile.AIR;
		
		TiledMapTileLayer tiled_tiles = (TiledMapTileLayer) map.getLayers().get("main"); 
		for(int yy = 0; yy<map_height; yy++) {
			for(int xx = 0; xx<map_width; xx++) {
				TiledMapTileLayer.Cell cell = tiled_tiles.getCell(xx, yy);
				if (cell != null) {
					int cid = cell.getTile().getId()-1;
					tiles[yy*map_width+xx] = (cid >= 0 && cid < Tile.TILESET.length) ? Tile.TILESET[cid] : Tile.AIR;
				}
			}
		}
		
		// Map objects
		MapObjects objects = map.getLayers().get("objects").getObjects();
		if (objects != null) {
		for(MapObject o : objects) {
			String type = (String) o.getProperties().get("type");
			if (type == null) continue;
			Vector2 p = Util.getMapObjectPosition(o);
			//MapProperties prop = o.getProperties();
			
			// Spawns
			if (type.equals("spawn")) {
				spawns.put(o.getName(), p);
			}
			
			// Doors
			if (type.equals("door")) {
				doors.add(new Door(this, (RectangleMapObject) o));
			}
			
			// Potions
			if (type.equals("big-potion")) new Potion(this, (TiledMapTileMapObject) o, 0);
			if (type.equals("medium-potion")) new Potion(this, (TiledMapTileMapObject) o, 1);
			if (type.equals("small-potion")) new Potion(this, (TiledMapTileMapObject) o, 2);
			
			// Keys
			if (type.equals("key")) new Key(this, (TiledMapTileMapObject) o);
			
			
			// Textbox
			if (type.equals("textbox")) new Textbox(this, (RectangleMapObject) o);
		}}
		if (!spawns.containsKey("default")) {
			if (spawns.isEmpty()) spawns.put("default", new Vector2(0,0));
			else spawns.put("default",spawns.values().iterator().next());
		}
		
	}
	
	public void update(float delta) {
		
		// preupdate
		for(Entity e : entities) e.preupdate(delta);
		
		// update
		for(Entity e : entities) e.update(delta);
		
		// resolve scalling
		for(Entity e : entities) e.resolveScalling();
		
		// doors and triggers
		if (player != null) for(Door d : doors) {
			if (Collision.aabbInsideaabb(
					player.x-player.hx*player.scale/2, player.y-player.hy*player.scale/2, player.hx*player.scale, player.hy*player.scale, 
					d.x,d.y,d.w,d.h)) {
				d.inside();
				d.active = true;
			} else {
				d.active = false;
			}				
		}
		
		// entity collisions
		for(Entity e : entities) { for(Entity o : entities) {
			if (e == o) continue;
			if (Collision.aabbToaabb(
					e.x-e.hx*e.scale/2, e.y-e.hy*e.scale/2, e.hx*e.scale, e.hy*e.scale, 
					o.x-o.hx*o.scale/2, o.y-o.hy*o.scale/2, o.hx*o.scale, o.hy*o.scale)) {
				e.entityCollision(o);
			}
		}}
		
		// postupdate
		for(Entity e : entities) e.postupdate(delta);
		
		// add new entities
		entities.addAll(newEntities);
		newEntities.clear();
		
		// remove old entities
		for(int i = 0; i < entities.size(); i++) 
			if (entities.get(i).remove)
				entities.remove(i);
		
		// level collisions
		for(Entity e : entities) e.checkLevelCollision();
		
		// camera position
		cameraPos.set((int) player.x,(int) player.y);
		cameraZoom = (float) Math.pow(player.scale,0.6f);
	}
	
	public void render(SpriteBatch batch) {
		
		for(Entity e : entities) e.render(batch);
		
	}
	
	public void renderDebug(ShapeRenderer shapeRenderer) {
		for(Entity e : entities) e.renderDebug(shapeRenderer);
		
		shapeRenderer.setColor(Color.BLUE);
		for(Door d : doors) {
			if (d.active) shapeRenderer.rect(d.x,d.y,d.w,d.h);
		}
		
	}

	public void addEntity(Entity entity) {	newEntities.add(entity); }

	public Tile getTile(int x, int y) {
		return (x < 0 || y < 0 || x >= map_width || y >= map_height) ? Tile.AIR : tiles[y*map_width+x];
	}
	public void setTile(int x, int y, Tile tile) {
		if (x >= 0 && y >= 0 && x < map_width && y < map_height) tiles[y*map_width+x] = tile;	
	}

	public void gotoLevel(String tl, String ts) {
		if (tl == null) {
			levelChange = false;
			targetLevel = null;	targetSpawn = null;
		} else {
			levelChange = true;
			targetLevel = tl; targetSpawn = ts;
		}		
	}

	public void gotoSpawn(String spawn) {
		if (player == null) return;
		if (!spawns.containsKey(spawn)) {
			gotoSpawn("default"); 
			return;
		}
		
		player.setPosition(spawns.get(spawn));		
	}
}
