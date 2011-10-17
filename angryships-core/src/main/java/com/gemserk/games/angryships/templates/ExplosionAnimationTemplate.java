package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.RenderableComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.Spatial;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.resources.GameResources;
import com.gemserk.games.angryships.scripts.UpdateAnimationScript;
import com.gemserk.resources.ResourceManager;

public class ExplosionAnimationTemplate extends EntityTemplateImpl {
	
	ResourceManager<String> resourceManager;
	Injector injector;

	@Override
	public void apply(Entity entity) {
		Animation animation = resourceManager.getResourceValue(GameResources.Animations.BombExplosionAnimation);
		
		Sprite currentFrame = animation.getCurrentFrame();
		
		Spatial spatial = parameters.get("spatial");
		
		spatial.setSize(currentFrame.getWidth(), currentFrame.getHeight());
		spatial.setAngle(MathUtils.random(0f, 360f));
		
		entity.addComponent(new RenderableComponent(0));
		entity.addComponent(new SpriteComponent(currentFrame, 0.5f, 0.5f, Color.WHITE));
		entity.addComponent(new SpatialComponent(spatial));
		entity.addComponent(new AnimationComponent(new Animation[] {animation}));
		entity.addComponent(new ScriptComponent(injector.getInstance(UpdateAnimationScript.class)));
	}

}
