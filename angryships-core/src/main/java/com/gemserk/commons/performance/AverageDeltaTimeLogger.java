package com.gemserk.commons.performance;

import com.badlogic.gdx.Gdx;

public class AverageDeltaTimeLogger extends TimeLogger {
	float average = 1f / 60f;
	int number = 1;

	@Override
	protected void registerData() {
		average += Gdx.graphics.getDeltaTime();
		number++;
		deltas.add(average / number);
	}
}