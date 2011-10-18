package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.resources.ResourceManager;

public class StaticSpriteTemplate extends EntityTemplateImpl {

	ResourceManager<String> resourceManager;

	@Override
	public void apply(Entity entity) {
		Spatial spatial = parameters.get("spatial");
		String spriteId = parameters.get("spriteId");
		Integer layer = parameters.get("layer");
		
		Sprite sprite = resourceManager.getResourceValue(spriteId);
		
		entity.addComponent(new RenderableComponent(layer));
		entity.addComponent(new SpriteComponent(sprite, 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new SpatialComponent(spatial));
	}

}
