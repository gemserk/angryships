package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.artemis.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.componentsengine.input.LibgdxInputMappingBuilder;
import com.gemserk.games.angryships.components.ButtonMonitorsComponent;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.scripts.ButtonsControllerScript;

public class KeyboardControllerTemplate extends EntityTemplateImpl {
	
	public static class UpdateButtonMonitorsScript extends ScriptJavaImpl {
		
		@Override
		public void update(World world, Entity e) {
			ButtonMonitorsComponent buttonMonitorsComponent = e.getComponent(ButtonMonitorsComponent.class);
			buttonMonitorsComponent.leftButtonMonitor.update();
			buttonMonitorsComponent.rightButtonMonitor.update();
			buttonMonitorsComponent.fireButtonMonitor.update();
			buttonMonitorsComponent.secondFireButtonMonitor.update();
			buttonMonitorsComponent.explodeButtonMonitor.update();
		}
		
	}
	
	Injector injector;

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		
		ButtonMonitorsComponent buttonMonitorsComponent = new ButtonMonitorsComponent();
		
		buttonMonitorsComponent.leftButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.LEFT);
		buttonMonitorsComponent.rightButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.RIGHT);
		buttonMonitorsComponent.fireButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.SPACE);
		buttonMonitorsComponent.secondFireButtonMonitor  = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.ENTER);
		buttonMonitorsComponent.explodeButtonMonitor = LibgdxInputMappingBuilder.keyButtonMonitor(Gdx.input, Keys.SPACE);
		
		entity.addComponent(buttonMonitorsComponent);
		
		entity.addComponent(new TagComponent(Tags.Controller));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent(injector.getInstance(ButtonsControllerScript.class), injector.getInstance(UpdateButtonMonitorsScript.class)));
	}

}
