package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.audio.Sound;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SoundSpawnerComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.Parameters;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.resources.ResourceManager;

public class ExplosionSoundSpawnerTemplate extends EntityTemplateImpl {

	public static class ExplosionSensorSpawnerScript extends ScriptJavaImpl {

		// private final Vector2 position = new Vector2();

		private final Parameters parameters = new ParametersWrapper();

		EntityFactory entityFactory;
		Injector injector;

		// PixmapWorld pixmapWorld;

		@Handles(ids = Events.explosion)
		public void explosion(Event event) {
			Entity e = (Entity) event.getSource();

			SpatialComponent spatialComponent = Components.getSpatialComponent(e);
			Spatial spatial = spatialComponent.getSpatial();

			ExplosionComponent explosionComponent = Components.getExplosionComponent(e);

			// Array<PixmapHelper> pixmaps = pixmapWorld.getPixmaps();
			//
			// for (int i = 0; i < pixmaps.size; i++) {
			// PixmapHelper pixmapHelper = pixmaps.get(i);
			// pixmapHelper.project(position, spatial.getX(), spatial.getY());
			// pixmapHelper.eraseCircle(position.x, position.y, explosionComponent.radius);
			// }

			SpatialImpl explosionSensorSpatial = new SpatialImpl(spatial);
			explosionSensorSpatial.setSize(explosionComponent.radius, explosionComponent.radius);

			entityFactory.instantiate(injector.getInstance(ExplosionSensorTemplate.class), parameters //
					.put("spatial", explosionSensorSpatial) //
					);
		}

	}

	ResourceManager<String> resourceManager;
	Injector injector;

	@Override
	public void apply(Entity entity) {
		Sound sound = resourceManager.getResourceValue(GameResources.Sounds.BombExplosion);
		entity.addComponent(new SoundSpawnerComponent(Events.explosion, sound));
		entity.addComponent(new ScriptComponent(injector.getInstance(ExplosionSensorSpawnerScript.class)));
	}

}
