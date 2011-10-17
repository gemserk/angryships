package com.gemserk.games.angryships.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.gemserk.componentsengine.input.ButtonMonitor;

public class GraphicButtonMonitor extends ButtonMonitor {

	Sprite sprite;
	Rectangle bounds;

	public GraphicButtonMonitor(Sprite sprite) {
		this.sprite = sprite;
		bounds = new Rectangle(this.sprite.getX(), //
				this.sprite.getY(), //
				this.sprite.getWidth(), //
				this.sprite.getHeight());
	}

	@Override
	protected boolean isDown() {
		if (!Gdx.input.isTouched())
			return false;

		float x = Gdx.input.getX();
		float y = Gdx.graphics.getHeight() - Gdx.input.getY();

		return bounds.contains(x, y);
	}

}