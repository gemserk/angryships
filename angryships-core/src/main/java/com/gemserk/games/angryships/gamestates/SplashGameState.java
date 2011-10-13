package com.gemserk.games.angryships.gamestates;

import java.util.ArrayList;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.games.angryships.CustomResourceManager;
import com.gemserk.games.angryships.Game;
import com.gemserk.resources.Resource;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.progress.TaskQueue;
import com.gemserk.resources.progress.tasks.SimulateLoadingTimeRunnable;

public class SplashGameState extends com.gemserk.commons.gdx.gamestates.LoadingGameState {

	private final Game game;

	private SpriteBatch spriteBatch;
	private BitmapFont font;

	private ResourceManager<String> resourceManager;

	private Sprite gemserkLogo;
	private Sprite lwjglLogo;
	private Sprite libgdxLogo;
	private Sprite gemserkLogoBlur;

	private Color blurColor = new Color();
	
	public void setResourceManager(ResourceManager<String> resourceManager) {
		this.resourceManager = resourceManager;
	}

	public SplashGameState(Game game) {
		this.game = game;
	}

	@Override
	public void init() {
		super.init();
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		int centerX = width / 2;
		int centerY = height / 2;

		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		font.setColor(1f, 1f, 0f, 1f);

		gemserkLogo = resourceManager.getResourceValue("GemserkLogo");
		gemserkLogoBlur = resourceManager.getResourceValue("GemserkLogoBlur");
		lwjglLogo = resourceManager.getResourceValue("LwjglLogo");
		libgdxLogo = resourceManager.getResourceValue("LibgdxLogo");

		SpriteUtils.resize(gemserkLogo, width * 0.8f);
		SpriteUtils.resize(gemserkLogoBlur, width * 0.8f);
		SpriteUtils.resize(lwjglLogo, width * 0.2f);
		SpriteUtils.resize(libgdxLogo, width * 0.2f);

		SpriteUtils.centerOn(gemserkLogo, centerX, centerY);
		SpriteUtils.centerOn(gemserkLogoBlur, centerX, centerY);
		SpriteUtils.centerOn(lwjglLogo, width * 0.85f, lwjglLogo.getHeight() * 0.5f);
		SpriteUtils.centerOn(libgdxLogo, width * 0.15f, libgdxLogo.getHeight() * 0.5f);

		Synchronizers.transition(blurColor, Transitions.transitionBuilder(new Color(1f, 0f, 0f, 0f)).end(new Color(1f, 0f, 0f, 1f)).time(1f));

		TaskQueue taskQueue = super.getTaskQueue();

		taskQueue.add(new SimulateLoadingTimeRunnable(0));

		final CustomResourceManager<String> resourceManager = game.getResourceManager();
		ArrayList<String> registeredResources = resourceManager.getRegisteredResources();
		for (int i = 0; i < registeredResources.size(); i++) {
			final String resourceId = registeredResources.get(i);
			taskQueue.add(new Runnable() {
				@Override
				public void run() {
					Gdx.app.log("VampireRunner", "Loading resource: " + resourceId);
					Resource resource = resourceManager.get(resourceId);
					resource.load();
				}
			}, "Loading assets");
		}

		taskQueue.add(new Runnable() {
			@Override
			public void run() {
				mainMenu();
			}
		});
	}

	private void mainMenu() {
//		game.transition(game.getHighscoresScreen()) //
		game.transition(game.getMainMenuScreen()) //
				.leaveTime(1500) //
				.disposeCurrent() //
				.start();
	}

	@Override
	public void render() {

		Gdx.graphics.getGL10().glClear(GL10.GL_COLOR_BUFFER_BIT);
		spriteBatch.begin();
		gemserkLogoBlur.setColor(blurColor);
		gemserkLogoBlur.draw(spriteBatch);
		gemserkLogo.draw(spriteBatch);
		if (Gdx.app.getType() != ApplicationType.Android)
			lwjglLogo.draw(spriteBatch);
		libgdxLogo.draw(spriteBatch);

		float percentage = getTaskQueue().getProgress().getPercentage();
		String currentTaskName = getTaskQueue().getCurrentTaskName();
		if ("".equals(currentTaskName))
			currentTaskName = "Loading ";
		SpriteBatchUtils.drawMultilineTextCentered(spriteBatch, font, currentTaskName + " - " + (int) (percentage) + "%...", //
				Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.25f);

		spriteBatch.end();

		super.render();
	}

	@Override
	public void update() {
		Synchronizers.synchronize(getDelta());
	}

	@Override
	public void resume() {
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		spriteBatch = null;
	}

}
