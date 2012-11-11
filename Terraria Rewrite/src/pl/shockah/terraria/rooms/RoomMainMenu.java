package pl.shockah.terraria.rooms;

import pl.shockah.easyslick.View;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.sentities.SEntityBackgrounds;
import pl.shockah.terraria.sentities.SEntityLogo;
import pl.shockah.terraria.world.WorldBlank;

public class RoomMainMenu extends Room {
	protected void onCreate() {
		Terraria.worlds.add(new WorldBlank());
		new SEntityBackgrounds().create();
		
		View view = View.getDefault();
		new SEntityLogo().create(view.pos.x+view.size.x/2f,view.pos.y+100);
	}
	protected void onEnd() {
		Terraria.worlds.remove(0);
	}
}