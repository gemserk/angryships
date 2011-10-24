package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.entities.Events;

public class AutoExplodeScript extends ScriptJavaImpl {
	
	EventManager eventManager;
	
	@Override
	public void update(World world, Entity e) {

		ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
		
		if (!controllerComponent.controller.explode)
			return;
		
		e.delete();
		eventManager.registerEvent(Events.explosion, e);
		
	}
	
}