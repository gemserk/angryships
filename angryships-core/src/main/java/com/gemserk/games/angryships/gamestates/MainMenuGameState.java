package com.gemserk.games.angryships.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.games.angryships.Game;
import com.gemserk.games.angryships.preferences.GamePreferences;
import com.gemserk.resources.ResourceManager;

public class MainMenuGameState extends GameStateImpl {

	private final Game game;

	private ResourceManager<String> resourceManager;
	private GamePreferences gamePreferences;

	private Container guiContainer;
	private SpriteBatch spriteBatch;

	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public void setGamePreferences(GamePreferences gamePreferences) {
		this.gamePreferences = gamePreferences;
	}
	
	public MainMenuGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		guiContainer = new Container();
		
		BitmapFont titleFont = new BitmapFont();
		
		guiContainer.add(GuiControls.label("Mad JetPACK") //
				.position(width * 0.5f, height * 0.95f) //
				.center(0.5f, 0.5f) //
				.font(titleFont) //
				.color(1f, 0f, 0f, 1f) //
				.build());

	}
	
	@Override
	public void render() {
		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		guiContainer.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
		guiContainer.update();
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}
}
