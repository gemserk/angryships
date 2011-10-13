package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.MovementComponent;
import com.gemserk.commons.artemis.components.PreviousStateSpatialComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Movement;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.resources.ResourceManager;

public class BombEntityTemplate extends EntityTemplateImpl {
	
	static class MovementScript extends ScriptJavaImpl {
		
		@Override
		public void update(World world, Entity e) {

			MovementComponent movementComponent = Components.getMovementComponent(e);
			SpatialComponent spatialComponent = Components.spatialComponent(e);
			ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
			
			Controller controller = controllerComponent.controller;
			Movement movement = movementComponent.getMovement();
			Spatial spatial = spatialComponent.getSpatial();
			
			float rotationAngle = 360f * GlobalTime.getDelta();
			
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

	ResourceManager<String> resourceManager;

	@Override
	public void apply(Entity entity) {
		
		Spatial spatial = parameters.get("spatial");
		Controller controller = parameters.get("controller");
		
		Sprite sprite = resourceManager.getResourceValue(GameResources.Sprites.BombSprite);
		
		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new PreviousStateSpatialComponent());
		
		entity.addComponent(new MovementComponent(150f, 0f, 0f));

		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent(new MovementScript()));
		
	}

}
