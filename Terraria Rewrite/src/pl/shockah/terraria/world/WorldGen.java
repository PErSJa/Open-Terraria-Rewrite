package pl.shockah.terraria.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.shockah.terraria.Vector2i;

public abstract class WorldGen<T extends World> {
	public static List<WorldGen<? extends World>> worldGenList = Collections.synchronizedList(new ArrayList<WorldGen<? extends World>>());
	
	public static void register(WorldGen<? extends World>... worldGens) {
		for (WorldGen<? extends World> worldGen : worldGens) worldGenList.add(worldGen);
	}
	public static void unregister(WorldGen<? extends World>... worldGens) {
		for (WorldGen<? extends World> worldGen : worldGens) worldGenList.remove(worldGen);
	}
	
	public final String tag;
	
	public WorldGen(String tag) {
		this.tag = tag;
	}
	
	public void register() {
		worldGenList.add(this);
	}
	
	public abstract Vector2i getWorldSize();
	public abstract T generateWorld();
}