package pl.shockah.terraria.world;

import java.util.List;

import pl.shockah.Util;
import pl.shockah.terraria.Vector2i;

public abstract class WorldGen {
	public static List<WorldGen> worldGenList = Util.syncedList(WorldGen.class);
	
	public static void register(WorldGen... worldGens) {
		for (WorldGen worldGen : worldGens) worldGenList.add(worldGen);
	}
	public static void unregister(WorldGen... worldGens) {
		for (WorldGen worldGen : worldGens) worldGenList.remove(worldGen);
	}
	
	public final String tag;
	
	public WorldGen(String tag) {
		this.tag = tag;
	}
	
	public abstract Vector2i getWorldSize();
	public abstract World generateWorld();
}