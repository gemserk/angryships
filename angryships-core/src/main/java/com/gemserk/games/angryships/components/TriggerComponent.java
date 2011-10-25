package com.gemserk.games.angryships.components;

import com.artemis.Component;

public class TriggerComponent extends Component {
	
	Trigger trigger;
	
	public Trigger getTrigger() {
		return trigger;
	}

	public TriggerComponent(Trigger trigger) {
		this.trigger = trigger;
	}

}
