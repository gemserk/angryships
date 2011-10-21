package com.gemserk.games.angryships.components;

import com.artemis.Entity;

public class GameComponents {

	public static final Class<ControllerComponent> controllerComponentClass = ControllerComponent.class;
	public static final Class<PixmapCollidableComponent> pixmapCollidableComponentClass = PixmapCollidableComponent.class;
	public static final Class<ExplosionComponent> explosionComponentClass = ExplosionComponent.class;
	public static final Class<CameraDistanceComponent> cameraDistanceComponentClass = CameraDistanceComponent.class;
	public static final Class<ClusterBombComponent> clusterBombComponentClass = ClusterBombComponent.class;

	public static ControllerComponent getControllerComponent(Entity e) {
		return e.getComponent(controllerComponentClass);
	}
	
	public static PixmapCollidableComponent getPixmapCollidableComponent(Entity e) {
		return e.getComponent(pixmapCollidableComponentClass);
	}

	public static ExplosionComponent getExplosionComponent(Entity e) {
		return e.getComponent(explosionComponentClass);
	}
	
	public static CameraDistanceComponent getCameraDistanceComponent(Entity e) {
		return e.getComponent(cameraDistanceComponentClass);
	}
	
	public static ClusterBombComponent getClusterBombComponent(Entity e) {
		return e.getComponent(clusterBombComponentClass);
	}
	
}
