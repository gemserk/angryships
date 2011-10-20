package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.box2d.BodyBuilder;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;
import com.gemserk.commons.gdx.games.Physics;
import com.gemserk.commons.gdx.games.SpatialPhysicsImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.entities.Collisions;

public class AreaTriggerTemplate extends EntityTemplateImpl {

	public static class RemoveEntitiesScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {
			PhysicsComponent physicsComponent = Components.physicsComponent(e);
			Physics physics = physicsComponent.getPhysics();

			Contacts contacts = physics.getContact();
			if (!contacts.isInContact())
				return;

			for (int i = 0; i < contacts.getContactCount(); i++) {
				Contact contact = contacts.getContact(i);
				Entity otherEntity = (Entity) contact.getOtherFixture().getBody().getUserData();
				if (otherEntity == null)
					continue;
				otherEntity.delete();
			}
		}

	}

	Injector injector;
	BodyBuilder bodyBuilder;

	@Override
	public void apply(Entity entity) {

		Rectangle area = parameters.get("area");

		float cx = area.x + area.width * 0.5f;
		float cy = area.y + area.height * 0.5f;

		Body body = bodyBuilder //
				.fixture(bodyBuilder.fixtureDefBuilder() //
						.boxShape(area.width * 0.5f, area.height * 0.5f) //
						.categoryBits(Collisions.AreaTrigger) //
						.maskBits(Collisions.All) //
						.sensor()) //
				.type(BodyType.StaticBody).position(cx, cy).userData(entity) //
				.build();

		entity.addComponent(new PhysicsComponent(body));
		entity.addComponent(new SpatialComponent(new SpatialPhysicsImpl(body, 1f, 1f)));

		entity.addComponent(new ScriptComponent(injector.getInstance(RemoveEntitiesScript.class)));
	}
}
