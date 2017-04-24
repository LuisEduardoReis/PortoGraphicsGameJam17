package pt.kr_eddie.pggj17.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import pt.kr_eddie.pggj17.Assets;
import pt.kr_eddie.pggj17.Main;
import pt.kr_eddie.pggj17.Main.GameKey;

public class SettingsScreen extends ScreenAdapter {
	
	Main game;
	
	Stage stage;
	Texture button_tex;
	Main.GameKey curr_key;
	HashMap<Main.GameKey,TextButton> buttons;
	Skin skin;
	
	public SettingsScreen(Main game) {this.game = game;}
	
	
	@Override
	public void show() {
		stage = new Stage(new ScreenViewport());
		skin = new Skin();
		
		Gdx.input.setInputProcessor(stage);
		
		curr_key = null;
		buttons = new HashMap<Main.GameKey, TextButton>();
		
		Pixmap pixmap = new Pixmap(100, 30, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));
		
		skin.add("default", Assets.font);
		
		TextButtonStyle bstyle = new TextButtonStyle();
		bstyle.up = skin.newDrawable("white", Color.DARK_GRAY);
		bstyle.down = skin.newDrawable("white", Color.DARK_GRAY);
		bstyle.checked = skin.newDrawable("white", Color.WHITE);
		bstyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
		bstyle.font = skin.getFont("default");
		skin.add("default", bstyle);
		
		LabelStyle lstyle = new LabelStyle();
		lstyle.font = skin.getFont("default");
		skin.add("default", lstyle);
		
		Table table = new Table();
		
		final SettingsScreen t = this;
		for(final GameKey key : GameKey.values()) {
			Label label = new Label(key.name() + ":", skin);
			
			TextButton button = new TextButton(Keys.toString(Main.keybindings.get(key)), bstyle);
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					if (t.curr_key == null) t.curr_key = key;
				}
			});
			buttons.put(key, button);
			
			table.add(label).padRight(10f);
			table.add(button).padBottom(10f);
			table.row();
		}
		table.add(new Label("Press Escape to go back to Main menu", skin)).colspan(2).padTop(15f).padBottom(20f);
		table.row();
				
		table.setFillParent(true);
		stage.addActor(table);
		
		stage.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (curr_key != null) {					
					TextButton button = t.buttons.get(curr_key);
					button.setChecked(false);
					
					if (keycode != Keys.ESCAPE) {
						button.setText(Keys.toString(keycode));					
						Main.keybindings.put(curr_key, keycode);
					}
					
					curr_key = null;
				}
				return false;
			}
		});
	}

	@Override
	public void render(float delta){
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			game.setScreen(new GameScreen(game));
			return;
		}
		
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);		
	}
}