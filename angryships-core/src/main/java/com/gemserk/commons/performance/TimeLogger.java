package com.gemserk.commons.performance;

import com.badlogic.gdx.utils.FloatArray;

public abstract class TimeLogger {

	private boolean enabled = false;
	public FloatArray deltas = new FloatArray(false, 60 * 60 * 10);

	public void update() {
		if (!enabled)
			return;
		registerData();
	}

	protected abstract void registerData();

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public FloatArray getDeltas() {
		return deltas;
	}

	public void clear() {
		deltas.clear();
	}
}