package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.PixmapWorld;
import com.gemserk.prototypes.pixmap.PixmapHelper;
import com.gemserk.resources.ResourceManager;

public class TerrainEntityTemplate extends EntityTemplateImpl {

	PixmapWorld pixmapWorld;
	ResourceManager<String> resourceManager;
	Injector injector;

	@Override
	public void apply(Entity entity) {
		String terrainId = parameters.get("terrainId");
		Spatial spatial = parameters.get("spatial");
		
		PixmapHelper terrain = resourceManager.getResourceValue(terrainId);
		
		spatial.setSize(terrain.pixmap.getWidth(), terrain.pixmap.getHeight());
		
		pixmapWorld.addPixmap(terrain);
		
		entity.addComponent(new RenderableComponent(-350));
		entity.addComponent(new SpriteComponent(terrain.sprite, 0.5f, 0.5f, Color.WHITE));
		
		entity.addComponent(new SpatialComponent(spatial));
	}

}
