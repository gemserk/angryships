package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.entities.Groups;

public class GameModeNormalScript extends ScriptJavaImpl {
	
	EventManager eventManager;

	@Override
	public void update(com.artemis.World world, Entity e) {
		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(Groups.Targets);
		if (targets.size() > 0)
			return;
		
		eventManager.registerEvent(Events.gameOver, e);
		
		e.delete();
	}

}