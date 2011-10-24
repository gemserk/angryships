package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Movement;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.gamestates.Controller;

public class MovementScript extends ScriptJavaImpl {
	
	@Override
	public void update(World world, Entity e) {

		MovementComponent movementComponent = Components.getMovementComponent(e);
		SpatialComponent spatialComponent = Components.spatialComponent(e);
		ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
		
		Controller controller = controllerComponent.controller;
		Movement movement = movementComponent.getMovement();
		Spatial spatial = spatialComponent.getSpatial();
		
		float rotationAngle = controllerComponent.rotationSpeed * GlobalTime.getDelta();
		
		Vector2 linearVelocity = movement.getLinearVelocity();
		
		if (controller.left) {
			spatial.setAngle(spatial.getAngle() + rotationAngle);
			linearVelocity.rotate(rotationAngle);
		} else if (controller.right) {
			spatial.setAngle(spatial.getAngle() - rotationAngle);
			linearVelocity.rotate(-rotationAngle);
		}
		
	}
	
}