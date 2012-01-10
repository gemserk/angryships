package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.components.GroupComponent;
import com.gemserk.games.angryships.components.PixmapCollidableComponent;
import com.gemserk.games.angryships.entities.Collisions;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.ExplodeWhenCollisionScript;
import com.gemserk.games.angryships.scripts.PixmapCollidableScript;
import com.gemserk.resources.ResourceManager;

public class ClusterBombMunitionTemplate extends EntityTemplateImpl {

	ResourceManager<String> resourceManager;
	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.BombSprite);

		// entity.setGroup(Groups.Bombs);
		entity.addComponent(new GroupComponent(Groups.Bombs));

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(0.25f) //
						.categoryBits(Collisions.Bomb) //
						// .maskBits(Collisions.None) //
						.maskBits((short) (Collisions.Target | Collisions.AreaTrigger)) //
						.sensor() //
				) //
				.position(spatial.getX(), spatial.getY()) //
				.angle(0f) //
				.userData(entity) //
				.type(BodyType.DynamicBody) //
				.build();

		body.setActive(false);

		entity.addComponent(new PhysicsComponent(body));

		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));

		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		// entity.addComponent(new PreviousStateSpatialComponent());

		entity.addComponent(new PixmapCollidableComponent());
		entity.addComponent(new ExplosionComponent(injector.getInstance(ExplosionAnimationTemplate.class), 0.5f));

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(PixmapCollidableScript.class), //
				injector.getInstance(ExplodeWhenCollisionScript.class) //
		));

	}

}
