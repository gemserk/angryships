package com.gemserk.commons.gdx;


public class DensityUtilsFixedImpl implements DensityUtils {
	
	private final Density density;

	public DensityUtilsFixedImpl(Density density) {
		this.density = density;
	}
	
	public Density getDensity() {
		return density;
	}

}
