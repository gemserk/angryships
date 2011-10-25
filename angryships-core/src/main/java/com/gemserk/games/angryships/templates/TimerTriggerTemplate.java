package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.games.angryships.components.Trigger;
import com.gemserk.games.angryships.components.TriggerComponent;

public class TimerTriggerTemplate extends EntityTemplateImpl {
	
	EventManager eventManager;
	
	@Override
	public void apply(Entity entity) {
		Float time = parameters.get("time");
		String eventId = parameters.get("eventId");
		entity.addComponent(new TimerComponent(time));
		entity.addComponent(new TriggerComponent(new Trigger(eventId)));
	}
	
}