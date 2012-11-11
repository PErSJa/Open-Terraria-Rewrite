package pl.shockah.terraria.world;

import java.util.Map;
import pl.shockah.Util;

public class Wall {
	public static Map<String,Wall> tagMap = Util.syncedMap(String.class,Wall.class);
	
	public final String tag;
	
	public Wall(String tag) {
		this.tag = tag;
	}
}