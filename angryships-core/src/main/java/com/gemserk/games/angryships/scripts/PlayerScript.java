package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityFactory;
import com.gemserk.commons.artemis.templates.EntityTemplate;
import com.gemserk.commons.gdx.games.SpatialImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Text;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.utils.ParametersWrapper;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.PlayerComponent;
import com.gemserk.games.angryships.components.PlayerData;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.templates.BombTemplate;
import com.gemserk.games.angryships.templates.KamikazeControllableBombTemplate;
import com.gemserk.games.angryships.templates.ThrustTemplate;

public class PlayerScript extends ScriptJavaImpl {

	EntityFactory entityFactory;
	Injector injector;
	EventManager eventManager;

	Container screen;
	
	private EntityTemplate bombEntityTemplate;
	private EntityTemplate kamikazeBombEntityTemplate;
	private EntityTemplate thrustTemplate;

	@Override
	public void init(World world, Entity e) {

		Text normalBombsCountLabel = screen.findControl("NormalBombCountLabel");

		if (normalBombsCountLabel != null) {
			PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
			PlayerData playerData = playerComponent.playerData;
			normalBombsCountLabel.setText("x" + playerData.bombsLeft);
		}

		Text kamikazeBombsCountLabel = screen.findControl("KamikazeBombCountLabel");

		if (kamikazeBombsCountLabel != null) {
			PlayerComponent playerComponent = e.getComponent(PlayerComponent.class);
			PlayerData playerData = playerComponent.playerData;
			kamikazeBombsCountLabel.setText("x" + playerData.kamikazeBombsLeft);
		}
		
		bombEntityTemplate = injector.getInstance(BombTemplate.class);
		kamikazeBombEntityTemplate = injector.getInstance(KamikazeControllableBombTemplate.class);
		thrustTemplate = injector.getInstance(ThrustTemplate.class);
		
	}

	@Override
	public void update(World world, Entity e) {
		updateFirstFire(world, e);
		updateSecondFire(world, e);
	}

	public void updateFirstFire(World world, Entity e) {
		ControllerComponent controllerComponent = Components.getControllerComponent(e);
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

		for (int i = 0; i < 1; i++) {
			Entity missile = entityFactory.instantiate(bombEntityTemplate, new ParametersWrapper() //
					.put("spatial", new SpatialImpl(0f + i, 5f, 0.75f, 0.75f, 45)) //
					.put("controller", controller) //
					);
			entityFactory.instantiate(thrustTemplate, new ParametersWrapper() //
					.put("owner", missile) //
					);
		}

		playerData.bombsLeft--;

		Text normalBombsCountLabel = screen.findControl("NormalBombCountLabel");

		if (normalBombsCountLabel != null)
			normalBombsCountLabel.setText("x" + playerData.bombsLeft);
	}

	public void updateSecondFire(World world, Entity e) {
		ControllerComponent controllerComponent = Components.getControllerComponent(e);
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

		for (int i = 0; i < 1; i++) {
			Entity missile = entityFactory.instantiate(kamikazeBombEntityTemplate, new ParametersWrapper() //
					.put("spatial", new SpatialImpl(0f + i, 5f, 0.75f, 0.75f, 45)) //
					.put("controller", controller) //
					);

			entityFactory.instantiate(thrustTemplate, new ParametersWrapper() //
					.put("owner", missile) //
					);
		}

		playerData.kamikazeBombsLeft--;

		Text normalBombsCountLabel = screen.findControl("KamikazeBombCountLabel");

		if (normalBombsCountLabel != null)
			normalBombsCountLabel.setText("x" + playerData.kamikazeBombsLeft);

	}

}