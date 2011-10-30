package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.animation4j.gdx.Animation;
import com.gemserk.commons.artemis.components.AnimationComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.SpriteComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.GlobalTime;

public class UpdateAnimationScript extends ScriptJavaImpl {

	@Override
	public void update(com.artemis.World world, Entity e) {
		SpriteComponent spriteComponent = Components.getSpriteComponent(e);
		AnimationComponent animationComponent = Components.getAnimationComponent(e);
		Animation animation = animationComponent.getCurrentAnimation();
		animation.update(GlobalTime.getDelta());
		Sprite sprite = animation.getCurrentFrame();
		spriteComponent.setSprite(sprite);
	}

}