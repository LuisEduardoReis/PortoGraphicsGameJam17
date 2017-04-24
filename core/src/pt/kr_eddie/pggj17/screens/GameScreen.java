package pt.kr_eddie.pggj17.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import pt.kr_eddie.pggj17.Assets;
import pt.kr_eddie.pggj17.FadeEffect;
import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Util;
import pt.kr_eddie.pggj17.entities.Player;
import pt.kr_eddie.pggj17.level.Level;

public class GameScreen extends ScreenAdapter {
	
	Main main;
	
	// Logic
	Level level;
	HashMap<String, Level> levels;
	
	float levelChangeTimer, levelChangeDelay;

	// Render
	public SpriteBatch batch;
	public ShapeRenderer shapeRenderer;
	
	OrthographicCamera camera;
	Viewport viewport;
	
	public String speech;
	public String tooltip;
	
	Texture fillTexture;
	
	FadeEffect fadeIn, fadeOut;
	
	// Sound
	Music music;
	
	
	public GameScreen(Main main) {this.main = main;}
	
	@Override
	public void show() {
		super.show();
		
		// Render
		camera = new OrthographicCamera();
		viewport = new FitViewport(Main.V_WIDTH, Main.V_HEIGHT, camera);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		
		// Logic
		levels = new HashMap<String, Level>();	
		
		levelChangeTimer = -1;
		levelChangeDelay = 1f;
		
		fadeIn = new FadeEffect();
		fadeIn.duration = 1.5f;
		fadeIn.up = false;
		
		fadeOut = new FadeEffect();
		fadeOut.duration = 2f;
		
		music = Assets.musics.get("start");
		
		// Start
		gotoLevel("garden", null);
		fadeIn();
		
	}
	
	private void gotoLevel(String name, String spawn) {
		Player player = null;
				
		
		if (level != null) {
			level.gotoLevel(null, null);
			
			if (level.name.equals(name)) {
				level.gotoSpawn(spawn);
				return;
			}
			
			player = level.player;
			level.entities.remove(player);
		}
			
		if (levels.containsKey(name))
			level = levels.get(name);
		else {
			level = new Level(this, name);
			levels.put(name, level);
		}
		
		if (player == null) player = new Player(level);
		else level.addEntity(player);
		level.player = player;
		player.level = level;
		player.scale = player.targetScale = 1;
			
		music = Assets.musics.get(level.music);
		
		level.gotoLevel(null, null);
		level.gotoSpawn(spawn);
		music.stop();
		music.play();
	}
	
	static GlyphLayout layout = new GlyphLayout();
	
	@Override
	public void render(float delta) {
		super.render(delta);
		
		//Logic
		delta = Math.min(delta, 0.25f);
		
		if (!Main.PAUSE) {
			speech = null;
			tooltip = null;
			
			level.update(delta);
			
			if (level.levelChange && levelChangeTimer < 0) {
				levelChangeTimer = fadeOut.duration;
				fadeOut.start();
			}
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();
		if (Gdx.input.isKeyJustPressed(Keys.D)) Main.DEBUG ^= true;
		if (Gdx.input.isKeyJustPressed(Keys.R)) main.setScreen(new GameScreen(main));
		
		if (levelChangeTimer >= 0) {
			levelChangeTimer = Util.stepTo(levelChangeTimer, 0, delta);
			if (levelChangeTimer == 0) {
				levelChangeTimer = -1;
				gotoLevel(level.targetLevel, level.targetSpawn);
				fadeIn();
			}
		}
		
		fadeIn.update(delta);
		fadeOut.update(delta);
		

		// Render
		Gdx.gl.glClearColor(0,0,0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		int w = Main.V_WIDTH, h = Main.V_HEIGHT;
		viewport.apply();
		
		// Level
			camera.position.set(level.cameraPos.x,level.cameraPos.y,0);
			camera.zoom = level.cameraZoom;
			camera.update();
			
			batch.setProjectionMatrix(camera.combined);
			batch.enableBlending();
	
			level.tileRenderer.setView(camera);
			level.tileRenderer.render();
			
			batch.begin();
				level.render(batch);
			batch.end();
		
			if (Main.DEBUG) {
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.begin(ShapeType.Line);
					level.renderDebug(shapeRenderer);
				shapeRenderer.end();
			}
			
		// HUD
			camera.position.set(w/2,h/2,0);
			camera.zoom = 1;
			camera.update();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
				// Tooltip && Speech
				float tw = w*0.75f;
				Assets.font.getData().setScale(0.75f);
				if (speech != null)	Assets.font.draw(batch, speech, w/2 - tw/2,h-16,tw,1,true);
				if (tooltip != null)	Assets.font.draw(batch, tooltip, w/2 - tw/2,32,tw,1,true);
				
			
				// Fade in/out
				float f = 0;
				f += fadeIn.getValue();
				f += fadeOut.getValue();
				f = Util.constrain(f, 0, 1f);
				music.setVolume(0.5f*Util.constrain(1-f, 0, 1f));
				batch.setColor(0, 0, 0, f);
				batch.draw(fillTexture,0,0);
				batch.setColor(1,1,1,1);			
			batch.end();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if (fillTexture != null) fillTexture.dispose();
		
		Pixmap p = new Pixmap(width, height, Format.RGB888);
		p.setColor(1, 1, 1, 1); p.fill();
		fillTexture = new Texture(p);
		p.dispose();
		
		
		viewport.update(width, height);
	}
	
	public void fadeIn() {		
		fadeIn.reset();
		fadeIn.start();
		
		fadeOut.reset();		
	}
	
	public void fadeOut() {		
		fadeIn.reset();
			
		fadeOut.reset();
		fadeOut.start();
	}
}
