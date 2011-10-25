package com.gemserk.games.angryships.components;

import com.artemis.Component;

public class PlayerComponent extends Component {

	public PlayerData playerData;

	public PlayerComponent(PlayerData playerData) {
		this.playerData = playerData;
	}

}