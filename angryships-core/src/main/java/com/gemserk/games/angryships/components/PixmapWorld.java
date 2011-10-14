package com.gemserk.games.angryships.components;

import com.badlogic.gdx.utils.Array;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class PixmapWorld {
	
	Array<PixmapHelper> pixmaps = new Array<PixmapHelper>();

	public PixmapWorld() {
		pixmaps = new Array<PixmapHelper>();
	}
	
	public void addPixmap(PixmapHelper pixmap) {
		pixmaps.add(pixmap);
	}
	
	public Array<PixmapHelper> getPixmaps() {
		return pixmaps;
	}
	
}
