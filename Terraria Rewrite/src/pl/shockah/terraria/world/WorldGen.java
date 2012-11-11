package pl.shockah.terraria.world;

import java.util.Map;
import pl.shockah.Util;
import pl.shockah.terraria.Vector2i;

public abstract class WorldGen {
	public static Map<String,WorldGen> tagMap = Util.syncedMap(String.class,WorldGen.class);
	
	public final String tag;
	
	public WorldGen(String tag) {
		this.tag = tag;
	}
	
	public abstract Vector2i getWorldSize();
	public abstract World generateWorld();
}