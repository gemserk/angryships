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
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.WorldWrapper;
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
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.ButtonHandler;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.input.CustomImageButton;
import com.gemserk.games.angryships.render.Layers;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.systems.AntiGravitySystem;
import com.gemserk.games.angryships.systems.Box2dRenderSystem;
import com.gemserk.games.angryships.systems.PixmapCollidableSystem;
import com.gemserk.games.angryships.templates.AreaTriggerTemplate;
import com.gemserk.games.angryships.templates.BombTemplate;
import com.gemserk.games.angryships.templates.CameraFollowTemplate;
import com.gemserk.games.angryships.templates.ClusterBombMunitionSpawnerTemplate;
import com.gemserk.games.angryships.templates.ExplosionSpawnerTemplate;
import com.gemserk.games.angryships.templates.HudTemplate;
import com.gemserk.games.angryships.templates.KeyboardControllerTemplate;
import com.gemserk.games.angryships.templates.StaticSpriteTemplate;
import com.gemserk.games.angryships.templates.TargetTemplate;
import com.gemserk.games.angryships.templates.TerrainEntityTemplate;
import com.gemserk.resources.ResourceManager;

public class PlayGameState extends GameStateImpl {

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	boolean rotate = false;

	Controller controller;

	Libgdx2dCamera guiCamera;

	ResourceManager<String> resourceManager;
	AdWhirlViewHandler adWhirlViewHandler;
	SoundPlayer soundPlayer;

	WorldWrapper worldWrapper;

	Injector injector;
	EntityFactory entityFactory;
	private PixmapWorld pixmapWorld;
	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	Container screen;
	private Libgdx2dCamera worldCamera;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		float worldScaleFactor = 1.5f;

		Rectangle backgroundCameraWorldBounds = new Rectangle(-1024f * worldScaleFactor * 0.5f, -512 * worldScaleFactor * 0.5f, 1024f * worldScaleFactor, 512f * worldScaleFactor);

		Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		Libgdx2dCamera secondBackgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		guiCamera = new Libgdx2dCameraTransformImpl();

		Rectangle worldBounds = new Rectangle(-10, 0, 50f, 25f);

		worldCamera.move(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera.zoom(1f);

		Camera worldFollowCamera = new CameraRestrictedImpl(0f, 0f, 40f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);

		Camera backgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), backgroundCameraWorldBounds);
		Camera secondBackgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), backgroundCameraWorldBounds);

		// pixmapTerrain = new PixmapHelper(pixmap);

		Gdx.graphics.getGL10().glClearColor(0.5f, 0.5f, 0.5f, 0f);

		controller = new Controller();

		// /

		screen = new Container();

		Container moveButtonsContainer = new Container("MovementButtonsContainer");

		Sprite turnRightSprite = resourceManager.getResourceValue(GameResources.Sprites.TurnRightButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(turnRightSprite)) //
				.id("TurnRightButton") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * (1f - 0.085f), Gdx.graphics.getHeight() * 0.15f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ButtonHandler() {
					@Override
					public void onPressed(Control control) {
						controller.right = true;
					}

					@Override
					public void onReleased(Control control) {
						controller.right = false;
					}

					@Override
					public void onLeave(Control control) {
						controller.right = false;
					}
				}) //
				.build());

		Sprite explodeSprite = resourceManager.getResourceValue(GameResources.Sprites.FireButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(explodeSprite) //
				.id("ExplodeButton") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * (1f - 3 * 0.085f), Gdx.graphics.getHeight() * 0.15f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						controller.explode = true;
					}
				}) //
				.build());

		Sprite turnLeftSprite = resourceManager.getResourceValue(GameResources.Sprites.TurnLeftButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(turnLeftSprite) //
				.id("TurnLeftButton") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * 0.085f, Gdx.graphics.getHeight() * 0.15f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ButtonHandler() {
					@Override
					public void onPressed(Control control) {
						controller.left = true;
					}

					@Override
					public void onReleased(Control control) {
						controller.left = false;
					}

					@Override
					public void onLeave(Control control) {
						controller.left = false;
					}
				}) //
				.build());

		Container fireButtonsContainer = new Container("FireButtonsContainer");

		Sprite fireButtonSprite = resourceManager.getResourceValue(GameResources.Sprites.FireButtonSprite);

		fireButtonsContainer.add(GuiControls.imageButton(fireButtonSprite) //
				.id("FireButton") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * (1f - 0.085f), Gdx.graphics.getHeight() * 0.15f) //
				// .position(Gdx.graphics.getWidth() * (1f - 0.25f), Gdx.graphics.getHeight() * 0.15f) //
				.color(1f, 1f, 1f, 1f) //
				.handler(new ButtonHandler() {
					@Override
					public void onReleased(Control control) {
						controller.fire = true;
					}
				}) //
				.build());

		screen.add(moveButtonsContainer);
		screen.add(fireButtonsContainer);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown("releaseBomb", 0);
			}
		};

		worldWrapper = new WorldWrapper(new World());

		RenderLayers renderLayers = new RenderLayers();

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-10000, -1000, backgroundCamera));
		renderLayers.add(Layers.SecondBackground, new RenderLayerSpriteBatchImpl(-1000, -500, secondBackgroundCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-500, 500, worldCamera));
		renderLayers.add(Layers.Hud, new RenderLayerSpriteBatchImpl(500, 10000, guiCamera));

		entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
		EventManager eventManager = new EventManagerImpl();

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, -10f), false);

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
		injector.bind("screen", screen);

		worldWrapper.addUpdateSystem(injector.getInstance(PreviousStateSpatialSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(AntiGravitySystem.class));
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
		worldWrapper.addRenderSystem(new Box2dRenderSystem(worldCamera, physicsWorld));

		worldWrapper.init();

		EntityTemplate terrainEntityTemplate = injector.getInstance(TerrainEntityTemplate.class);
		EntityTemplate explosionSpawnerTemplate = injector.getInstance(ExplosionSpawnerTemplate.class);
		EntityTemplate keyboardControllerTemplate = injector.getInstance(KeyboardControllerTemplate.class);
		EntityTemplate targetTemplate = injector.getInstance(TargetTemplate.class);
		EntityTemplate cameraFollowTemplate = injector.getInstance(CameraFollowTemplate.class);
		EntityTemplate staticSpriteTemplate = injector.getInstance(StaticSpriteTemplate.class);
		EntityTemplate areaTriggerTemplate = injector.getInstance(AreaTriggerTemplate.class);

		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x - 1f, worldBounds.y, 1f, worldBounds.height)) //
				);
		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x + worldBounds.width, worldBounds.y, 1f, worldBounds.height)) //
				);
		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x, worldBounds.y - 1f, worldBounds.width, 1f)) //
				);
		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x, worldBounds.y + worldBounds.height, worldBounds.width, 1f)) //
				);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", GameResources.Sprites.BackgroundSprite) //
				.put("spatial", new SpatialImpl(0, 0, backgroundCameraWorldBounds.getWidth(), backgroundCameraWorldBounds.getHeight(), 0f)) //
				.put("layer", -5000) //
				);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", GameResources.Sprites.SecondBackgroundSprite) //
				.put("spatial", new SpatialImpl(0, 0, backgroundCameraWorldBounds.getWidth(), backgroundCameraWorldBounds.getHeight(), 0f)) //
				.put("layer", -800) //
				);

		entityFactory.instantiate(cameraFollowTemplate, new ParametersWrapper() //
				.put("libgdx2dCamera", worldCamera) //
				.put("camera", worldFollowCamera) //
				.put("distance", new Float(1f)) //
				);

		entityFactory.instantiate(cameraFollowTemplate, new ParametersWrapper() //
				.put("libgdx2dCamera", backgroundCamera) //
				.put("camera", backgroundFollowCamera) //
				.put("distance", new Float(1f)) //
				);

		entityFactory.instantiate(cameraFollowTemplate, new ParametersWrapper() //
				.put("libgdx2dCamera", secondBackgroundCamera) //
				.put("camera", secondBackgroundFollowCamera) //
				.put("distance", new Float(0.5f)) //
				);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(10f, 10f, 12.8f, 12.8f, 0)) //
				.put("terrainId", "Level01_0") //
				);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(10f + 12.8f, 10, 12.8f, 12.8f, 0)) //
				.put("terrainId", "Level01_1") //
				);

		entityFactory.instantiate(explosionSpawnerTemplate, new ParametersWrapper());

		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet)
			entityFactory.instantiate(keyboardControllerTemplate, new ParametersWrapper() //
					.put("controller", controller) //
					);

		entityFactory.instantiate(targetTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(23.5f, 7f, 1f, 1f, 0)) //
				);

		entityFactory.instantiate(injector.getInstance(ClusterBombMunitionSpawnerTemplate.class));

		entityFactory.instantiate(injector.getInstance(HudTemplate.class));
		
//		CustomImageButton turnRightButton = screen.findControl("TurnRightButton");

	}

	@Override
	public void update() {
		super.update();

		controller.fire = false;

		Synchronizers.synchronize(getDelta());

		screen.update();

		inputDevicesMonitor.update();

		worldWrapper.update(getDeltaInMs());

		ImmutableBag<Entity> bombs = worldWrapper.getWorld().getGroupManager().getEntities(Groups.Bombs);

		if (bombs.size() <= 0) {
			if (controller.fire) {
				EntityTemplate bombEntityTemplate = injector.getInstance(BombTemplate.class);
				entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
						.put("spatial", new SpatialImpl(2f, 10f, 0.75f, 0.75f, 0)) //
						.put("controller", controller) //
						);

			}
		}
		
		// if (inputDevicesMonitor.getButton("releaseBomb").isReleased()) {
		//
		// EntityTemplate miniBombTemplate = injector.getInstance(ClusterBombMunitionTemplate.class);
		// Vector2 position = new Vector2(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		//
		// worldCamera.unproject(position);
		// System.out.println(position);
		//
		// for (int i = 0; i < 3; i++) {
		//
		// Entity minibomb = entityFactory.instantiate(miniBombTemplate, new ParametersWrapper() //
		// .put("spatial", new SpatialImpl(position.x, position.y, 0.4f, 0.4f, MathUtils.random(0f, 360f))) //
		// );
		//
		// PhysicsComponent physicsComponent = Components.physicsComponent(minibomb);
		// Body body = physicsComponent.getBody();
		// body.applyLinearImpulse(MathUtils.random(-3f, 3f), 0f, body.getPosition().x, body.getPosition().y);
		//
		// }
		//
		// }

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
	}

	@Override
	public void dispose() {
		pixmapWorld.dispose();
		spriteBatch.dispose();
		resourceManager.unloadAll();
	}

}