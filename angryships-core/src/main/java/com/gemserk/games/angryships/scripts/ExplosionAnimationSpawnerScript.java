package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.utils.EntityStore;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.commons.utils.StoreFactory;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.templates.ExplosionAnimationTemplate;

public class ExplosionAnimationSpawnerScript extends ScriptJavaImpl {

	private final Parameters parameters = new ParametersWrapper();

	EntityFactory entityFactory;
	Injector injector;

	EntityStore explosionAnimationStore = new EntityStore(new StoreFactory<Entity>() {
		@Override
		public Entity createObject() {
			return entityFactory.instantiate(explosionAnimationTemplate, parameters.put("spatial", new SpatialImpl(0f, 0f)).put("radius", 1f));
		}
	});

	private ExplosionAnimationTemplate explosionAnimationTemplate;

	public void init(com.artemis.World world, Entity e) {
		explosionAnimationTemplate = injector.getInstance(ExplosionAnimationTemplate.class);
		explosionAnimationStore.preCreate(10);
	}

	@Override
	public void update(World world, Entity e) {
		for (int i = 0; i < explosionAnimationStore.size(); i++) {
			Entity explosionAnimation = explosionAnimationStore.get(i);
			AnimationComponent animationComponent = Components.getAnimationComponent(explosionAnimation);
			if (animationComponent.getCurrentAnimation().isFinished())
				explosionAnimationStore.free(explosionAnimation);
		}
	}

	@Handles(ids = Events.explosion)
	public void explosion(Event event) {
		Entity e = (Entity) event.getSource();

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = spatialComponent.getSpatial();

		ExplosionComponent explosionComponent = Components.getExplosionComponent(e);

		Entity explosionAnimation = explosionAnimationStore.get();
		Spatial explosionAnimationSpatial = Components.getSpatialComponent(explosionAnimation).getSpatial();
		explosionAnimationSpatial.set(spatial);

		int newRadius = Math.round(explosionComponent.radius * 4f);

		explosionAnimationSpatial.setSize(newRadius, newRadius);
		explosionAnimationSpatial.setAngle(MathUtils.random(0f, 360f));
		
		AnimationComponent animationComponent = Components.getAnimationComponent(explosionAnimation);
		animationComponent.getCurrentAnimation().restart();
	}

}