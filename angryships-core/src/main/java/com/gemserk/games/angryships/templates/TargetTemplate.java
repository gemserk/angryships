package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
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
import com.gemserk.games.angryships.entities.Collisions;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.ExplodeWhenCollisionScript;
import com.gemserk.games.angryships.scripts.UpdateAnimationScript;
import com.gemserk.resources.ResourceManager;

public class TargetTemplate extends EntityTemplateImpl {
	
	ResourceManager<String> resourceManager;
	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Animation idleAnimation = resourceManager.getResourceValue(GameResources.Animations.BarrelIdleAnimation);

		entity.setGroup(Groups.Targets);
		
		Body body = bodyBuilder //
			.fixture(bodyBuilder.fixtureDefBuilder() //
					.circleShape(16f) //
					.categoryBits(Collisions.Target) //
					.maskBits((short)(Collisions.Bomb | Collisions.Explosion)) //
					.sensor() //
					) //
			.position(spatial.getX(), spatial.getY()) //
			.angle(0f) //
			.userData(entity) //
			.type(BodyType.DynamicBody) //
			.build();
		
		entity.addComponent(new PhysicsComponent(body));
		
		entity.addComponent(new RenderableComponent(5));
		entity.addComponent(new SpriteComponent(idleAnimation.getCurrentFrame(), 0.5f, 0.5f, Color.WHITE));
		
		entity.addComponent(new AnimationComponent(new Animation[] {idleAnimation}));
		entity.addComponent(new ExplosionComponent(injector.getInstance(ExplosionAnimationTemplate.class), 30f));

		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));

		entity.addComponent(new ScriptComponent(injector.getInstance(UpdateAnimationScript.class), injector.getInstance(ExplodeWhenCollisionScript.class)));
	}

}
