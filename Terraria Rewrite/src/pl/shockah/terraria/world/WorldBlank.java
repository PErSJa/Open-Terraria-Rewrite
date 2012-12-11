package pl.shockah.terraria.world;

import pl.shockah.BinBuffer;
import pl.shockah.easyslick.App;
import pl.shockah.terraria.Vector2i;

public class WorldBlank extends World {
	public WorldBlank(WorldGen<? extends WorldBlank> worldGen) {
		super(worldGen,0,0);
	}
	
	protected void generateMappings() {}
	
	protected void write(BinBuffer binb) {
		try {
			throw new WorldException("Can't use a blank world.");
		} catch (WorldException e) {App.getApp().handle(e);}
	}
	
	public int getTileID(Vector2i v) {
		try {
			throw new WorldException("Can't use a blank world.");
		} catch (WorldException e) {App.getApp().handle(e);}
		return -1;
	}
	public int getWallID(Vector2i v) {
		try {
			throw new WorldException("Can't use a blank world.");
		} catch (WorldException e) {App.getApp().handle(e);}
		return -1;
	}
}