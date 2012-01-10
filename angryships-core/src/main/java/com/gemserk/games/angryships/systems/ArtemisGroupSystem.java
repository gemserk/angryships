package com.gemserk.games.angryships.systems;

import com.artemis.Entity;
import com.artemis.EntityProcessingSystem;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.GroupComponent;

public class ArtemisGroupSystem extends EntityProcessingSystem {

	@SuppressWarnings("unchecked")
	public ArtemisGroupSystem() {
		super(Components.groupComponentClass);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}
	
	@Override
	protected void enabled(Entity e) {
		super.enabled(e);
		GroupComponent groupComponent = Components.getGroupComponent(e);
		world.getGroupManager().set(groupComponent.group, e);
	}
	
	@Override
	protected void disabled(Entity e) {
		super.disabled(e);
		world.getGroupManager().remove(e);
	}

	@Override
	protected void process(Entity e) {

	}

}
