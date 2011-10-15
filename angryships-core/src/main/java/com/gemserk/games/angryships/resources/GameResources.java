package com.gemserk.games.angryships.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.gemserk.commons.gdx.resources.LibgdxResourceBuilder;
import com.gemserk.prototypes.pixmap.PixmapHelper;
import com.gemserk.resources.ResourceManager;
import com.gemserk.resources.dataloaders.DataLoader;

/**
 * Declares all resources needed for the game.
 */
public class GameResources extends LibgdxResourceBuilder {

	public static class Sprites {

		public static final String GemserkLogo = "GemserkLogo";
		public static final String GemserkLogoBlur = "GemserkLogoBlur";
		public static final String LwjglLogo = "LwjglLogo";
		public static final String LibgdxLogo = "LibgdxLogo";

		public static final String BombSprite = "BombSprite";

	}

	public static class Sounds {

		public static final String BombExplosion = "BombExplosionSound";

	}

	public static void load(ResourceManager<String> resourceManager) {
		new GameResources(resourceManager);
	}

	private GameResources(ResourceManager<String> resourceManager) {
		super(resourceManager);

		texture("GemserkLogoTexture", "data/images/logos/logo-gemserk-512x128.png");
		texture("GemserkLogoTextureBlur", "data/images/logos/logo-gemserk-512x128-blur.png");
		texture("LwjglLogoTexture", "data/images/logos/logo-lwjgl-512x256-inverted.png");
		texture("LibgdxLogoTexture", "data/images/logos/logo-libgdx-clockwork-512x256.png");

		sprite(Sprites.GemserkLogo, "GemserkLogoTexture");
		sprite(Sprites.GemserkLogoBlur, "GemserkLogoTextureBlur");
		sprite(Sprites.LwjglLogo, "LwjglLogoTexture", 0, 0, 512, 185);
		sprite(Sprites.LibgdxLogo, "LibgdxLogoTexture", 0, 25, 512, 256 - 50);

		texture("BackgroundTexture", "data/levels/superangrysheep-background.png", true);
		sprite("BackgroundSprite", "BackgroundTexture");

		texture("SecondBackgroundTexture", "data/levels/superangrysheep-background2.png", true);
		sprite("SecondBackgroundSprite", "SecondBackgroundTexture");

		texture("BombExplosionSpriteSheet", "data/animations/bomb-explosion-animation.png");
		animation("BombExplosionAnimation", "BombExplosionSpriteSheet", 0, 0, 128, 128, 15, false, 35);

		texture("BombTexture", "data/images/bomb.png", true);
		sprite(Sprites.BombSprite, "BombTexture");

		texture("ButtonFireTexture", "data/gui/button-fire.png", true);
		sprite("ButtonFireSprite", "ButtonFireTexture");

		texture("ButtonTurnLeftTexture", "data/gui/button-turn-left.png", true);
		sprite("ButtonTurnLeftSprite", "ButtonTurnLeftTexture");

		texture("ButtonTurnRightTexture", "data/gui/button-turn-right.png", true);
		sprite("ButtonTurnRightSprite", "ButtonTurnRightTexture");

		sound(Sounds.BombExplosion, "data/audio/bomb-explosion.ogg");
		
		pixmapHelper("Level01_0", "data/levels/level01-0.png");
		pixmapHelper("Level01_1", "data/levels/level01-1.png");
		
	}
	
	public void pixmapHelper(String id, final String file) {
		resourceManager.addVolatile(id, new DataLoader<PixmapHelper>() {
			@Override
			public PixmapHelper load() {
				return new PixmapHelper(new Pixmap(Gdx.files.internal(file)));
			}
		});
	}
}
