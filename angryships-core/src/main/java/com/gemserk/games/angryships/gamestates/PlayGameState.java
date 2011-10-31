package com.gemserk.games.angryships.gamestates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.EntityBuilder;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.EventManagerImpl;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.systems.AntiGravitySystem;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.ContainerSystem;
import com.gemserk.commons.artemis.systems.EventManagerWorldSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.OwnerSystem;
import com.gemserk.commons.artemis.systems.PhysicsSystem;
import com.gemserk.commons.artemis.systems.PreviousStateSpatialSystem;
import com.gemserk.commons.artemis.systems.ReflectionRegistratorEventSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SoundSpawnerSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.systems.TagSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.DensityUtils;
import com.gemserk.commons.gdx.DensityUtils.Density;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.audio.SoundPlayer;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraRestrictedImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.GuiControls;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.Game;
import com.gemserk.games.angryships.GameInformation;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.components.PlayerData;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.input.CustomImageButton;
import com.gemserk.games.angryships.render.Layers;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.GameModeNormalScript;
import com.gemserk.games.angryships.systems.Box2dRenderSystem;
import com.gemserk.games.angryships.systems.PixmapCollidableSystem;
import com.gemserk.games.angryships.systems.RenderLayerParticleEmitterImpl;
import com.gemserk.games.angryships.systems.TimerTriggerSystem;
import com.gemserk.games.angryships.systems.TimerUpdateSystem;
import com.gemserk.games.angryships.templates.AreaTriggerTemplate;
import com.gemserk.games.angryships.templates.CameraFollowTemplate;
import com.gemserk.games.angryships.templates.ClusterBombMunitionSpawnerTemplate;
import com.gemserk.games.angryships.templates.ExplosionSpawnerTemplate;
import com.gemserk.games.angryships.templates.HudButtonsControllerTemplate;
import com.gemserk.games.angryships.templates.HudTemplate;
import com.gemserk.games.angryships.templates.KeyboardControllerTemplate;
import com.gemserk.games.angryships.templates.PlayerTemplate;
import com.gemserk.games.angryships.templates.StaticSpriteTemplate;
import com.gemserk.games.angryships.templates.TargetTemplate;
import com.gemserk.games.angryships.templates.TerrainTemplate;
import com.gemserk.games.angryships.templates.TimerTriggerTemplate;
import com.gemserk.resources.ResourceManager;

public class PlayGameState extends GameStateImpl {

	public static class Actions {

		public static final String releaseBomb = "releaseBomb";

	}

	Game game;

	private GL10 gl;
	private SpriteBatch spriteBatch;

	private InputDevicesMonitorImpl<String> inputDevicesMonitor;

	Libgdx2dCamera guiCamera;

	ResourceManager<String> resourceManager;
	AdWhirlViewHandler adWhirlViewHandler;
	SoundPlayer soundPlayer;
	DensityUtils densityUtils;

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

		float worldScaleFactor = 1f;

		Rectangle backgroundCameraWorldBounds = new Rectangle(-1024f * worldScaleFactor * 0.5f, -512 * worldScaleFactor * 0.5f, 1024f * worldScaleFactor, 512f * worldScaleFactor);

		Libgdx2dCamera backgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		Libgdx2dCamera secondBackgroundCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera = new Libgdx2dCameraTransformImpl(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.49f);
		guiCamera = new Libgdx2dCameraTransformImpl();

		Rectangle worldBounds = new Rectangle(-10, 0, 40f, 25f);

		worldCamera.move(Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.5f);
		worldCamera.zoom(worldScaleFactor);

		Camera worldFollowCamera = new CameraRestrictedImpl(0f, 0f, 40f * worldScaleFactor, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);

		Camera backgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), backgroundCameraWorldBounds);
		Camera secondBackgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), backgroundCameraWorldBounds);

		// pixmapTerrain = new PixmapHelper(pixmap);

		Gdx.graphics.getGL10().glClearColor(0f, 0f, 0f, 1f);

		Controller controller = new Controller();

		// /

		screen = new Container();
		
		float buttonWidth = 128f;
		float buttonHeight = 128f;
		
		if (densityUtils.getDensity() == Density.Low) {
			buttonWidth = 64f;
			buttonHeight = 64f;
		} else if (densityUtils.getDensity() == Density.Medium) {
			buttonWidth = 96f;
			buttonHeight = 96f;
		}
		
		float defaultWidth = buttonWidth / Gdx.graphics.getWidth();
		float defaultHeight = buttonHeight / Gdx.graphics.getHeight();

		Container moveButtonsContainer = new Container("MovementButtonsContainer");
		moveButtonsContainer.setPosition(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f);

		Sprite turnRightSprite = resourceManager.getResourceValue(GameResources.Sprites.TurnRightButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(turnRightSprite)) //
				.id("TurnRightButton") //
				.center(0.5f, 0.5f) //
				.size(buttonWidth, buttonHeight) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Sprite explodeSprite = resourceManager.getResourceValue(GameResources.Sprites.FireButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(explodeSprite)) //
				.id("ExplodeButton") //
				.center(0.5f, 0.5f) //
				.size(buttonWidth, buttonHeight) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f - defaultWidth), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Sprite turnLeftSprite = resourceManager.getResourceValue(GameResources.Sprites.TurnLeftButtonSprite);

		moveButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(turnLeftSprite)) //
				.id("TurnLeftButton") //
				.center(0.5f, 0.5f) //
				.size(buttonWidth, buttonHeight) //
				.position(Gdx.graphics.getWidth() * (0f + defaultWidth * 0.5f), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Container fireButtonsContainer = new Container("FireButtonsContainer");
		fireButtonsContainer.setPosition(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f);

		Sprite fireButtonSprite = resourceManager.getResourceValue(GameResources.Sprites.FireButtonSprite);

		fireButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(fireButtonSprite)) //
				.id("FireButton") //
				.center(0.5f, 0.5f) //
				.size(buttonWidth, buttonHeight) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		fireButtonsContainer.add(GuiControls.label("NA") //
				.id("NormalBombCountLabel") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		Sprite kamikazeBombButtonSprite = resourceManager.getResourceValue(GameResources.Sprites.FireButtonSprite);

		fireButtonsContainer.add(GuiControls.imageButton(new CustomImageButton(kamikazeBombButtonSprite)) //
				.id("SecondFireButton") //
				.center(0.5f, 0.5f) //
				.size(buttonWidth, buttonHeight) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f - defaultWidth), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		fireButtonsContainer.add(GuiControls.label("NA") //
				.id("KamikazeBombCountLabel") //
				.center(0.5f, 0.5f) //
				.position(Gdx.graphics.getWidth() * (1f - defaultWidth * 0.5f - defaultWidth), Gdx.graphics.getHeight() * defaultHeight * 0.5f) //
				.color(1f, 1f, 1f, 1f) //
				.build());

		screen.add(moveButtonsContainer);
		screen.add(fireButtonsContainer);

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				monitorPointerDown(Actions.releaseBomb, 0);
			}
		};

		worldWrapper = new WorldWrapper(new World());

		RenderLayers renderLayers = new RenderLayers();

		renderLayers.add(Layers.Background, new RenderLayerSpriteBatchImpl(-10000, -1000, backgroundCamera));
		renderLayers.add(Layers.SecondBackground, new RenderLayerSpriteBatchImpl(-1000, -500, secondBackgroundCamera));
		renderLayers.add(Layers.Particles, new RenderLayerParticleEmitterImpl(-500, -400, worldCamera));
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-400, 500, worldCamera));
		renderLayers.add(Layers.Hud, new RenderLayerSpriteBatchImpl(500, 10000, guiCamera));

		entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());
		EventManager eventManager = new EventManagerImpl();
		EntityBuilder entityBuilder = new EntityBuilder(worldWrapper.getWorld());

		physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0f, -10f), false);

		pixmapWorld = new PixmapWorld();

		// pixmapWorld.addPixmap(pixmapTerrain);
		BodyBuilder bodyBuilder = new BodyBuilder(physicsWorld);

		injector = new InjectorImpl();

		// SignalRegistry signalRegistry = new SignalRegistryImpl();
		// bombSpawnedSignal = signalRegistry.register(Events.bombSpawned);

		injector.bind("timeStepProvider", new TimeStepProviderGameStateImpl(this));
		injector.bind("renderLayers", renderLayers);
		injector.bind("entityFactory", entityFactory);
		injector.bind("resourceManager", resourceManager);
		injector.bind("pixmapWorld", pixmapWorld);
		injector.bind("eventManager", eventManager);
		injector.bind("soundPlayer", soundPlayer);
		injector.bind("bodyBuilder", bodyBuilder);
		injector.bind("screen", screen);
		// injector.bind("signalRegistry", signalRegistry);

		worldWrapper.addUpdateSystem(injector.getInstance(PreviousStateSpatialSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(AntiGravitySystem.class));
		worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		worldWrapper.addUpdateSystem(injector.getInstance(MovementSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(PixmapCollidableSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(ScriptSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(SoundSpawnerSystem.class));

		worldWrapper.addUpdateSystem(injector.getInstance(EventManagerWorldSystem.class));
		worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));

		worldWrapper.addUpdateSystem(injector.getInstance(TimerUpdateSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(TimerTriggerSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(TagSystem.class));
		
		// 
		worldWrapper.addUpdateSystem(injector.getInstance(OwnerSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(ContainerSystem.class));		

		worldWrapper.addRenderSystem(injector.getInstance(CameraUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(SpriteUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(RenderableSystem.class));
		worldWrapper.addRenderSystem(new Box2dRenderSystem(worldCamera, physicsWorld));

		worldWrapper.init();

		EntityTemplate terrainEntityTemplate = injector.getInstance(TerrainTemplate.class);
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
				.put("area", new Rectangle(worldBounds.x + worldBounds.width + 8f, worldBounds.y, 1f, worldBounds.height)) //
				);
		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x, worldBounds.y + 0f, worldBounds.width + 10f, 1.5f)) //
				);
		entityFactory.instantiate(areaTriggerTemplate, new ParametersWrapper() //
				.put("area", new Rectangle(worldBounds.x, worldBounds.y + worldBounds.height + 2f, worldBounds.width + 10f, 1f)) //
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
				.put("distance", new Float(0.5f)) //
				);

		entityFactory.instantiate(cameraFollowTemplate, new ParametersWrapper() //
				.put("libgdx2dCamera", secondBackgroundCamera) //
				.put("camera", secondBackgroundFollowCamera) //
				.put("distance", new Float(0.25f)) //
				);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(10f, 10f, 12.8f, 12.8f, 0)) //
				.put("terrainId", "Level01_0") //
				);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "Level01_0Sprite") //
				.put("spatial", new SpatialImpl(10f, 10f, 12.8f, 12.8f, 0)) //
				.put("color", new Color(0f, 0f, 0f, 0.35f)) //
				.put("layer", -360) //
				);

		entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(10f + 12.8f, 10, 12.8f, 12.8f, 0)) //
				.put("terrainId", "Level01_1") //
				);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "Level01_1Sprite") //
				.put("spatial", new SpatialImpl(10f + 12.8f, 10, 12.8f, 12.8f, 0)) //
				.put("color", new Color(0f, 0f, 0f, 0.35f)) //
				.put("layer", -360) //
				);

		// entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
		// .put("spatial", new SpatialImpl(worldBounds.x + 12.8f, 12.8f * 0.48f, 12.8f * 2, 12.8f, 0)) //
		// .put("terrainId", "Level01-ground") //
		// .put("resistance", 1f) //
		// );

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "LevelGroundSprite") //
				.put("spatial", new SpatialImpl(worldBounds.x + 12.8f, 12.8f * 0.48f, 12.8f * 2, 12.8f, 0)) //
				.put("color", Color.WHITE) //
				.put("layer", -360) //
				);

		// entityFactory.instantiate(terrainEntityTemplate, new ParametersWrapper() //
		// .put("spatial", new SpatialImpl(worldBounds.x + 12.8f + 12.8f * 2, 12.8f * 0.48f, 12.8f * 2, 12.8f, 0)) //
		// .put("terrainId", "Level01-ground") //
		// .put("resistance", 1f) //
		// );

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", "LevelGroundSprite") //
				.put("spatial", new SpatialImpl(worldBounds.x + 12.8f + 12.8f * 2, 12.8f * 0.48f, 12.8f * 2, 12.8f, 0)) //
				.put("color", Color.WHITE) //
				.put("layer", -360) //
				);

		entityFactory.instantiate(staticSpriteTemplate, new ParametersWrapper() //
				.put("spriteId", GameResources.Sprites.FarmSprite) //
				.put("spatial", new SpatialImpl(-1f, 3f, 3.2f, 3.2f, 0f)) //
				.put("layer", -361) //
				);

		entityFactory.instantiate(explosionSpawnerTemplate, new ParametersWrapper());

		if (Gdx.app.getType() == ApplicationType.Desktop || Gdx.app.getType() == ApplicationType.Applet) {
			entityFactory.instantiate(keyboardControllerTemplate, new ParametersWrapper() //
					.put("controller", controller) //
					);
		} else {
			entityFactory.instantiate(injector.getInstance(HudButtonsControllerTemplate.class), new ParametersWrapper() //
					.put("controller", controller) //
					.put("leftButtonMonitor", ((CustomImageButton) screen.findControl("TurnLeftButton")).getButtonMonitor()) //
					.put("rightButtonMonitor", ((CustomImageButton) screen.findControl("TurnRightButton")).getButtonMonitor()) //
					.put("fireButtonMonitor", ((CustomImageButton) screen.findControl("FireButton")).getButtonMonitor()) //
					.put("secondFireButtonMonitor", ((CustomImageButton) screen.findControl("SecondFireButton")).getButtonMonitor()) //
					.put("explodeButtonMonitor", ((CustomImageButton) screen.findControl("ExplodeButton")).getButtonMonitor()) //
					);
		}

		entityFactory.instantiate(targetTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(23.5f, 7f, 1f, 1f, 0)) //
				);

		entityFactory.instantiate(injector.getInstance(ClusterBombMunitionSpawnerTemplate.class));

		entityFactory.instantiate(injector.getInstance(HudTemplate.class));

		entityFactory.instantiate(injector.getInstance(PlayerTemplate.class), new ParametersWrapper() //
				.put("controller", controller) //
				.put("playerData", new PlayerData(5, 3)) //
				);

		entityFactory.instantiate(new EntityTemplateImpl() {
			@Override
			public void apply(Entity entity) {
				entity.addComponent(new ScriptComponent(injector.getInstance(GameModeNormalScript.class)));
			}
		});

		entityBuilder //
				.component(new ScriptComponent(new ScriptJavaImpl() {

					private final Parameters parameters = new ParametersWrapper();

					private World world;

					@Override
					public void init(World world, Entity e) {
						this.world = world;
					}

					@Handles(ids = Events.gameOver)
					public void gameOver(Event event) {

						boolean win = world.getGroupManager().getEntities(Groups.Targets).size() == 0;

						Gdx.app.log(GameInformation.applicationId, "Game over: " + (win ? "win" : "lose"));

						Entity controller = world.getTagManager().getEntity(Tags.Controller);

						if (controller != null)
							controller.delete();

						TimerTriggerTemplate timerTriggerTemplate = injector.getInstance(TimerTriggerTemplate.class);

						entityFactory.instantiate(timerTriggerTemplate, parameters //
								.put("time", 2.5f) //
								.put("eventId", Events.restartLevel) //
								);

					}

					@Handles(ids = Events.restartLevel)
					public void restartLevel(Event event) {
						game.transition(game.gameOverScreen) //
								.parameter("worldWrapper", worldWrapper) //
								.parameter("pixmapWorld", pixmapWorld) //
								.restartScreen() //
								.leaveTime(0f) //
								.start();
					}

				})) //
				.build();

		worldWrapper.update(1);

	}

	@Override
	public void update() {
		super.update();

		Synchronizers.synchronize(getDelta());

		screen.update();

		inputDevicesMonitor.update();

		if (inputDevicesMonitor.getButton(Actions.releaseBomb).isReleased()) {
			// bombSpawnedSignal.signal(this);
		}

		worldWrapper.update(getDeltaInMs());

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
		// pixmapWorld.dispose();
		worldWrapper.dispose();
		spriteBatch.dispose();
	}

}