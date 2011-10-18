package com.gemserk.games.angryships.systems;

import com.artemis.World;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.gemserk.commons.artemis.WorldSystemImpl;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;

public class Box2dRenderSystem extends WorldSystemImpl {

	private final Box2DDebugRenderer box2dDebugRenderer = new Box2DDebugRenderer();
	private final Libgdx2dCamera libgdx2dCamera;
	private final com.badlogic.gdx.physics.box2d.World physicsWorld;

	public Box2dRenderSystem(Libgdx2dCamera libgdx2dCamera, com.badlogic.gdx.physics.box2d.World physicsWorld) {
		this.libgdx2dCamera = libgdx2dCamera;
		this.physicsWorld = physicsWorld;
	}

	@Override
	public void process(World world) {
		super.process(world);
		box2dDebugRenderer.render(physicsWorld, libgdx2dCamera.getCombinedMatrix());
	}

}
