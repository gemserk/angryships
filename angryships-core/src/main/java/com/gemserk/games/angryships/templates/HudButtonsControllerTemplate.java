package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.ButtonMonitorsComponent;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.scripts.ButtonsControllerScript;

public class HudButtonsControllerTemplate extends EntityTemplateImpl {
	
	Injector injector;

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		
		ButtonMonitorsComponent buttonMonitorsComponent = new ButtonMonitorsComponent();
		
		buttonMonitorsComponent.leftButtonMonitor = parameters.get("leftButtonMonitor");
		buttonMonitorsComponent.rightButtonMonitor = parameters.get("rightButtonMonitor");
		buttonMonitorsComponent.fireButtonMonitor = parameters.get("fireButtonMonitor");
		buttonMonitorsComponent.explodeButtonMonitor = parameters.get("explodeButtonMonitor");
		
		entity.addComponent(buttonMonitorsComponent);
		
		entity.addComponent(new TagComponent(Tags.Controller));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent(injector.getInstance(ButtonsControllerScript.class)));
	}

}
