package com.gemserk.games.angryships.gamestates;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.EventManagerWorldSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.PreviousStateSpatialSystem;
import com.gemserk.commons.artemis.systems.ReflectionRegistratorEventSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SoundSpawnerSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.audio.SoundPlayer;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraRestrictedImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.input.GraphicButtonMonitor;
import com.gemserk.games.angryships.render.Layers;
import com.gemserk.games.angryships.systems.PixmapCollidableSystem;
import com.gemserk.games.angryships.templates.BombTemplate;
import com.gemserk.games.angryships.templates.ExplosionSpawnerTemplate;
import com.gemserk.games.angryships.templates.KeyboardControllerTemplate;
import com.gemserk.games.angryships.templates.TargetTemplate;
import com.gemserk.games.angryships.templates.TerrainEntityTemplate;
import com.gemserk.resources.ResourceManager;

public class PlayGameState extends GameStateImpl {

	class LeftButton {

		Controller controller;
		Sprite sprite;

		ButtonMonitor buttonMonitor;

		public LeftButton(Controller controller) {
			this.controller = controller;
			this.sprite = resourceManager.getResourceValue("ButtonTurnLeftSprite");
			SpriteUtils.centerOn(this.sprite, Gdx.graphics.getWidth() * 0.085f, Gdx.graphics.getHeight() * 0.15f);
			this.buttonMonitor = new GraphicButtonMonitor(sprite);
		}

		void update() {
			buttonMonitor.update();
			controller.left = buttonMonitor.isHolded();
		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	class RightButton {

		Controller controller;
		Sprite sprite;

		ButtonMonitor buttonMonitor;

		public RightButton(Controller controller) {
			this.controller = controller;
			this.sprite = resourceManager.getResourceValue("ButtonTurnRightSprite");
			SpriteUtils.centerOn(this.sprite, Gdx.graphics.getWidth() * (1f - 0.085f), Gdx.graphics.getHeight() * 0.15f);
			this.buttonMonitor = new GraphicButtonMonitor(sprite);
		}

		void update() {
			buttonMonitor.update();
			controller.right = buttonMonitor.isHolded();
		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	class FireButton {

		Controller controller;
		Sprite sprite;

		ButtonMonitor buttonMonitor;

		public FireButton(Controller controller) {
			this.controller = controller;
			this.sprite = resourceManager.getResourceValue("ButtonFireSprite");
			SpriteUtils.centerOn(this.sprite, Gdx.graphics.getWidth() * (1f - 0.25f), Gdx.graphics.getHeight() * 0.15f);
			this.buttonMonitor = new GraphicButtonMonitor(sprite);
		}

		void update() {
			buttonMonitor.update();
			controller.fire = buttonMonitor.isReleased();
		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	boolean rotate = false;

	Controller controller;

	LeftButton leftButton;
	RightButton rightButton;
	FireButton fireButton;
	Sprite backgroundSprite;

	Libgdx2dCamera backgroundCamera;
	Libgdx2dCamera secondBackgroundCamera;

	Libgdx2dCamera worldCamera;
	Libgdx2dCamera guiCamera;

	Camera worldFollowCamera;
	Camera backgroundFollowCamera;
	Camera secondBackgroundFollowCamera;

	ResourceManager<String> resourceManager;
	AdWhirlViewHandler adWhirlViewHandler;
	SoundPlayer soundPlayer;

	Rectangle worldBounds;
	private Sprite secondBackgroundSprite;
	WorldWrapper worldWrapper;

	Injector injector;
	EntityFactory entityFactory;
	private PixmapWorld pixmapWorld;
	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		backgroundSprite = resourceManager.getResourceValue("BackgroundSprite");
		secondBackgroundSprite = resourceManager.getResourceValue("SecondBackgroundSprite");

		float worldScaleFactor = 1.5f;

		worldBounds = new Rectangle(-1024f * worldScaleFactor * 0.5f, -512 * worldScaleFactor * 0.5f, 1024f * worldScaleFactor, 512f * worldScaleFactor);

		backgroundSprite.setOrigin(worldBounds.getWidth() * 0.5f, worldBounds.getHeight() * 0.5f);
		backgroundSprite.setSize(worldBounds.getWidth(), worldBounds.getHeight());
		backgroundSprite.setPosition(0f - backgroundSprite.getOriginX(), 0f - backgroundSprite.getOriginY());

		secondBackgroundSprite.setOrigin(worldBounds.getWidth() * 0.5f, worldBounds.getHeight() * 0.5f);
		secondBackgroundSprite.setSize(worldBounds.getWidth(), worldBounds.getHeight());
		secondBackgroundSprite.setPosition(0f - secondBackgroundSprite.getOriginX(), 0f - secondBackgroundSprite.getOriginY());

		backgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		secondBackgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);

		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		guiCamera = new Libgdx2dCameraTransformImpl();

		worldCamera.move(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera.zoom(1f);

		Rectangle worldCameraBounds = new Rectangle(-1024f * 2f * 0.5f, -512 * 2f * 0.5f, 1024f * 4f, 512f * 4f);

		worldFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldCameraBounds);
		backgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);
		secondBackgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);

		// pixmapTerrain = new PixmapHelper(pixmap);

		Gdx.graphics.getGL10().glClearColor(0.5f, 0.5f, 0.5f, 0f);

		controller = new Controller();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorPointerDown("releaseBomb", 0);
			}
		};

		leftButton = new LeftButton(controller);
		rightButton = new RightButton(controller);
		fireButton = new FireButton(controller);

		worldWrapper = new WorldWrapper(new World());

		RenderLayers renderLayers = new RenderLayers();
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-500, 500, worldCamera));

		entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
		EventManager eventManager = new EventManagerImpl();

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(), false);

		pixmapWorld = new PixmapWorld();

		// pixmapWorld.addPixmap(pixmapTerrain);
		BodyBuilder bodyBuilder = new BodyBuilder(physicsWorld);

		injector = new InjectorImpl();

		injector.bind("timeStepProvider", new TimeStepProviderGameStateImpl(this));
		injector.bind("renderLayers", renderLayers);
		injector.bind("entityFactory", entityFactory);
		injector.bind("resourceManager", resourceManager);
		injector.bind("pixmapWorld", pixmapWorld);
		injector.bind("eventManager", eventManager);
		injector.bind("soundPlayer", soundPlayer);
		injector.bind("bodyBuilder", bodyBuilder);

		worldWrapper.addUpdateSystem(injector.getInstance(PreviousStateSpatialSystem.class));
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(injector.getInstance(MovementSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(PixmapCollidableSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(ScriptSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(SoundSpawnerSystem.class));

		worldWrapper.addUpdateSystem(injector.getInstance(EventManagerWorldSystem.class));
		worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		worldWrapper.addRenderSystem(injector.getInstance(CameraUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(SpriteUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(RenderableSystem.class));

		worldWrapper.init();

		EntityTemplate terrainEntityTemplate = injector.getInstance(TerrainEntityTemplate.class);
		EntityTemplate explosionSpawnerTemplate = injector.getInstance(ExplosionSpawnerTemplate.class);
		EntityTemplate keyboardControllerTemplate = injector.getInstance(KeyboardControllerTemplate.class);
		EntityTemplate targetTemplate = injector.getInstance(TargetTemplate.class);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(256f, 256f, 32f, 32f, 0)) //
				.put("terrainId", "Level01_0") //
				);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(256f + 512f, 256f, 32f, 32f, 0)) //
				.put("terrainId", "Level01_1") //
				);

		entityFactory.instantiate(explosionSpawnerTemplate, new ParametersWrapper());

		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet)
			entityFactory.instantiate(keyboardControllerTemplate, new ParametersWrapper() //
					.put("controller", controller) //
					);

		entityFactory.instantiate(targetTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(256f + 512f, 125f, 64f, 64f, 0)) //
				);
	}

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		worldWrapper.update(getDeltaInMs());

		if (Gdx.app.getType() == ApplicationType.Android) {
			leftButton.update();
			rightButton.update();
			fireButton.update();
		}

		if (controller.fire) {
			EntityTemplate bombEntityTemplate = injector.getInstance(BombTemplate.class);
			entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
					.put("spatial", new SpatialImpl(-200f, Gdx.graphics.getHeight() * 0.5f, 32f, 32f, 0)) //
					.put("controller", controller) //
					);
		}

		float midpointx = 0f;
		float midpointy = 0f;

		ImmutableBag<Entity> bombEntities = worldWrapper.getWorld().getGroupManager().getEntities(Groups.Bombs);

		for (int i = 0; i < bombEntities.size(); i++) {
			Entity bomb = bombEntities.get(i);

			SpatialComponent spatialComponent = Components.spatialComponent(bomb);
			Spatial spatial = spatialComponent.getSpatial();

			midpointx += spatial.getX();
			midpointy += spatial.getY();
		}

		if (bombEntities.size() >= 1) {
			midpointx /= bombEntities.size();
			midpointy /= bombEntities.size();
			worldFollowCamera.setPosition(midpointx, midpointy);
			backgroundFollowCamera.setPosition(midpointx / 12f, midpointy / 12f);
			secondBackgroundFollowCamera.setPosition(midpointx / 4f, midpointy / 4f);
		}

		worldCamera.zoom(worldFollowCamera.getZoom());
		worldCamera.move(worldFollowCamera.getX(), worldFollowCamera.getY());

		backgroundCamera.zoom(backgroundFollowCamera.getZoom());
		backgroundCamera.move(backgroundFollowCamera.getX(), backgroundFollowCamera.getY());

		secondBackgroundCamera.zoom(secondBackgroundFollowCamera.getZoom());
		secondBackgroundCamera.move(secondBackgroundFollowCamera.getX(), secondBackgroundFollowCamera.getY());

	}

	Box2DDebugRenderer box2dDebugRenderer = new Box2DDebugRenderer();

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// //

		backgroundCamera.apply(spriteBatch);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		spriteBatch.end();

		secondBackgroundCamera.apply(spriteBatch);
		spriteBatch.begin();
		secondBackgroundSprite.draw(spriteBatch);
		spriteBatch.end();

		// worldCamera.apply(spriteBatch);
		// spriteBatch.begin();
		//
		// spriteBatch.end();
		worldWrapper.render();

		guiCamera.apply(spriteBatch);
		spriteBatch.begin();
		 if (Gdx.app.getType() == ApplicationType.Android) {
		leftButton.draw(spriteBatch);
		rightButton.draw(spriteBatch);
		fireButton.draw(spriteBatch);
		 }
		spriteBatch.end();

//		box2dDebugRenderer.render(physicsWorld, worldCamera.getCombinedMatrix());

	}

	@Override
	public void resume() {
		pixmapWorld.reload();
		adWhirlViewHandler.show();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		pixmapWorld.dispose();
		spriteBatch.dispose();
		resourceManager.unloadAll();
	}

}