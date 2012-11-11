package pl.shockah.terraria.world;

import java.util.Map;
import pl.shockah.Util;

public class Tile {
	public static Map<String,Tile> tagMap = Util.syncedMap(String.class,Tile.class);
	
	public final String tag;
	
	public Tile(String tag) {
		this.tag = tag;
	}
}