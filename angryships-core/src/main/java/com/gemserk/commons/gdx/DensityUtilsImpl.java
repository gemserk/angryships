package com.gemserk.commons.gdx;

import com.badlogic.gdx.Gdx;

public class DensityUtilsImpl implements DensityUtils {
	
	public Density getDensity() {

		float d = Gdx.graphics.getDensity();
		
		if (d < 1f)
			return Density.Low;
		else if (d > 1f && d < 1.5f)
			return Density.Medium;
		else if (d > 1.5f && d < 2f)
			return Density.High;
		else
			return Density.ExtraHigh;

	}

}
