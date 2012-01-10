package com.gemserk.commons.performance;

import com.badlogic.gdx.Gdx;

public class DeltaTimeLogger extends TimeLogger {

	@Override
	protected void registerData() {
		deltas.add(Gdx.graphics.getDeltaTime());
	}

}