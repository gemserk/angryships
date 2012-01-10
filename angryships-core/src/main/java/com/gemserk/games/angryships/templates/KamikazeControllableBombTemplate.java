package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.AntiGravityComponent;
import com.gemserk.commons.artemis.components.ContainerComponent;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Movement;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.components.PixmapCollidableComponent;
import com.gemserk.games.angryships.entities.Collisions;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.AutoExplodeScript;
import com.gemserk.games.angryships.scripts.ExplodeWhenCollisionScript;
import com.gemserk.games.angryships.scripts.MovementScript;
import com.gemserk.games.angryships.scripts.PixmapCollidableScript;
import com.gemserk.games.angryships.scripts.RemoveWhenExplodedScript;
import com.gemserk.resources.ResourceManager;

public class KamikazeControllableBombTemplate extends EntityTemplateImpl {

	public static class SpawnKamikazeBombScript extends ScriptJavaImpl {

		private final Parameters parameters = new ParametersWrapper();

		EntityFactory entityFactory;
		Injector injector;

		@Override
		public void update(World world, Entity e) {

			ControllerComponent controllerComponent = Components.getControllerComponent(e);
			Controller controller = controllerComponent.controller;

			if (!controller.explode)
				return;

			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			Spatial spatial = spatialComponent.getSpatial();

			MovementComponent movementComponent = Components.getMovementComponent(e);
			Movement movement = movementComponent.getMovement();

			EntityTemplate kamikazeBombTemplate = injector.getInstance(KamikazeBombTemplate.class);

			Entity kamikazeBomb = entityFactory.instantiate(kamikazeBombTemplate, parameters //
					.put("spatial", spatial) //
					);

			MovementComponent kamikazeMovementComponent = Components.getMovementComponent(kamikazeBomb);

			kamikazeMovementComponent.getMovement().getLinearVelocity().rotate(movement.getLinearVelocity().angle());

			e.delete();
		}

	}

	private static final Vector2 velocity = new Vector2();

	ResourceManager<String> resourceManager;
	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		Controller controller = parameters.get("controller");

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.BombSprite);

		entity.setGroup(Groups.Bombs);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(0.25f) //
						.categoryBits(Collisions.Bomb) //
						.maskBits((short) (Collisions.Target | Collisions.Explosion | Collisions.AreaTrigger)) //
						.sensor() //
				) //
				.position(spatial.getX(), spatial.getY()) //
				.angle(0f) //
				.userData(entity) //
				.type(BodyType.DynamicBody) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new AntiGravityComponent());

		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));

		entity.addComponent(new ExplosionComponent(injector.getInstance(ExplosionAnimationTemplate.class), 0.5f));

		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		velocity.set(4f, 0f);
		velocity.rotate(spatial.getAngle());

		entity.addComponent(new MovementComponent(velocity.x, velocity.y, 0f));

		entity.addComponent(new ControllerComponent(controller, 180f));

		entity.addComponent(new PixmapCollidableComponent());

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(MovementScript.class), //
				injector.getInstance(AutoExplodeScript.class), //
				injector.getInstance(PixmapCollidableScript.class), //
				injector.getInstance(SpawnKamikazeBombScript.class), //
				injector.getInstance(ExplodeWhenCollisionScript.class), //
				injector.getInstance(RemoveWhenExplodedScript.class) //
		));

		entity.addComponent(new ContainerComponent());
	}

}
