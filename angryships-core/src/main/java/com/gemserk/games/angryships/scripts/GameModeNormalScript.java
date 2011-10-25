package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.games.angryships.components.PlayerComponent;
import com.gemserk.games.angryships.components.PlayerData;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.entities.Groups;
import com.gemserk.games.angryships.entities.Tags;

public class GameModeNormalScript extends ScriptJavaImpl {

	EventManager eventManager;

	@Override
	public void update(com.artemis.World world, Entity e) {
		
		Entity player = world.getTagManager().getEntity(Tags.Player);
		if (player == null)
			return;
		
		PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
		
		ImmutableBag<Entity> targets = world.getGroupManager().getEntities(Groups.Targets);
		
		if (targets.size() == 0) {
			eventManager.registerEvent(Events.gameOver, player);
			e.delete();
			return;
		}
		
		ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);
		
		PlayerData playerData = playerComponent.playerData;

		if (playerData.bombsLeft == 0 && bombs.size() == 0) {
			eventManager.registerEvent(Events.gameOver, player);
			e.delete();
			return;
		}
	}

}