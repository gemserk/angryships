package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.ClusterBombComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.templates.ClusterBombMunitionTemplate;

public class ClusterBombScript extends ScriptJavaImpl {

	EventManager eventManager;
	EntityFactory entityFactory;
	Injector injector;

	private final Parameters parameters = new ParametersWrapper();

	@Handles(ids = Events.explosion)
	public void explosion(Event event) {

		Entity e = (Entity) event.getSource();

		ClusterBombComponent clusterBombComponent = GameComponents.getClusterBombComponent(e);

		if (clusterBombComponent == null)
			return;

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);

		Spatial spatial = spatialComponent.getSpatial();

		ClusterBombMunitionTemplate clusterBombMunitionTemplate = injector.getInstance(ClusterBombMunitionTemplate.class);

		for (int i = 0; i < clusterBombComponent.count; i++) {
			Entity clusterBombMunition = entityFactory.instantiate(clusterBombMunitionTemplate, parameters //
					.put("spatial", new SpatialImpl(spatial.getX(), spatial.getY(), 0.4f, 0.4f, MathUtils.random(0f, 360f))) //
					);
			PhysicsComponent physicsComponent = Components.getPhysicsComponent(clusterBombMunition);
			Body body = physicsComponent.getBody();
			body.applyLinearImpulse(MathUtils.random(-3f, 3f), 0f, body.getPosition().x, body.getPosition().y);
		}

	}

}