package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.math.Vector2;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.input.LibgdxPointer;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.CameraDistanceComponent;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.scripts.CameraScript;

public class CameraFollowTemplate extends EntityTemplateImpl {

	public static class DragCameraControllerScript extends ScriptJavaImpl {

		LibgdxPointer libgdxPointer;
		Vector2 lastPosition = new Vector2();
		Vector2 movement = new Vector2();

		@Override
		public void init(World world, Entity e) {
			libgdxPointer = new LibgdxPointer(0);
		}

		@Override
		public void update(World world, Entity e) {

			ImmutableBag<Entity> bombEntities = world.getGroupManager().getEntities(Groups.Bombs);
			if (!bombEntities.isEmpty())
				return;

			libgdxPointer.update();

			if (!libgdxPointer.touched)
				return;

			if (libgdxPointer.wasPressed()) {
				lastPosition.set(libgdxPointer.getPressedPosition());
				return;
			}

			movement.set(libgdxPointer.getPosition());
			movement.sub(lastPosition);

			CameraComponent cameraComponent = Components.getCameraComponent(e);

			movement.mul(1f / cameraComponent.getCamera().getZoom());

			Camera camera = cameraComponent.getCamera();
			camera.setPosition(camera.getX() - movement.x, camera.getY() - movement.y);

			lastPosition.set(libgdxPointer.getPosition());
		}

	}

	Injector injector;

	@Override
	public void apply(Entity entity) {

		Libgdx2dCamera libgdx2dCamera = parameters.get("libgdx2dCamera");
		Camera camera = parameters.get("camera");
		Float distance = parameters.get("distance");

		entity.addComponent(new CameraComponent(libgdx2dCamera, camera));
		entity.addComponent(new CameraDistanceComponent(distance));
		entity.addComponent(new SpatialComponent(new SpatialImpl(0, 0)));
		entity.addComponent(new ScriptComponent( //
				new DragCameraControllerScript(), //
				injector.getInstance(CameraScript.class) //
		));
	}

}
