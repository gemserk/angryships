package com.gemserk.games.angryships.components;

import com.badlogic.gdx.utils.Array;
import com.gemserk.prototypes.pixmap.PixmapHelper;

public class PixmapCollision {
	
	Array<PixmapHelper> pixmapContacts = new Array<PixmapHelper>();
	int activeContacts = 0;

	public boolean isInContact() {
		return activeContacts != 0;
	}

	public void addContact(PixmapHelper pixmapHelper) {
		if (activeContacts >= pixmapContacts.size)
			pixmapContacts.add(pixmapHelper);
		else
			pixmapContacts.set(activeContacts++, pixmapHelper);
	}
	
	public int getContactCount() {
		return activeContacts;
	}
	
	public void clearContacts() {
		activeContacts = 0;
	}
	
	public PixmapHelper getContact(int i) {
		return pixmapContacts.get(i);
	}
	
}
