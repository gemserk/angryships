package com.gemserk.games.angryships.components;

import com.artemis.Component;
import com.gemserk.commons.artemis.templates.EntityTemplate;

public class ExplosionComponent extends Component {

	public float radius;
	public EntityTemplate explosionAnimationTemplate;
	public boolean exploded = false;

	public ExplosionComponent(EntityTemplate explosionAnimationTemplate, float radius) {
		this.explosionAnimationTemplate = explosionAnimationTemplate;
		this.radius = radius;
	}

}
