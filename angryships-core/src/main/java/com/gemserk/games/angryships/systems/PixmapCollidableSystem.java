package com.gemserk.games.angryships.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.gdx.graphics.ColorUtils;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.components.PixmapCollidableComponent;
import com.gemserk.games.angryships.components.PixmapCollision;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class PixmapCollidableSystem extends EntityProcessingSystem implements Disposable {

	private static final Color color = new Color();
	private static final Vector2 position = new Vector2();

	PixmapWorld pixmapWorld;

	public void setPixmapWorld(PixmapWorld pixmapWorld) {
		this.pixmapWorld = pixmapWorld;
	}

	public PixmapCollidableSystem() {
		super(PixmapCollidableComponent.class);
	}

	@Override
	protected void process(Entity e) {

		SpatialComponent spatialComponent = Components.getSpatialComponent(e);
		Spatial spatial = spatialComponent.getSpatial();

		PixmapCollidableComponent pixmapCollidableComponent = GameComponents.getPixmapCollidableComponent(e);
		PixmapCollision pixmapCollision = pixmapCollidableComponent.pixmapCollision;
		
		pixmapCollision.clearContacts();

		Array<PixmapHelper> pixmaps = pixmapWorld.getPixmaps();

		for (int i = 0; i < pixmaps.size; i++) {
			PixmapHelper pixmap = pixmaps.get(i);
			pixmap.project(position, spatial.getX(), spatial.getY());
			ColorUtils.rgba8888ToColor(color, pixmap.getPixel(position.x, position.y));
			if (color.a == 0)
				continue;
			pixmapCollision.addContact(pixmap);
		}

	}
	
	@Override
	protected void end() {
		super.end();
	
		Array<PixmapHelper> pixmaps = pixmapWorld.getPixmaps();
		for (int i = 0; i < pixmaps.size; i++) 
			pixmaps.get(i).update();
		
	}

	@Override
	public void dispose() {
		pixmapWorld.dispose();		
	}

}
