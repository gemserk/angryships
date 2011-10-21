package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gemserk.commons.artemis.components.PhysicsComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;

public class AntiGravityScript extends ScriptJavaImpl {

	private static final Vector2 antiGravity = new Vector2(0, 10f);
	private final Vector2 tmp = new Vector2();

	@Override
	public void update(World world, Entity e) {
		PhysicsComponent physicsComponent = e.getComponent(PhysicsComponent.class);
		Body body = physicsComponent.getPhysics().getBody();

		tmp.set(antiGravity).mul(body.getMass());

		body.applyForceToCenter(tmp);
	}

}