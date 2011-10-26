package com.gemserk.games.angryships.scripts;

import com.artemis.Entity;
import com.artemis.World;
import com.artemis.utils.ImmutableBag;
import com.badlogic.gdx.Gdx;
import com.gemserk.animation4j.interpolator.function.InterpolationFunctions;
import com.gemserk.animation4j.transitions.Transitions;
import com.gemserk.animation4j.transitions.sync.Synchronizers;
import com.gemserk.commons.artemis.components.Components;
import com.gemserk.commons.artemis.components.PropertiesComponent;
import com.gemserk.commons.artemis.events.ArtemisEventListener;
import com.gemserk.commons.artemis.events.Event;
import com.gemserk.commons.artemis.events.EventListener;
import com.gemserk.commons.artemis.events.EventManager;
import com.gemserk.commons.artemis.scripts.ScriptJavaImpl;
import com.gemserk.commons.gdx.gui.Container;
import com.gemserk.commons.gdx.gui.Control;
import com.gemserk.commons.gdx.gui.animation4j.converters.GuiConverters;
import com.gemserk.commons.values.BooleanValue;
import com.gemserk.games.angryships.entities.Events;
import com.gemserk.games.angryships.entities.Groups;

public class HudScript extends ScriptJavaImpl {

	private static final String gameOverListenerProperty = "gameOverProperty";
	private static final String visibleProperty = "visibleProperty";

	EventManager eventManager;

	// this one could be a parameter instead, be stored in a custom component or the properties component instead being unique.
	Container screen;

	@Override
	public void init(World world, Entity e) {

		Control menuButtonsPanel = screen.findControl("MovementButtonsContainer");
		Control fireButtonsPanel = screen.findControl("FireButtonsContainer");

		Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, GuiConverters.controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());

		Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, GuiConverters.controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());

		EventListener gameOverListener = new ArtemisEventListener(world, e) {
			@Override
			public void onEvent(Event event) {
				gameOver(this.world, this.entity, event);
			}
		};

		PropertiesComponent propertiesComponent = Components.getPropertiesComponent(e);

		propertiesComponent.properties.put(HudScript.gameOverListenerProperty, gameOverListener);
		propertiesComponent.properties.put(HudScript.visibleProperty, new BooleanValue(false));

		eventManager.register(Events.gameOver, gameOverListener);

	}

	@Override
	public void dispose(World world, Entity e) {
		PropertiesComponent propertiesComponent = Components.getPropertiesComponent(e);
		EventListener gameOverListener = (EventListener) propertiesComponent.properties.get(HudScript.gameOverListenerProperty);
		eventManager.unregister(Events.gameOver, gameOverListener);

		propertiesComponent.properties.remove(HudScript.gameOverListenerProperty);
		propertiesComponent.properties.remove(HudScript.visibleProperty);
	}

	public void gameOver(World world, Entity e, Event event) {
		Control menuButtonsPanel = screen.findControl("MovementButtonsContainer");
		Control fireButtonsPanel = screen.findControl("FireButtonsContainer");

		Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, GuiConverters.controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());

		Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, GuiConverters.controlPositionConverter) //
				.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
				.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
				.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
				.build());

		PropertiesComponent propertiesComponent = Components.getPropertiesComponent(e);
		BooleanValue visible = (BooleanValue) propertiesComponent.properties.get(HudScript.visibleProperty);
		visible.value = false;

		e.delete();
	}

	@Override
	public void update(World world, Entity e) {

		ImmutableBag<Entity> bombs = world.getGroupManager().getEntities(Groups.Bombs);

		Control menuButtonsPanel = screen.findControl("MovementButtonsContainer");
		Control fireButtonsPanel = screen.findControl("FireButtonsContainer");

		PropertiesComponent propertiesComponent = Components.getPropertiesComponent(e);
		BooleanValue visible = (BooleanValue) propertiesComponent.properties.get(HudScript.visibleProperty);

		if (bombs.size() == 0 && visible.value) {

			Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, GuiConverters.controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());

			Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, GuiConverters.controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());

			visible.value = false;
		} else if (bombs.size() > 0 && !visible.value) {

			Synchronizers.transition(Transitions.mutableTransition(menuButtonsPanel, GuiConverters.controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());

			Synchronizers.transition(Transitions.mutableTransition(fireButtonsPanel, GuiConverters.controlPositionConverter) //
					.start(Gdx.graphics.getWidth() * 0f, Gdx.graphics.getHeight() * 0f) //
					.end(0.5f, Gdx.graphics.getWidth() * 0f, -Gdx.graphics.getHeight() * 0.5f) //
					.functions(InterpolationFunctions.easeIn(), InterpolationFunctions.easeIn()) //
					.build());

			visible.value = true;
		}

	}

}