package pt.kr_eddie.pggj17;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	
	
	public static Texture spritesheet;
	
	public static TextureRegion[][] spritesheet32;
	
	public static Sprite player_side;
	public static Sprite player_front;
	public static Sprite player_air;

	public static Sprite[] potion_sprites;
	public static Sprite[] key_sprites;
	
	public static BitmapFont font;
	
	public static HashMap<String,Music> musics;
	public static Sound pickup;
	public static Sound powerup;
	
	
	public static void loadAssets() {
		spritesheet = new Texture("spritesheet.png");
		spritesheet32 = TextureRegion.split(spritesheet,32,32);
		
		player_side = new Sprite()
				.addFrame(Assets.spritesheet32[0][5])
				//.addFrame(Assets.spritesheet32[0][1])
				.addFrame(Assets.spritesheet32[0][6]);
				//.addFrame(Assets.spritesheet32[0][1]);
		player_front = new Sprite()
				.addFrame(Assets.spritesheet32[0][3]);
		player_air = new Sprite()
				.addFrame(Assets.spritesheet32[0][7])
				.addFrame(Assets.spritesheet32[0][8]);
		
		
		potion_sprites = new Sprite[3];
		for(int i = 0; i < 3; i++)
			potion_sprites[i] = new Sprite().addFrame(Assets.spritesheet32[1][i]);
		
		key_sprites = new Sprite[3];
		for(int i = 0; i < 3; i++)
			key_sprites[i] = new Sprite().addFrame(Assets.spritesheet32[1][i+3]);
		
		
		font = new BitmapFont();
		
		musics = new HashMap<String, Music>();
		
		Music start_music = Gdx.audio.newMusic(Gdx.files.internal("sounds/start.mp3"));
		start_music.setLooping(true);
		musics.put("start", start_music);
		
		Music fall_music = Gdx.audio.newMusic(Gdx.files.internal("sounds/fall.mp3"));
		fall_music.setLooping(true);
		musics.put("fall", fall_music);
		
		Music shopsong_music = Gdx.audio.newMusic(Gdx.files.internal("sounds/shopsong.mp3"));
		shopsong_music.setLooping(true);
		musics.put("shopsong", shopsong_music);
		
		pickup = Gdx.audio.newSound(Gdx.files.internal("sounds/pickup.wav"));
		powerup = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
		
		
	};
}
