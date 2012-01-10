package com.gemserk.games.angryships.components;

import com.artemis.ComponentType;
import com.artemis.ComponentTypeManager;
import com.artemis.Entity;

public class Components extends com.gemserk.commons.artemis.components.Components {

	public static final Class<CameraDistanceComponent> cameraDistanceComponentClass = CameraDistanceComponent.class;
	public static final ComponentType cameraDistanceComponentType = ComponentTypeManager.getTypeFor(cameraDistanceComponentClass);

	public static final Class<ControllerComponent> controllerComponentClass = ControllerComponent.class;
	public static final ComponentType controllerComponentType = ComponentTypeManager.getTypeFor(controllerComponentClass);
	
	public static final Class<PixmapCollidableComponent> pixmapCollidableComponentClass = PixmapCollidableComponent.class;
	public static final ComponentType pixmapCollidableComponentType = ComponentTypeManager.getTypeFor(pixmapCollidableComponentClass);
	
	public static final Class<ExplosionComponent> explosionComponentClass = ExplosionComponent.class;
	public static final ComponentType explosionComponentType = ComponentTypeManager.getTypeFor(explosionComponentClass);
	
	public static final Class<ClusterBombComponent> clusterBombComponentClass = ClusterBombComponent.class;
	public static final ComponentType clusterBombComponentType = ComponentTypeManager.getTypeFor(clusterBombComponentClass);

	public static ControllerComponent getControllerComponent(Entity e) {
		return controllerComponentClass.cast(e.getComponent(controllerComponentType));
	}

	public static PixmapCollidableComponent getPixmapCollidableComponent(Entity e) {
		return pixmapCollidableComponentClass.cast(e.getComponent(pixmapCollidableComponentType));
	}

	public static ExplosionComponent getExplosionComponent(Entity e) {
		return explosionComponentClass.cast(e.getComponent(explosionComponentType));
	}

	public static CameraDistanceComponent getCameraDistanceComponent(Entity e) {
		return cameraDistanceComponentClass.cast(e.getComponent(cameraDistanceComponentType));
	}

	public static ClusterBombComponent getClusterBombComponent(Entity e) {
		return clusterBombComponentClass.cast(e.getComponent(clusterBombComponentType));
	}

	public static final Class<GroupComponent> groupComponentClass = GroupComponent.class;
	public static final ComponentType groupComponentType = ComponentTypeManager.getTypeFor(groupComponentClass);
	
	public static GroupComponent getGroupComponent(Entity e) {
		return groupComponentClass.cast(e.getComponent(groupComponentType));
	}

}
