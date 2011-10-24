package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.artemis.components.AntiGravityComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.components.PixmapCollidableComponent;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.entities.Collisions;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.ExplodeWhenCollisionScript;
import com.gemserk.prototypes.pixmap.PixmapHelper;
import com.gemserk.resources.ResourceManager;

public class KamikazeBombTemplate extends EntityTemplateImpl {

	public static class ErasePixmapWhenCollisionScript extends ScriptJavaImpl {

		private static final Vector2 position = new Vector2();

		PixmapWorld pixmapWorld;

		@Override
		public void update(World world, Entity e) {

			SpatialComponent spatialComponent = Components.spatialComponent(e);
			Spatial spatial = spatialComponent.getSpatial();

			Array<PixmapHelper> pixmaps = pixmapWorld.getPixmaps();
			
			for (int i = 0; i < pixmaps.size; i++) {
				PixmapHelper pixmapHelper = pixmaps.get(i);
				// PixmapHelper pixmapHelper = pixmapCollision.getContact(i);
				pixmapHelper.project(position, spatial.getX(), spatial.getY());
				pixmapHelper.eraseCircle(position.x, position.y, spatial.getWidth() * 0.5f);
			}

		}

	}

	ResourceManager<String> resourceManager;
	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.BombSprite);

		entity.setGroup(Groups.Bombs);

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(0.25f) //
						.categoryBits(Collisions.Bomb) //
						.maskBits((short) (Collisions.Target | Collisions.AreaTrigger)) //
						.sensor() //
				) //
				.position(spatial.getX(), spatial.getY()) //
				.angle(0f) //
				.userData(entity) //
				.type(BodyType.DynamicBody) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new AntiGravityComponent());

		entity.addComponent(new MovementComponent(15f, 0f, 0f));
		
		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));

		entity.addComponent(new ExplosionComponent(injector.getInstance(ExplosionAnimationTemplate.class), 0.25f));

		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		entity.addComponent(new PixmapCollidableComponent());

		entity.addComponent(new ScriptComponent( //
				 injector.getInstance(ErasePixmapWhenCollisionScript.class), //
				injector.getInstance(ExplodeWhenCollisionScript.class) //
		));
	}

}
