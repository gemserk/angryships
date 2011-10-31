package com.gemserk.games.angryships.templates;

import com.artemis.Entity;
import com.gemserk.commons.artemis.components.ScriptComponent;
import com.gemserk.commons.artemis.components.TagComponent;
import com.gemserk.commons.artemis.templates.EntityTemplateImpl;
import com.gemserk.commons.reflection.Injector;
import com.gemserk.games.angryships.components.ControllerComponent;
import com.gemserk.games.angryships.components.PlayerComponent;
import com.gemserk.games.angryships.components.PlayerData;
import com.gemserk.games.angryships.entities.Tags;
import com.gemserk.games.angryships.gamestates.Controller;
import com.gemserk.games.angryships.scripts.PlayerScript;

public class PlayerTemplate extends EntityTemplateImpl {

	Injector injector;

	@Override
	public void apply(Entity entity) {
		Controller controller = parameters.get("controller");
		PlayerData playerData = parameters.get("playerData");

		entity.addComponent(new TagComponent(Tags.Player));
		entity.addComponent(new ControllerComponent(controller));
		entity.addComponent(new PlayerComponent(playerData));
		entity.addComponent(new ScriptComponent(injector.getInstance(PlayerScript.class)));
	}

}