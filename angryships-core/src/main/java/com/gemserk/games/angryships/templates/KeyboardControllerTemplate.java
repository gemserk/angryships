package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.scripts.KeyboardControllerScript;

public class KeyboardControllerTemplate extends EntityTemplateImpl {

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		entity.addComponent(new TagComponent(Tags.Controller));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new ScriptComponent(new KeyboardControllerScript()));
	}

}
