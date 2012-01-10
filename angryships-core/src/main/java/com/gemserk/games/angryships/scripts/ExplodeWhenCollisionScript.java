package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.commons.gdx.box2d.Contacts.Contact;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.entities.Events;

public class ExplodeWhenCollisionScript extends ScriptJavaImpl {

	EventManager eventManager;

	@Override
	public void update(World world, Entity e) {

		PhysicsComponent physicsComponent = Components.getPhysicsComponent(e);
		Contacts contacts = physicsComponent.getContact();

		if (!contacts.isInContact())
			return;

		boolean sensor = true;

		for (int i = 0; i < contacts.getContactCount(); i++) {
			Contact contact = contacts.getContact(i);
			String type = (String) contact.getOtherFixture().getUserData();

			// if (type == Collisions.RemoveAreaType)
			// continue;

			sensor = false;
			break;
		}

		if (sensor)
			return;

		ExplosionComponent explosionComponent = Components.getExplosionComponent(e);
		explosionComponent.exploded = true;
		
		// e.delete();
		eventManager.registerEvent(Events.explosion, e);

	}

}