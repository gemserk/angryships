package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.PixmapCollidableComponent;
import com.gemserk.games.angryships.components.PixmapCollision;
import com.gemserk.games.angryships.entities.Events;

public class PixmapCollidableScript extends ScriptJavaImpl {

	EventManager eventManager;

	@Override
	public void update(World world, Entity e) {
		PixmapCollidableComponent pixmapCollidableComponent = Components.getPixmapCollidableComponent(e);
		PixmapCollision pixmapCollision = pixmapCollidableComponent.pixmapCollision;

		if (!pixmapCollision.isInContact())
			return;

		e.delete();

		eventManager.registerEvent(Events.explosion, e);
	}

}