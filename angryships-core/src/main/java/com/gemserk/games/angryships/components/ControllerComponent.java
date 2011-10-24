package com.gemserk.games.angryships.components;

import com.artemis.Component;
import com.gemserk.games.angryships.gamestates.Controller;

public class ControllerComponent extends Component {
	
	public Controller controller;
	
	public float rotationSpeed;

	public ControllerComponent(Controller controller) {
		this(controller, 360f);
	}

	public ControllerComponent(Controller controller, float rotationSpeed) {
		this.controller = controller;
		this.rotationSpeed = rotationSpeed;
	}

}
