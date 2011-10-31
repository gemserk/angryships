package com.gemserk.games.angryships.systems;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.systems.OrderedByLayerEntities;
import com.gemserk.commons.artemis.systems.RenderLayer;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.games.angryships.components.ParticleEmitterComponent;

public class RenderLayerParticleEmitterImpl implements RenderLayer {

	private static final Class<RenderableComponent> renderableComponentClass = RenderableComponent.class;
	private static final Class<ParticleEmitterComponent> particleEmitterComponentClass = ParticleEmitterComponent.class;
	private static final Class<SpatialComponent> spatialComponentClass = SpatialComponent.class;

	private final Libgdx2dCamera camera;
	private final OrderedByLayerEntities orderedByLayerEntities;
	private boolean enabled;
	private SpriteBatch spriteBatch;

	public RenderLayerParticleEmitterImpl(int minLayer, int maxLayer, Libgdx2dCamera camera) {
		this.camera = camera;
		this.orderedByLayerEntities = new OrderedByLayerEntities(minLayer, maxLayer);
		this.enabled = true;
		this.spriteBatch = new SpriteBatch();
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public boolean belongs(Entity e) {
		RenderableComponent renderableComponent = e.getComponent(renderableComponentClass);
		if (renderableComponent == null)
			return false;

		ParticleEmitterComponent particleEmitterComponent = e.getComponent(particleEmitterComponentClass);
		if (particleEmitterComponent == null)
			return false;
		
		SpatialComponent spatialComponent = e.getComponent(spatialComponentClass);
		if (spatialComponent == null)
			return false;
		
		return orderedByLayerEntities.belongs(renderableComponent.getLayer());
	}

	@Override
	public void add(Entity entity) {
		orderedByLayerEntities.add(entity);
	}

	@Override
	public void remove(Entity entity) {
		orderedByLayerEntities.remove(entity);
	}

	@Override
	public void render() {
		camera.apply(spriteBatch);
		spriteBatch.begin();
		for (int i = 0; i < orderedByLayerEntities.size(); i++) {
			Entity entity = orderedByLayerEntities.get(i);
			RenderableComponent renderableComponent = entity.getComponent(renderableComponentClass);
			if (!renderableComponent.isVisible())
				continue;
			ParticleEmitterComponent particleEmitterComponent = entity.getComponent(particleEmitterComponentClass);
			particleEmitterComponent.particleEmitter.draw(spriteBatch);
		}
		spriteBatch.end();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
