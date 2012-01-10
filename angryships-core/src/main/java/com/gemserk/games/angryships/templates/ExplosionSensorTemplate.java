package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.AliveComponent;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.entities.Collisions;
import com.gemserk.resources.ResourceManager;

public class ExplosionSensorTemplate extends EntityTemplateImpl {

	public static class AliveScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {
			super.update(world, e);
			AliveComponent aliveComponent = e.getComponent(AliveComponent.class);
			aliveComponent.setTime(aliveComponent.getTime() - GlobalTime.getDelta());
			if (aliveComponent.getTime() <= 0) {
				e.delete();
			}
		}

	}

	ResourceManager<String> resourceManager;
	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.circleShape(spatial.getWidth()) //
						.categoryBits(Collisions.Explosion) //
						.maskBits(Collisions.All) //
						.sensor() //
						, Collisions.ExplosionType) //
				.position(spatial.getX(), spatial.getY()) //
				.angle(0f) //
				.userData(entity) //
				.type(BodyType.DynamicBody) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new AliveComponent(0.001f));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, spatial)));
		entity.addComponent(new ScriptComponent(injector.getInstance(AliveScript.class)));
	}

}
