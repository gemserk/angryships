package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.CameraComponent;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.SpatialComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.camera.Camera;
import com.gemserk.commons.gdx.camera.Libgdx2dCamera;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.CameraDistanceComponent;
import com.gemserk.games.angryships.scripts.CameraScript;

public class CameraFollowTemplate extends EntityTemplateImpl {
	
	Injector injector;

	@Override
	public void apply(Entity entity) {
		
		Libgdx2dCamera libgdx2dCamera = parameters.get("libgdx2dCamera");
		Camera camera = parameters.get("camera");
		Float distance = parameters.get("distance");
		
		entity.addComponent(new CameraComponent(libgdx2dCamera, camera));
		entity.addComponent(new CameraDistanceComponent(distance));
		entity.addComponent(new SpatialComponent(new SpatialImpl(0, 0)));
		entity.addComponent(new ScriptComponent(injector.getInstance(CameraScript.class)));
	}

}
