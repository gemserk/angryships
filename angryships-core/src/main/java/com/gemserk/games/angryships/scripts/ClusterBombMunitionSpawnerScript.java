package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
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
import com.gemserk.games.angryships.components.ClusterBombComponent;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.templates.ClusterBombMunitionTemplate;

public class ClusterBombMunitionSpawnerScript extends ScriptJavaImpl {

	EventManager eventManager;
	EntityFactory entityFactory;
	Injector injector;

	private final Parameters parameters = new ParametersWrapper();
	private ClusterBombMunitionTemplate clusterBombMunitionTemplate;

	EntityStore clusterBombStore = new EntityStore(new StoreFactory<Entity>() {
		@Override
		public Entity createObject() {
			return entityFactory.instantiate(clusterBombMunitionTemplate, parameters.put("spatial", new SpatialImpl(0f, 0f)));
		}
	});

	public void init(World world, Entity e) {
		clusterBombMunitionTemplate = injector.getInstance(ClusterBombMunitionTemplate.class);
		clusterBombStore.preCreate(10);
	}

	@Override
	public void update(World world, Entity e) {
		for (int i = 0; i < clusterBombStore.size(); i++) {
			Entity entity = clusterBombStore.get(i);
			ExplosionComponent explosionComponent = Components.getExplosionComponent(entity);
			if (explosionComponent.exploded)
				clusterBombStore.free(entity);
		}
	}

	@Handles(ids = Events.explosion)
	public void explosion(Event event) {
		Entity e = (Entity) event.getSource();

		ClusterBombComponent clusterBombComponent = Components.getClusterBombComponent(e);

		if (clusterBombComponent == null)
			return;

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);

		Spatial spatial = spatialComponent.getSpatial();

		for (int i = 0; i < clusterBombComponent.count; i++) {
			Entity clusterBombMunition = clusterBombStore.get();

			ExplosionComponent explosionComponent = Components.getExplosionComponent(clusterBombMunition);
			explosionComponent.exploded = false;

			// Entity clusterBombMunition = entityFactory.instantiate(clusterBombMunitionTemplate, parameters //
			// .put("spatial", new SpatialImpl(spatial.getX(), spatial.getY(), 0.4f, 0.4f, MathUtils.random(0f, 360f))) //
			// );
			Spatial clusterBombMunitionSpatial = Components.getSpatialComponent(clusterBombMunition).getSpatial();

			clusterBombMunitionSpatial.setPosition(spatial.getX(), spatial.getY());
			clusterBombMunitionSpatial.setSize(0.4f, 0.4f);
			clusterBombMunitionSpatial.setAngle(MathUtils.random(0f, 360f));

			PhysicsComponent physicsComponent = Components.getPhysicsComponent(clusterBombMunition);
			Body body = physicsComponent.getBody();
			body.setLinearVelocity(0f, 0f);
			body.applyLinearImpulse(MathUtils.random(-3f, 3f), 0f, body.getPosition().x, body.getPosition().y);

			// Box2dUtils.setFilter(body, Collisions.Bomb, (short) (Collisions.Target | Collisions.AreaTrigger), (short) 0);
		}

	}

}