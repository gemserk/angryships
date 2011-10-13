package com.gemserk.games.angryships.components;

import com.artemis.Component;
import com.gemserk.games.angryships.gamestates.Controller;

public class ControllerComponent extends Component {
	
	public Controller controller;

	public ControllerComponent(Controller controller) {
		this.controller = controller;
	}

}
