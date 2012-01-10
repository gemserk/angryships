package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.components.ButtonMonitorsComponent;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.Components;
import com.gemserk.games.angryships.gamestates.Controller;

public class ButtonsControllerScript extends ScriptJavaImpl {

	@Override
	public void update(World world, Entity e) {
		ButtonMonitorsComponent buttonMonitorsComponent = e.getComponent(ButtonMonitorsComponent.class);

		ControllerComponent controllerComponent = Components.getControllerComponent(e);
		Controller controller = controllerComponent.controller;

		controller.left = buttonMonitorsComponent.leftButtonMonitor.isHolded();
		controller.right = buttonMonitorsComponent.rightButtonMonitor.isHolded();
		controller.fire = buttonMonitorsComponent.fireButtonMonitor.isReleased();
		controller.secondFire = buttonMonitorsComponent.secondFireButtonMonitor.isReleased();
		controller.explode = buttonMonitorsComponent.explodeButtonMonitor.isReleased();
	}

}