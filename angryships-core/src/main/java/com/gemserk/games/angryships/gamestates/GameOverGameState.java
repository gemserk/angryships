package com.gemserk.games.angryships.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transition;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.event.TransitionEventHandler;
import com.gemserk.animation4j.transitions.sync.Synchronizer;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.gui.animation4j.converters.GuiConverters;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.Game;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.input.CustomImageButton;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.resources.ResourceManager;

public class GameOverGameState extends GameStateImpl {

	private static class GuiScreen {

		static final String RestartButton = "RestartButton";

	}

	Game game;
	ResourceManager<String> resourceManager;
	AdWhirlViewHandler adWhirlViewHandler;
	Injector injector;

	private GL10 gl;
	private SpriteBatch spriteBatch;

	Synchronizer synchronizer;
	Libgdx2dCamera guiCamera;
	WorldWrapper worldWrapper;
	PixmapWorld pixmapWorld;
	Container screen;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();
		gl.glClearColor(0f, 0f, 0f, 1f);

		synchronizer = new Synchronizer();
		spriteBatch = new SpriteBatch();

		guiCamera = new Libgdx2dCameraTransformImpl();

		screen = new Container();

		// BitmapFont messageFont = resourceManager.getResourceValue(GameResources.Fonts.MessageFont);
		//
		// screen.add(GuiControls.label("Level finished") //
		// .id("GameOverLabel") //
		// .position(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f) //
		// .font(messageFont) //
		// .center(0.5f, 0.5f) //
		// .color(1f, 1f, 1f, 1f) //
		// .build());

		Sprite whiteRectangleSprite = resourceManager.getResourceValue(GameResources.Sprites.WhiteRectangleSprite);

		screen.add(GuiControls.imageButton(new CustomImageButton(whiteRectangleSprite)) //
				.center(0f, 0f) //
				.size(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) //
				.position(0f, 0f) //
				.color(0f, 0f, 0f, 0.25f) //
				.build());

		Sprite restartButtonSprite = resourceManager.getResourceValue(GameResources.Sprites.RestartButtonSprite);

		screen.add(GuiControls.imageButton(new CustomImageButton(restartButtonSprite)) //
				.id(GuiScreen.RestartButton) //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.35f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		worldWrapper = getParameters().get("worldWrapper");
		pixmapWorld = getParameters().get("pixmapWorld");
	}

	@Override
	public void update() {
		super.update();
		synchronizer.synchronize(getDelta());
		screen.update();
		worldWrapper.update(getDeltaInMs());

		CustomImageButton restartButton = screen.findControl("RestartButton");

		if (restartButton.getButtonMonitor().isReleased()) {

			Transition hideTransition = Transitions.mutableTransition(restartButton, GuiConverters.controlPositionConverter) //
					.end(0.5f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * -0.5f) //
					.functions(InterpolationFunctions.easeOut(), InterpolationFunctions.easeOut()) //
					.build();

			synchronizer.transition(hideTransition);

			synchronizer.monitor(hideTransition, new TransitionEventHandler() {
				@Override
				public void onTransitionFinished(Transition transition) {
					game.transition(game.playScreen) //
							.disposeCurrent() //
							.restartScreen() //
							.leaveTime(0f) //
							.start();
				}
			});

		}

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		worldWrapper.render();
		guiCamera.apply(spriteBatch);
		spriteBatch.begin();
		screen.draw(spriteBatch);
		spriteBatch.end();
	}

	@Override
	public void resume() {
		pixmapWorld.reload();
		adWhirlViewHandler.show();
		Gdx.input.setCatchBackKey(false);

		Control restartButton = screen.findControl(GuiScreen.RestartButton);

		synchronizer.transition(Transitions.mutableTransition(restartButton, GuiConverters.controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0.5f, -Gdx.graphics.getHeight() * 0.5f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

}