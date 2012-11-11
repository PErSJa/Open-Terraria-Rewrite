package pl.shockah.terraria.rooms;

import org.newdawn.slick.Color;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.resources.Progress;
import pl.shockah.terraria.resources.ResourceManager;

public class RoomLoading extends Room {
	protected void onCreate() {
		for (ResourceManager<?> manager : Terraria.managers) manager.autoLoad();
	}
	protected void onTick(int delta) {
		for (ResourceManager<?> manager : Terraria.managers) if (!manager.updateLoading()) return;
		Terraria.managerMod.onContentLoaded();
		Room.set(new RoomTitle());
	}
	protected void onRender(GraphicsHelper gh) {
		Progress progress = null;
		for (ResourceManager<?> manager : Terraria.managers) if (progress == null || progress.percent >= 1d) progress = manager.getProgress();
		
		gh.g().setColor(Color.white);
		gh.g().drawString("Loading: "+progress.text,10,10);
	}
}