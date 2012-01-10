package com.gemserk.commons;

import java.awt.Toolkit;

import org.junit.Test;

public class DensityTests {

	public float getPpiX() {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public float getPpiY() {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public float getPpcX() {
		return (Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f);
	}

	public float getPpcY() {
		return (Toolkit.getDefaultToolkit().getScreenResolution() / 2.54f);
	}

	public float getDensity() {
		return (Toolkit.getDefaultToolkit().getScreenResolution() / 160f);
	}

	@Test
	public void test() {

		System.out.println("ppix : " + getPpiX());
		System.out.println("ppiy : " + getPpiY());

		System.out.println("ppcx : " + getPpcX());
		System.out.println("ppcy : " + getPpcY());
		
		System.out.println("density : " + getDensity());
		
		System.out.println("size : " + Toolkit.getDefaultToolkit().getScreenSize());
		
		System.out.println("size : " + (800f/480f));
		System.out.println("size : " + (480f/320f));
		System.out.println("size : " + (320f/240f));
	}

}
