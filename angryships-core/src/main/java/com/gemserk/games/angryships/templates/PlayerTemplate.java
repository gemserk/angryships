package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.components.PlayerComponent;
import com.gemserk.games.angryships.components.PlayerData;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;

public class PlayerTemplate extends EntityTemplateImpl {

	public static class FireBombScript extends ScriptJavaImpl {

		EntityFactory entityFactory;
		Injector injector;
		EventManager eventManager;

		Container screen;

		@Override
		public void init(World world, Entity e) {

			Text normalBombsCountLabel = screen.findControl("NormalBombCountLabel");

			if (normalBombsCountLabel != null) {
				PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
				PlayerData playerData = playerComponent.playerData;
				normalBombsCountLabel.setText("x" + playerData.bombsLeft);
			}

			// KamikazeBombCountLabel

			Text kamikazeBombsCountLabel = screen.findControl("KamikazeBombCountLabel");

			if (kamikazeBombsCountLabel != null) {
				PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
				PlayerData playerData = playerComponent.playerData;
				kamikazeBombsCountLabel.setText("x" + playerData.kamikazeBombsLeft);
			}

		}

		@Override
		public void update(World world, Entity e) {
			ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
			Controller controller = controllerComponent.controller;

			updateFirstFire(world, e);
			updateSecondFire(world, e);
		}

		public void updateFirstFire(World world, Entity e) {
			ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
			Controller controller = controllerComponent.controller;

			if (!controller.fire)
				return;

			ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);

			if (bombs.size() > 0)
				return;
			
			PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
			PlayerData playerData = playerComponent.playerData;
			
			if (playerData.bombsLeft == 0)
				return;

			EntityTemplate bombEntityTemplate = injector.getInstance(BombTemplate.class);
			// EntityTemplate bombEntityTemplate = injector.getInstance(KamikazeControllableBombTemplate.class);

			for (int i = 0; i < 1; i++) {
				entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
						.put("spatial", new SpatialImpl(0f + i, 5f, 0.75f, 0.75f, 45)) //
						.put("controller", controller) //
						);
			}

			playerData.bombsLeft--;

			// eventManager.registerEvent(Events.bombSpawned, e);

			Text normalBombsCountLabel = screen.findControl("NormalBombCountLabel");

			if (normalBombsCountLabel != null)
				normalBombsCountLabel.setText("x" + playerData.bombsLeft);
		}

		public void updateSecondFire(World world, Entity e) {
			ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
			Controller controller = controllerComponent.controller;

			if (!controller.secondFire)
				return;

			ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);

			if (bombs.size() > 0)
				return;
			
			PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
			PlayerData playerData = playerComponent.playerData;
			
			if (playerData.kamikazeBombsLeft == 0)
				return;

			EntityTemplate bombEntityTemplate = injector.getInstance(KamikazeControllableBombTemplate.class);

			for (int i = 0; i < 1; i++) {
				entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
						.put("spatial", new SpatialImpl(0f + i, 5f, 0.75f, 0.75f, 45)) //
						.put("controller", controller) //
						);
			}

			playerData.kamikazeBombsLeft--;

			Text normalBombsCountLabel = screen.findControl("KamikazeBombCountLabel");

			if (normalBombsCountLabel != null)
				normalBombsCountLabel.setText("x" + playerData.kamikazeBombsLeft);
			
		}

	}

	Injector injector;

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		PlayerData playerData = parameters.get("playerData");

		entity.addComponent(new TagComponent(Tags.Player));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new PlayerComponent(playerData));
		entity.addComponent(new ScriptComponent(injector.getInstance(FireBombScript.class)));
	}

}