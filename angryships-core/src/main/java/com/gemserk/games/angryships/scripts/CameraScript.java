package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.PreviousStateCameraComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.angryships.components.CameraDistanceComponent;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.entities.Groups;

public class CameraScript extends ScriptJavaImpl {

	@Override
	public void update(World world, Entity e) {

		float midpointx = 0f;
		float midpointy = 0f;

		ImmutableBag<Entity> bombEntities = world.getGroupManager().getEntities(Groups.Bombs);

		for (int i = 0; i < bombEntities.size(); i++) {
			Entity bomb = bombEntities.get(i);

			SpatialComponent spatialComponent = Components.getSpatialComponent(bomb);
			Spatial spatial = spatialComponent.getSpatial();

			midpointx += spatial.getX();
			midpointy += spatial.getY();
		}

		CameraComponent cameraComponent = Components.getCameraComponent(e);
		Camera camera = cameraComponent.getCamera();

		// store previous camera state, to be used for interpolation
		PreviousStateCameraComponent previousStateCameraComponent = Components.getPreviousStateCameraComponent(e);

		if (previousStateCameraComponent != null) {
			Camera previousCamera = previousStateCameraComponent.getCamera();
			previousCamera.setPosition(camera.getX(), camera.getY());
			previousCamera.setAngle(camera.getAngle());
			previousCamera.setZoom(camera.getZoom());
		}
		
		CameraDistanceComponent cameraDistanceComponent = Components.getCameraDistanceComponent(e);
		
		if (bombEntities.size() >= 1) {
			midpointx /= bombEntities.size();
			midpointy /= bombEntities.size();
			camera.setPosition(midpointx / cameraDistanceComponent.distance, midpointy / cameraDistanceComponent.distance);
		}
		
		Libgdx2dCamera libgdx2dCamera = cameraComponent.getLibgdx2dCamera();
		
		libgdx2dCamera.zoom(camera.getZoom());
		libgdx2dCamera.move(camera.getX(), camera.getY());
		libgdx2dCamera.rotate(camera.getAngle());
		
	}

}
