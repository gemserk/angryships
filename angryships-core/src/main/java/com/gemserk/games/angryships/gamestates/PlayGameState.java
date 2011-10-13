package com.gemserk.games.angryships.gamestates;

import java.util.ArrayList;

import com.artemis.World;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.artemis.WorldWrapper;
import com.gemserk.commons.artemis.render.RenderLayers;
import com.gemserk.commons.artemis.systems.CameraUpdateSystem;
import com.gemserk.commons.artemis.systems.MovementSystem;
import com.gemserk.commons.artemis.systems.PreviousStateSpatialSystem;
import com.gemserk.commons.artemis.systems.RenderLayerSpriteBatchImpl;
import com.gemserk.commons.artemis.systems.RenderableSystem;
import com.gemserk.commons.artemis.systems.ScriptSystem;
import com.gemserk.commons.artemis.systems.SpriteUpdateSystem;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityFactoryImpl;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.GameStateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.CameraRestrictedImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.camera.Libgdx2dCameraTransformImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.graphics.ColorUtils;
import com.gemserk.commons.gdx.graphics.SpriteUtils;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.commons.gdx.time.TimeStepProviderGameStateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.reflection.InjectorImpl;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.InputDevicesMonitorImpl;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.render.Layers;
import com.gemserk.games.angryships.templates.BombEntityTemplate;
import com.gemserk.prototypes.pixmap.PixmapHelper;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.ResourceManagerImpl;

public class PlayGameState extends GameStateImpl {

	static class Bomb {

		Vector2 position = new Vector2();
		Vector2 center = new Vector2(0.5f, 0.5f);
		Vector2 velocity = new Vector2();

		float width;
		float height;
		float explosionRadius;

		float angle;
		Sprite sprite;

		long soundHandle;

		PixmapHelper pixmapHelper;
		Color color = new Color();
		Vector2 projectedPosition = new Vector2();

		boolean deleted = false;

		Controller controller;

		public void setSprite(Sprite sprite) {
			this.sprite = sprite;
			this.width = sprite.getWidth();
			this.height = sprite.getHeight();
		}

		void update() {

			// velocity.y += -1 * 100f * GlobalTime.getDelta();

			position.x += velocity.x * GlobalTime.getDelta();
			position.y += velocity.y * GlobalTime.getDelta();

			sprite.setRotation(angle);
			sprite.setOrigin(width * center.x, height * center.y);
			sprite.setSize(width, height);
			sprite.setPosition(position.x - sprite.getOriginX(), position.y - sprite.getOriginY());

			pixmapHelper.project(projectedPosition, position.x, position.y);

			ColorUtils.rgba8888ToColor(color, pixmapHelper.getPixel(projectedPosition.x, projectedPosition.y));

			if (color.a != 0) {
				pixmapHelper.eraseCircle(projectedPosition.x, projectedPosition.y, explosionRadius);
				deleted = true;
				// remove this bomb...
			}

			float rotationAngle = 360f * GlobalTime.getDelta();

			if (controller.left) {
				this.angle += rotationAngle;
				velocity.rotate(rotationAngle);
			} else if (controller.right) {
				this.angle -= rotationAngle;
				velocity.rotate(-rotationAngle);
			}
		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	static class GraphicButtonMonitor extends ButtonMonitor {

		Sprite sprite;
		Rectangle bounds;

		public GraphicButtonMonitor(Sprite sprite) {
			this.sprite = sprite;
			bounds = new Rectangle(this.sprite.getX(), //
					this.sprite.getY(), //
					this.sprite.getWidth(), //
					this.sprite.getHeight());
		}

		@Override
		protected boolean isDown() {
			if (!Gdx.input.isTouched())
				return false;

			float x = Gdx.input.getX();
			float y = Gdx.graphics.getHeight() - Gdx.input.getY();

			return bounds.contains(x, y);
		}

	}

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

	static class BombExplosion {

		Sprite sprite;
		Vector2 position = new Vector2();
		float angle;
		Animation animation;

		Vector2 center = new Vector2(0.5f, 0.5f);

		float width;
		float height;

		public BombExplosion(Animation animation) {
			this.animation = animation;

			sprite = animation.getCurrentFrame();

			this.width = sprite.getWidth() * 1.5f;
			this.height = sprite.getHeight() * 1.5f;
		}

		void update() {

			animation.update(GlobalTime.getDelta());
			sprite = animation.getCurrentFrame();

			sprite.setRotation(angle);
			sprite.setOrigin(width * center.x, height * center.y);
			sprite.setSize(width, height);
			sprite.setPosition(position.x - sprite.getOriginX(), position.y - sprite.getOriginY());

		}

		void draw(SpriteBatch spriteBatch) {
			sprite.draw(spriteBatch);
		}

	}

	private GL10 gl;
	private SpriteBatch spriteBatch;
	// private OrthographicCamera orthographicCamera;

	private Color color = new Color();

	private PixmapHelper pixmapTerrain;

	private final Vector2 position = new Vector2();
	private InputDevicesMonitorImpl inputDevicesMonitor;

	boolean rotate = false;

	ArrayList<PlayGameState.Bomb> bombs = new ArrayList<PlayGameState.Bomb>();
	ArrayList<PlayGameState.Bomb> bombsToDelete = new ArrayList<PlayGameState.Bomb>();

	ArrayList<BombExplosion> bombExplosions = new ArrayList<BombExplosion>();
	ArrayList<BombExplosion> bombExplosionsToDelete = new ArrayList<BombExplosion>();

	private Sound bombExplosionSound;

	Controller controller;

	LeftButton leftButton;
	RightButton rightButton;
	FireButton fireButton;
	Sprite backgroundSprite;

	Libgdx2dCamera backgroundCamera;
	Libgdx2dCamera secondBackgroundCamera;

	Libgdx2dCamera worldCamera;
	Libgdx2dCamera guiCamera;

	Camera backgroundFollowCamera;
	Camera secondBackgroundFollowCamera;

	ResourceManager<String> resourceManager;
	AdWhirlViewHandler adWhirlViewHandler;

	Rectangle worldBounds;
	private Sprite secondBackgroundSprite;
	WorldWrapper worldWrapper;

	@Override
	public void init() {
		gl = Gdx.graphics.getGL10();

		spriteBatch = new SpriteBatch();

		// orthographicCamera = new OrthographicCamera();

		resourceManager = new ResourceManagerImpl<String>();

		new LibgdxResourceBuilder(resourceManager) {
			{
				texture("BackgroundTexture", "data/levels/superangrysheep-background.png", true);
				sprite("BackgroundSprite", "BackgroundTexture");

				texture("SecondBackgroundTexture", "data/levels/superangrysheep-background2.png", true);
				sprite("SecondBackgroundSprite", "SecondBackgroundTexture");

				texture("BombExplosionSpriteSheet", "data/animations/bomb-explosion-animation.png");
				animation("BombExplosionAnimation", "BombExplosionSpriteSheet", 0, 0, 128, 128, 15, false, 35);

				texture("BombTexture", "data/images/bomb.png", true);
				sprite("BombSprite", "BombTexture");

				texture("ButtonFireTexture", "data/gui/button-fire.png", true);
				sprite("ButtonFireSprite", "ButtonFireTexture");

				texture("ButtonTurnLeftTexture", "data/gui/button-turn-left.png", true);
				sprite("ButtonTurnLeftSprite", "ButtonTurnLeftTexture");

				texture("ButtonTurnRightTexture", "data/gui/button-turn-right.png", true);
				sprite("ButtonTurnRightSprite", "ButtonTurnRightTexture");

			}
		};

		bombExplosionSound = Gdx.audio.newSound(Gdx.files.internal("data/audio/bomb-explosion.ogg"));

		Pixmap pixmap = new Pixmap(Gdx.files.internal("data/levels/superangrysheep-level.png"));

		Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		Sprite sprite = new Sprite(texture);

		sprite.setPosition(0, 0);

		// backgroundTexture = new Texture(Gdx.files.internal("superangrysheep/superangrysheep-background.png"));
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

		backgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);
		secondBackgroundFollowCamera = new CameraRestrictedImpl(0f, 0f, 1f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), worldBounds);

		pixmapTerrain = new PixmapHelper(pixmap);

		Gdx.graphics.getGL10().glClearColor(0.5f, 0.5f, 0.5f, 0f);

		controller = new Controller();

		inputDevicesMonitor = new InputDevicesMonitorImpl<String>();
		new LibgdxInputMappingBuilder<String>(inputDevicesMonitor, Gdx.input) {
			{
				if (Gdx.app.getType() == ApplicationType.Android)
					monitorPointerDown("releaseBomb", 0);
				else {
					monitorKey("releaseBomb", Keys.SPACE);
				}
			}
		};

		bombs = new ArrayList<PlayGameState.Bomb>();
		bombExplosions = new ArrayList<BombExplosion>();

		leftButton = new LeftButton(controller);
		rightButton = new RightButton(controller);
		fireButton = new FireButton(controller);

		worldWrapper = new WorldWrapper(new World());

		RenderLayers renderLayers = new RenderLayers();
		renderLayers.add(Layers.World, new RenderLayerSpriteBatchImpl(-500, 500, worldCamera));

		EntityFactory entityFactory = new EntityFactoryImpl(worldWrapper.getWorld());

		Injector injector = new InjectorImpl();

		injector.bind("timeStepProvider", new TimeStepProviderGameStateImpl(this));
		injector.bind("renderLayers", renderLayers);
		injector.bind("entityFactory", entityFactory);
		injector.bind("resourceManager", resourceManager);

		worldWrapper.addUpdateSystem(injector.getInstance(PreviousStateSpatialSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(MovementSystem.class));
		worldWrapper.addUpdateSystem(injector.getInstance(ScriptSystem.class));

		worldWrapper.addRenderSystem(injector.getInstance(CameraUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(SpriteUpdateSystem.class));
		worldWrapper.addRenderSystem(injector.getInstance(RenderableSystem.class));

		worldWrapper.init();

		//
		// worldWrapper.addUpdateSystem(new PhysicsSystem(physicsWorld));
		// worldWrapper.addUpdateSystem(new ScriptSystem());
		// worldWrapper.addUpdateSystem(new TagSystem());
		// worldWrapper.addUpdateSystem(new ContainerSystem());
		// worldWrapper.addUpdateSystem(new OwnerSystem());
		//
		// // testing event listener auto registration using reflection
		// worldWrapper.addUpdateSystem(new ReflectionRegistratorEventSystem(eventManager));
		// worldWrapper.addUpdateSystem(injector.getInstance(SoundSpawnerSystem.class));
		//
		// worldWrapper.addRenderSystem(new CameraUpdateSystem(timeStepProvider));
		// worldWrapper.addRenderSystem(new SpriteUpdateSystem(timeStepProvider));
		// worldWrapper.addRenderSystem(new TextLocationUpdateSystem());
		// worldWrapper.addRenderSystem(new RenderableSystem(renderLayers));
		// worldWrapper.addRenderSystem(new ParticleEmitterSystem());

		EntityTemplate bombEntityTemplate = injector.getInstance(BombEntityTemplate.class);

		entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
				.put("spatial", new SpatialImpl(-200f, Gdx.graphics.getHeight() * 0.5f, 32f, 32f, 0)) //
				.put("controller", controller) //
				.put("pixmapHelper", pixmapTerrain) //
				);

	}

	@Override
	public void update() {
		super.update();

		inputDevicesMonitor.update();

		worldWrapper.update(getDeltaInMs());

		int x = Gdx.input.getX();
		int y = (Gdx.graphics.getHeight() - Gdx.input.getY());

		pixmapTerrain.project(position, x, y);

		int pixel = pixmapTerrain.getPixel(position.x, position.y);

		ColorUtils.rgba8888ToColor(color, pixel);

		// System.out.println(MessageFormat.format("({0},{1}) => ({2},{3},{4},{5}} and {6}", position.x, position.y, color.r, color.g, color.b, color.a, Integer.toHexString(pixel)));

		// System.out.println("" + x + "," + y + ": " + color + ", " + Integer.toHexString(pixel));

		if (rotate)
			pixmapTerrain.sprite.rotate(0.1f);

		// update controller

		if (Gdx.app.getType() == ApplicationType.Android) {
			leftButton.update();
			rightButton.update();
			fireButton.update();
		} else {
			controller.fire = inputDevicesMonitor.getButton("releaseBomb").isReleased();
			controller.left = Gdx.input.isKeyPressed(Keys.LEFT);
			controller.right = Gdx.input.isKeyPressed(Keys.RIGHT);
		}

		if (controller.fire) {
			PlayGameState.Bomb bomb = new Bomb();

			// bomb.soundHandle = bombSound.play();

			bomb.position.set(-200f, Gdx.graphics.getHeight() * 0.5f);
			bomb.velocity.set(200f, 0f);
			bomb.angle = 0;
			bomb.pixmapHelper = pixmapTerrain;
			bomb.controller = controller;
			bomb.explosionRadius = 30f;

			Sprite bombSprite = resourceManager.getResourceValue("BombSprite");

			// Sprite bombSprite = new Sprite(bombTexture);

			bombSprite.setSize(32f, 32f);

			bomb.setSprite(bombSprite);

			bombs.add(bomb);
		}

		float midpointx = 0f;
		float midpointy = 0f;

		for (int i = 0; i < bombs.size(); i++) {
			PlayGameState.Bomb bomb = bombs.get(i);
			bomb.update();
			if (bomb.deleted) {
				// bombSound.stop(bomb.soundHandle);
				bombsToDelete.add(bomb);
				bombExplosionSound.play();
				System.out.println("removing bomb");

				Animation animation = resourceManager.getResourceValue("BombExplosionAnimation");

				BombExplosion bombExplosion = new BombExplosion(animation);
				bombExplosion.position.set(bomb.position);

				bombExplosions.add(bombExplosion);

			}
			midpointx += bomb.position.x;
			midpointy += bomb.position.y;
		}

		for (int i = 0; i < bombExplosions.size(); i++) {
			BombExplosion bombExplosion = bombExplosions.get(i);
			bombExplosion.update();
			if (bombExplosion.animation.isFinished())
				bombExplosionsToDelete.add(bombExplosion);
		}

		if (bombs.size() >= 1) {
			midpointx /= bombs.size();
			midpointy /= bombs.size();
			worldCamera.move(midpointx, midpointy);

			backgroundFollowCamera.setPosition(midpointx / 12f, midpointy / 12f);
			secondBackgroundFollowCamera.setPosition(midpointx / 4f, midpointy / 4f);
		} else {
			// midpointx = Gdx.graphics.getWidth() * 0.5f;
			// midpointy = Gdx.graphics.getHeight() * 0.5f;
		}

		bombs.removeAll(bombsToDelete);
		bombsToDelete.clear();

		bombExplosions.removeAll(bombExplosionsToDelete);
		bombExplosionsToDelete.clear();

		backgroundCamera.zoom(backgroundFollowCamera.getZoom());
		backgroundCamera.move(backgroundFollowCamera.getX(), backgroundFollowCamera.getY());

		secondBackgroundCamera.zoom(secondBackgroundFollowCamera.getZoom());
		secondBackgroundCamera.move(secondBackgroundFollowCamera.getX(), secondBackgroundFollowCamera.getY());

	}

	@Override
	public void render() {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// orthographicCamera.apply(gl);
		//
		// spriteBatch.setProjectionMatrix(orthographicCamera.projection);
		// spriteBatch.setTransformMatrix(orthographicCamera.view);

		backgroundCamera.apply(spriteBatch);
		spriteBatch.begin();
		backgroundSprite.draw(spriteBatch);
		spriteBatch.end();

		secondBackgroundCamera.apply(spriteBatch);
		spriteBatch.begin();
		secondBackgroundSprite.draw(spriteBatch);
		spriteBatch.end();

		worldCamera.apply(spriteBatch);
		spriteBatch.begin();
		// backgroundSprite.draw(spriteBatch);
		pixmapTerrain.sprite.draw(spriteBatch);
		for (int i = 0; i < bombs.size(); i++) {
			bombs.get(i).draw(spriteBatch);
		}
		for (int i = 0; i < bombExplosions.size(); i++) {
			bombExplosions.get(i).draw(spriteBatch);
		}
		spriteBatch.end();

		guiCamera.apply(spriteBatch);
		spriteBatch.begin();
		// if (Gdx.app.getType() == ApplicationType.Android) {
		leftButton.draw(spriteBatch);
		rightButton.draw(spriteBatch);
		fireButton.draw(spriteBatch);
		// }
		spriteBatch.end();

		worldWrapper.render();
	}

	@Override
	public void resume() {
		pixmapTerrain.reloadTexture();
		adWhirlViewHandler.show();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		pixmapTerrain.dispose();
		bombExplosionSound.dispose();
		resourceManager.unloadAll();
	}

}