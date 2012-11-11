package pl.shockah.terraria.game;

import java.util.List;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.Util;
import pl.shockah.easyslick.GraphicsHelper;

public abstract class Biome {
	public static final List<Biome> biomeList = Util.syncedList(Biome.class);
	
	public final String name;
	
	public Biome(String name) {
		this.name = name;
	}
	
	public abstract void onRender(GraphicsHelper gh, Vector2f offset, float alpha);
}