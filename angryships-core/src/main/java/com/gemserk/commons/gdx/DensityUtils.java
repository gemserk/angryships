package com.gemserk.commons.gdx;


public interface DensityUtils {

	public static enum Density {

		Low, Medium, High, ExtraHigh

	}
	
	Density getDensity();
	
}
