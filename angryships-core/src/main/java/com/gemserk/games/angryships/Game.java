package com.gemserk.games.angryships;

import java.io.IOException;
import java.util.Properties;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.converters.Converters;
import com.gemserk.animation4j.gdx.converters.LibgdxConverters;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.EventListenerReflectionRegistrator;
import com.gemserk.commons.gdx.GameState;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.Screen;
import com.gemserk.commons.gdx.ScreenImpl;
import com.gemserk.commons.gdx.audio.SoundPlayer;
import com.gemserk.commons.gdx.graphics.SpriteBatchUtils;
import com.gemserk.commons.gdx.screens.transitions.TransitionBuilder;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.gamestates.GameOverGameState;
import com.gemserk.games.angryships.gamestates.PlayGameState;
import com.gemserk.games.angryships.gamestates.SplashGameState;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.resources.CustomResourceManager;
import com.gemserk.util.ScreenshotSaver;

public class Game extends com.gemserk.commons.gdx.Game {

	private static boolean showFps = false;
	private static boolean showBox2dDebug = false;

	public static void setShowFps(boolean showFps) {
		Game.showFps = showFps;
	}

	public static boolean isShowFps() {
		return showFps;
	}

	public static boolean isShowBox2dDebug() {
		return showBox2dDebug;
	}

	public static void setShowBox2dDebug(boolean showBox2dDebug) {
		Game.showBox2dDebug = showBox2dDebug;
	}

	private CustomResourceManager<String> resourceManager;
	private BitmapFont fpsFont;
	private SpriteBatch spriteBatch;
	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	private EventManager eventManager;

	/**
	 * Used to store global information about the game and to send data between GameStates and Screens.
	 */
	private Parameters gameData;
	private AdWhirlViewHandler adWhirlViewHandler;
	
	public Screen splashScreen;
	public Screen playScreen;
	public Screen gameOverScreen;
	
	public Parameters getGameData() {
		return gameData;
	}

	/**
	 * Used to communicate between gamestates.
	 */
	public EventManager getEventManager() {
		return eventManager;
	}
	
	public void setAdWhirlViewHandler(AdWhirlViewHandler adWhirlViewHandler) {
		this.adWhirlViewHandler = adWhirlViewHandler;
	}

	@Override
	public void create() {

		Converters.register(Vector2.class, LibgdxConverters.vector2());
		Converters.register(Color.class, LibgdxConverters.color());

		gameData = new ParametersWrapper();

		try {
			Properties properties = new Properties();
			properties.load(Gdx.files.classpath("version.properties").read());
			getGameData().put("version", properties.getProperty("version"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// ExecutorService executorService = Executors.newCachedThreadPool();
		// Preferences preferences = Gdx.app.getPreferences("gemserk-" + GameInformation.applicationId);

		Injector injector = new InjectorImpl();

		injector.bind("game", this);
		injector.bind("eventManager", new EventManagerImpl());
		injector.bind("resourceManager", new CustomResourceManager<String>());
		injector.bind("adWhirlViewHandler", adWhirlViewHandler);
		injector.bind("soundPlayer", new SoundPlayer());
		
		injector.injectMembers(this);

		GameResources.load(resourceManager);

		// fpsFont = resourceManager.getResourceValue("FpsFont");
		fpsFont = new BitmapFont();
		spriteBatch = new SpriteBatch();

		GameState splashGameState = injector.getInstance(SplashGameState.class);
		GameState playGameState = injector.getInstance(PlayGameState.class);
		GameState gameOverGameState = injector.getInstance(GameOverGameState.class);

		splashScreen = new ScreenImpl(splashGameState);
		playScreen = new ScreenImpl(playGameState);
		gameOverScreen = new ScreenImpl(gameOverGameState);

		EventListenerReflectionRegistrator registrator = new EventListenerReflectionRegistrator(eventManager);

		registrator.registerEventListeners(this);

		setScreen(splashScreen);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorKey("grabScreenshot", Keys.NUM_9);
				monitorKey("toggleFps", Keys.NUM_7);
				monitorKey("restartScreen", Keys.NUM_1);
			}
		};

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);
	}

	@Override
	public void render() {

		GlobalTime.setDelta(Gdx.graphics.getDeltaTime());

		inputDevicesMonitor.update();

		super.render();

		spriteBatch.begin();
		if (showFps)
			SpriteBatchUtils.drawMultilineText(spriteBatch, fpsFont, "FPS: " + Gdx.graphics.getFramesPerSecond(), Gdx.graphics.getWidth() * 0.02f, Gdx.graphics.getHeight() * 0.90f, 0f, 0.5f);
		spriteBatch.end();

		if (inputDevicesMonitor.getButton("grabScreenshot").isReleased()) {
			try {
				ScreenshotSaver.saveScreenshot(GameInformation.applicationId);
			} catch (IOException e) {
				Gdx.app.log(GameInformation.applicationId, "Can't save screenshot");
			}
		}

		if (inputDevicesMonitor.getButton("restartScreen").isReleased()) {
			resourceManager.unloadAll();
			getScreen().restart();
		}

		if (inputDevicesMonitor.getButton("toggleFps").isReleased())
			setShowFps(!isShowFps());

		if (inputDevicesMonitor.getButton("toggleBox2dDebug").isReleased())
			setShowBox2dDebug(!isShowBox2dDebug());

		eventManager.process();
	}

	public TransitionBuilder transition(Screen screen) {
		return new TransitionBuilder(this, screen);
	}
	
	@Override
	public void pause() {
		super.pause();
		adWhirlViewHandler.hide();		
	}

	@Override
	public void dispose() {
		super.dispose();
		resourceManager.unloadAll();
		spriteBatch.dispose();
	}

}
