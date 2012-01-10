package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ExplosionComponent;

public class RemoveWhenExplodedScript extends ScriptJavaImpl {

	@Override
	public void update(World world, Entity e) {
		ExplosionComponent explosionComponent = Components.getExplosionComponent(e);
		if (explosionComponent.exploded)
			e.delete();
	}

}