package com.gemserk.games.angryships.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.games.angryships.components.Trigger;
import com.gemserk.games.angryships.components.TriggerComponent;

public class TimerTriggerSystem extends EntityProcessingSystem {
	
	private static final Class<TimerComponent> timerComponentClass = TimerComponent.class;
	private static final Class<TriggerComponent> triggerComponentClass = TriggerComponent.class;

	EventManager eventManager;
	
	public TimerTriggerSystem() {
		super(timerComponentClass, triggerComponentClass);
	}
	
	@Override
	protected void process(Entity e) {
		TimerComponent timerComponent = e.getComponent(timerComponentClass);
		
		if (!timerComponent.isFinished())
			return;
		
		TriggerComponent triggerComponent = e.getComponent(triggerComponentClass);
		Trigger trigger = triggerComponent.getTrigger();
		
		if (trigger.triggered)
			return;
		
		eventManager.registerEvent(trigger.eventId, trigger);
		
		trigger.triggered = true;
	}


}
