package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.components.PlayerInformationComponent;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;

public class PlayerTemplate extends EntityTemplateImpl {

	public static class FireBombScript extends ScriptJavaImpl {

		EntityFactory entityFactory;
		Injector injector;

		@Override
		public void update(World world, Entity e) {
			ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
			Controller controller = controllerComponent.controller;

			if (!controller.fire)
				return;

			ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);

			if (bombs.size() > 0)
				return;

			EntityTemplate bombEntityTemplate = injector.getInstance(BombTemplate.class);
			// EntityTemplate bombEntityTemplate = injector.getInstance(KamikazeControllableBombTemplate.class);
			entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
					.put("spatial", new SpatialImpl(2f, 10f, 0.75f, 0.75f, 0)) //
					.put("controller", controller) //
					);

			PlayerInformationComponent component = e.getComponent(PlayerInformationComponent.class);
			component.bombsLeft--;
		}

	}

	Injector injector;

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		entity.addComponent(new TagComponent(Tags.Player));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new PlayerInformationComponent());
		entity.addComponent(new ScriptComponent(injector.getInstance(FireBombScript.class)));
	}

}