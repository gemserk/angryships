package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.box2d.Contacts;
import com.gemserk.games.angryships.entities.Events;

public class ExplodeWhenCollisionScript extends ScriptJavaImpl {
	
	EventManager eventManager;
	
	@Override
	public void update(World world, Entity e) {
		
		PhysicsComponent physicsComponent = Components.physicsComponent(e);
		Contacts contacts = physicsComponent.getContact();
		
		if (!contacts.isInContact())
			return;
		
		e.delete();
		eventManager.registerEvent(Events.explosion, e);
		
	}
	
}