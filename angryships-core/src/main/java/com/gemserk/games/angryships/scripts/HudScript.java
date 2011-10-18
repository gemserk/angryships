package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.animation4j.ControlPositionConverter;
import com.gemserk.games.angryships.entities.Groups;

public class HudScript extends ScriptJavaImpl {
	
	private static final ControlPositionConverter controlPositionConverter = new ControlPositionConverter();
	
	Container screen;
	
	boolean visible = false;
	
	@Override
	public void init(World world, Entity e) {
		
		Control menuButtonsPanel = screen.findControl("MovementButtonsContainer");
		Control fireButtonsPanel = screen.findControl("FireButtonsContainer");
		
		Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());
		
		Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());
		
	}
	
	@Override
	public void update(com.artemis.World world, Entity e) {
		
		ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);

		Control menuButtonsPanel = screen.findControl("MovementButtonsContainer");
		Control fireButtonsPanel = screen.findControl("FireButtonsContainer");
		
		if (bombs.size() == 0 && visible) {
			
			Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());
			
			Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());
			
			visible = false;
		} else if ( bombs.size() > 0 && !visible ){
			
			Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());
			
			Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());
			
			visible = true;
		}
		
	}

}