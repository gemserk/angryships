package com.gemserk.games.angryships.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.commons.artemis.components.TimerComponent;
import com.gemserk.commons.gdx.GlobalTime;

public class TimerUpdateSystem extends EntityProcessingSystem {
	
	private static final Class<TimerComponent> timerComponentClass = TimerComponent.class;

	public TimerUpdateSystem() {
		super(timerComponentClass);
	}
	
	@Override
	protected void process(Entity e) {
		TimerComponent timerComponent = e.getComponent(timerComponentClass);
		if (timerComponent.isFinished())
			return;
		timerComponent.setCurrentTime(timerComponent.getCurrentTime() - GlobalTime.getDelta());
	}


}
