package com.gemserk.commons.artemis;

import com.artemis.Entity;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.utils.StoreFactory;

public class StoreFactoryEntityTemplateImpl implements StoreFactory<Entity> {
	
	EntityFactory entityFactory;
	EntityTemplate entityTemplate;

	@Override
	public Entity createObject() {
		return entityFactory.instantiate(entityTemplate);
	}

}
