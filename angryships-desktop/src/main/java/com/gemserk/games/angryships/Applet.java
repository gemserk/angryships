package com.gemserk.games.angryships;

import java.awt.BorderLayout;
import java.awt.Canvas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.gemserk.commons.adwhirl.AdWhirlViewHandler;
import com.gemserk.commons.gdx.DensityUtils.Density;
import com.gemserk.commons.gdx.DensityUtilsFixedImpl;
import com.gemserk.commons.utils.gdx.LwjglLibgdxLibraryUtils;

public class Applet extends java.applet.Applet {

	private static final long serialVersionUID = 6396112708370503447L;

	private Canvas canvas;

	private LwjglApplication application;

	public void start() {

	}

	public void stop() {

	}

	public void destroy() {
		remove(canvas);
		super.destroy();
	}
	
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		canvas.setSize(width, height);
		// libgdx calls automatically the resize method of the application listener whenever the canvas size was modified....
	}

	public void init() {

		GdxNativesLoader.disableNativesLoading = true;
		LwjglLibgdxLibraryUtils.loadLibgdxLibrary();

		try {
			setLayout(new BorderLayout());
			// ApplicationListener game = (ApplicationListener) Class.forName(getParameter("game")).newInstance();

			canvas = new Canvas() {
				public final void addNotify() {
					super.addNotify();

					Game game = new Game() {
						@Override
						public void create() {
							Gdx.graphics.setVSync(true);
							super.create();
						};
					};
					
					game.setAdWhirlViewHandler(new AdWhirlViewHandler());
					game.setDensityUtils(new DensityUtilsFixedImpl(Density.High));

					application = new LwjglApplication(game, false, this) {
						public com.badlogic.gdx.Application.ApplicationType getType() {
							return ApplicationType.Applet;
						};
					};
				}

				public final void removeNotify() {
					application.stop();
					super.removeNotify();
				}
			};
			canvas.setSize(getWidth(), getHeight());
			add(canvas);
			canvas.setFocusable(true);
			canvas.requestFocus();
			canvas.setIgnoreRepaint(true);
			setVisible(true);
		} catch (Exception e) {
			System.err.println(e);
			throw new RuntimeException("Unable to create display", e);
		}
	}
}