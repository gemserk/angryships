package com.gemserk.games.angryships.components;

public class Trigger {

	/**
	 * The event to register when this trigger is triggered.
	 */
	public String eventId;

	/**
	 * If the trigger was triggered.
	 */
	public boolean triggered;

	public Trigger(String eventId) {
		this.eventId = eventId;
		this.triggered = false;
	}

}
