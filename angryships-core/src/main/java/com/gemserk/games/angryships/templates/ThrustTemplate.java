package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.OwnerComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.GlobalTime;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialHierarchicalImpl;
import com.gemserk.games.angryships.components.ParticleEmitterComponent;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.resources.ResourceManager;

public class ThrustTemplate extends EntityTemplateImpl {

	@Deprecated
	public static class UpdateParticleEmitterScript extends ScriptJavaImpl {

		@Override
		public void update(World world, Entity e) {
			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			Spatial spatial = spatialComponent.getSpatial();

			ParticleEmitterComponent particleEmitterComponent = e.getComponent(ParticleEmitterComponent.class);

			particleEmitterComponent.particleEmitter.update(GlobalTime.getDelta());
			particleEmitterComponent.particleEmitter.setPosition(spatial.getX(), spatial.getY());
		}

	}

	ResourceManager<String> resourceManager;

	@Override
	public void apply(Entity entity) {
		Entity owner = parameters.get("owner");

		SpatialComponent spatialComponent = Components.getSpatialComponent(owner);
		Spatial ownerSpatial = spatialComponent.getSpatial();

		ParticleEmitter thrustEmitter = resourceManager.getResourceValue(GameResources.ParticleEmitters.ThrustEmitter);

		SpatialHierarchicalImpl particleSpatial = new SpatialHierarchicalImpl(ownerSpatial);
		particleSpatial.setPosition(ownerSpatial.getX() - 0.25f, ownerSpatial.getY());

		entity.addComponent(new OwnerComponent(owner));
		entity.addComponent(new SpatialComponent(particleSpatial));

		entity.addComponent(new RenderableComponent(-450));
		entity.addComponent(new ParticleEmitterComponent(thrustEmitter));

		entity.addComponent(new ScriptComponent(new UpdateParticleEmitterScript()));
	}

}
