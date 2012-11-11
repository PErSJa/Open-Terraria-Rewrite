package pl.shockah.terraria.rooms;

import pl.shockah.terraria.Vector2i;

public class Room extends pl.shockah.easyslick.Room {
	protected static Vector2i sizeBase = new Vector2i(800,600);
	
	protected void setupRoom() {
		setSize(sizeBase.x,sizeBase.y);
		maxFPS = 60;
	}
}