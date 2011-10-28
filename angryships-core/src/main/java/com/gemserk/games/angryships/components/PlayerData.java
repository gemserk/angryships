package com.gemserk.games.angryships.components;

public class PlayerData {
	
	public int bombsLeft;
	public int kamikazeBombsLeft;
	
	public PlayerData(int normalBombs, int kamikazeBombs) {
		this.bombsLeft = normalBombs;
		this.kamikazeBombsLeft = kamikazeBombs;
	}

}
