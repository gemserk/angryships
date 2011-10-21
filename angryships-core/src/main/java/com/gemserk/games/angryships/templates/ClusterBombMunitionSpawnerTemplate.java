package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.scripts.ClusterBombScript;

public class ClusterBombMunitionSpawnerTemplate extends EntityTemplateImpl {

	Injector injector;

	@Override
	public void apply(Entity entity) {

		entity.addComponent(new ScriptComponent( //
				injector.getInstance(ClusterBombScript.class) //
		));

	}

}
