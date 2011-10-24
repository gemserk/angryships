package com.gemserk.games.angryships.input;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.gemserk.commons.gdx.gui.ImageButton;

public class CustomImageButton extends ImageButton {
	
	GraphicButtonMonitor buttonMonitor;

	public GraphicButtonMonitor getButtonMonitor() {
		return buttonMonitor;
	}
	
	public CustomImageButton(Sprite sprite) {
		super(sprite);
		this.buttonMonitor = new GraphicButtonMonitor(sprite);
	}
	
	@Override
	public void update() {
		super.update();
		buttonMonitor.update();
	}
	
	@Override
	public void recalculateBounds() {
		super.recalculateBounds();
		this.buttonMonitor.setBounds(getBounds());
	}

}
