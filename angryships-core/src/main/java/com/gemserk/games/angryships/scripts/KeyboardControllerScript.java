package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.componentsengine.input.ButtonMonitor;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.GameComponents;
import com.gemserk.games.angryships.gamestates.Controller;

public class KeyboardControllerScript extends ScriptJavaImpl {
	
	private ButtonMonitor leftButtonMonitor;
	private ButtonMonitor rightButtonMonitor;
	private ButtonMonitor fireButtonMonitor;

	@Override
	public void init(World world, Entity e) {
		super.init(world, e);
		leftButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.LEFT);
		rightButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.RIGHT);
		fireButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.SPACE);
	}

	@Override
	public void update(World world, Entity e) {
		
		leftButtonMonitor.update();
		rightButtonMonitor.update();
		fireButtonMonitor.update();
		
		ControllerComponent controllerComponent = GameComponents.getControllerComponent(e);
		Controller controller = controllerComponent.controller;
		
		controller.left = leftButtonMonitor.isHolded();
		controller.right = rightButtonMonitor.isHolded();
		controller.fire = fireButtonMonitor.isReleased();

	}

}