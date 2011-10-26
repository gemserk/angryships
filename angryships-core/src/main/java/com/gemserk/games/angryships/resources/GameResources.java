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
		
		public static final String WhiteRectangleSprite = "WhiteRectangleSprite";
		
		public static final String BackgroundSprite = "BackgroundSprite";
		public static final String SecondBackgroundSprite = "SecondBackgroundSprite";

		public static final String BombSprite = "BombSprite";
		
		public static final String TurnRightButtonSprite = "TurnRightButtonSprite";
		public static final String TurnLeftButtonSprite = "TurnLeftButtonSprite";
		public static final String FireButtonSprite = "FireButtonSprite";
		
		public static final String PauseButtonSprite = "PauseButtonSprite";
		public static final String RestartButtonSprite = "RestartButtonSprite";

	}

	public static class Animations {

		public static final String BombExplosionAnimation = "BombExplosionAnimation";

		public static final String ItemIdleAnimation = "ItemIdleAnimation";

	}

	public static class Sounds {

		public static final String BombExplosion = "BombExplosionSound";

	}
	
	public static class Fonts {

		public static final String MessageFont = "MessageFont";

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
		
		texture("WhiteRectangleTexture", "data/images/white-rectangle.png", true);
		sprite(Sprites.WhiteRectangleSprite, "WhiteRectangleTexture");

		texture("BackgroundTexture", "data/levels/background.png", true);
		sprite(Sprites.BackgroundSprite, "BackgroundTexture");

		texture("SecondBackgroundTexture", "data/levels/background2.png", true);
		sprite(Sprites.SecondBackgroundSprite, "SecondBackgroundTexture");

		texture("BombExplosionSpriteSheet", "data/animations/bomb-explosion-animation.png", true);
		animation(Animations.BombExplosionAnimation, "BombExplosionSpriteSheet", 0, 0, 128, 128, 15, false, 35);
		
		texture("ItemIdleSpriteSheet", "data/animations/item-idle-animation.png", true);
		animation(Animations.ItemIdleAnimation, "ItemIdleSpriteSheet", 0, 0, 128, 128, 9, true, 200, 125);
		
		texture("BombTexture", "data/images/bomb.png", true);
		sprite(Sprites.BombSprite, "BombTexture");

		texture("ButtonFireTexture", "data/gui/button-fire.png", true);
		sprite(Sprites.FireButtonSprite, "ButtonFireTexture");

		texture("ButtonTurnLeftTexture", "data/gui/button-turn-left.png", true);
		sprite(Sprites.TurnLeftButtonSprite, "ButtonTurnLeftTexture");

		texture("ButtonTurnRightTexture", "data/gui/button-turn-right.png", true);
		sprite(Sprites.TurnRightButtonSprite, "ButtonTurnRightTexture");
		
		texture("ButtonRestartTexture", "data/gui/button-restart.png", true);
		sprite(Sprites.RestartButtonSprite, "ButtonRestartTexture");
		
		texture("ButtonPauseTexture", "data/gui/button-pause.png", true);
		sprite(Sprites.PauseButtonSprite, "ButtonPauseTexture");
		
		sound(Sounds.BombExplosion, "data/audio/bomb-explosion.ogg");

		texture("LevelGroundTexture", "data/levels/ground.png", true);
		sprite("LevelGroundSprite", "LevelGroundTexture");
		
		texture("Level01_0Texture", "data/levels/level01-0.png", true);
		sprite("Level01_0Sprite", "Level01_0Texture");

		texture("Level01_1Texture", "data/levels/level01-1.png", true);
		sprite("Level01_1Sprite", "Level01_1Texture");

		pixmapHelper("Level01_0", "data/levels/level01-0.png");
		pixmapHelper("Level01_1", "data/levels/level01-1.png");
		pixmapHelper("Level01-ground", "data/levels/ground.png");
		
		font(Fonts.MessageFont, "data/fonts/purisa-18.png", "data/fonts/purisa-18.fnt", false);
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
