package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.reflection.Handles;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class ErasePixmapWhenExplosionScript extends ScriptJavaImpl {

	private final Vector2 position = new Vector2();

	PixmapWorld pixmapWorld;

	@Handles(ids = Events.explosion)
	public void explosion(Event event) {
		Entity e = (Entity) event.getSource();

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = spatialComponent.getSpatial();

		ExplosionComponent explosionComponent = Components.getExplosionComponent(e);

		Array<PixmapHelper> pixmaps = pixmapWorld.getPixmaps();

		for (int i = 0; i < pixmaps.size; i++) {
			PixmapHelper pixmapHelper = pixmaps.get(i);
			pixmapHelper.project(position, spatial.getX(), spatial.getY());
			pixmapHelper.eraseCircle(position.x, position.y, explosionComponent.radius);
		}
	}

}