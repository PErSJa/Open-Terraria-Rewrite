package pl.shockah.terraria.world;

import java.util.Map;
import java.util.Map.Entry;
import pl.shockah.BinBuffer;
import pl.shockah.Util;
import pl.shockah.terraria.Vector2i;

public class World {
	public static World read(BinBuffer binb) throws WorldException {
		String worldGenTag = binb.readJavaString();
		for (WorldGen worldGen : WorldGen.worldGenList) {
			if (worldGen.tag.equals(worldGenTag)) {
				World world = new World(worldGen,(int)binb.readUInt(),(int)binb.readUInt(),false);
				
				world.data.setPos(0);
				world.data.writeBinBuffer(binb,world.width*world.height*2*2);
				
				int count;
				
				count = binb.readUShort();
				while (count-- > 0) {
					int id = binb.readUShort();
					String tag = binb.readJavaString();
					Tile tile = Tile.tagMap.get(tag);
					if (tile == null) throw new WorldException("No tile with '"+tag+"' tag found. Are you missing a mod?");
					world.idMapTiles.put(id,tile);
					if (id > world.genMapTiles) world.genMapTiles = id;
				}
				
				count = binb.readUShort();
				while (count-- > 0) {
					int id = binb.readUShort();
					String tag = binb.readJavaString();
					Wall wall = Wall.tagMap.get(tag);
					if (wall == null) throw new WorldException("No wall with '"+tag+"' tag found. Are you missing a mod?");
					world.idMapWalls.put(id,wall);
					if (id > world.genMapWalls) world.genMapWalls = id;
				}
				
				world.generateMappings();
				return world;
			}
		}
		throw new WorldException("No world gen with '"+worldGenTag+"' tag found. Are you missing a mod?");
	}
	
	protected Map<Integer,Tile> idMapTiles = Util.syncedMap(int.class,Tile.class);
	protected Map<Integer,Wall> idMapWalls = Util.syncedMap(int.class,Wall.class);
	
	protected final WorldGen worldGen;
	protected final BinBuffer data;
	protected final int width, height;
	
	protected int genMapTiles = 0, genMapWalls = 0;
	
	public int day = 0, time = 0;
	
	protected World(WorldGen worldGen, int w, int h) {
		this(worldGen,w,h,true);
	}
	protected World(WorldGen worldGen, int w, int h, boolean genMappings) {
		this.worldGen = worldGen;
		data = new BinBuffer(w*h*2*2);
		width = w;
		height = h;
		
		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) data.writeUShort(0);
		for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) data.writeUShort(0);
		
		if (genMappings) generateMappings();
	}
	
	protected void generateMappings() {
		for (Tile tile : Tile.tagMap.values()) {
			if (idMapTiles.containsValue(tile)) continue;
			idMapTiles.put(++genMapTiles,tile);
		}
		
		for (Wall wall : Wall.tagMap.values()) {
			if (idMapWalls.containsValue(wall)) continue;
			idMapWalls.put(++genMapWalls,wall);
		}
	}
	
	protected void write(BinBuffer binb) {
		binb.writeJavaString(worldGen.tag);
		
		data.setPos(0);
		binb.writeUInt(width);
		binb.writeUInt(height);
		binb.writeBinBuffer(data);
		
		binb.writeUShort(idMapTiles.size());
		for (Entry<Integer,Tile> entry : idMapTiles.entrySet()) {
			binb.writeUShort(entry.getKey());
			binb.writeJavaString(entry.getValue().tag);
		}
		
		binb.writeUShort(idMapWalls.size());
		for (Entry<Integer,Wall> entry : idMapWalls.entrySet()) {
			binb.writeUShort(entry.getKey());
			binb.writeJavaString(entry.getValue().tag);
		}
	}
	
	public int getTileID(Vector2i v) {
		data.setPos(v.x+v.y*width*2);
		return data.readUShort();
	}
	public int getWallID(Vector2i v) {
		data.setPos(width*height*2+v.x+v.y*width*2);
		return data.readUShort();
	}
	
	public void updateTime(int by) {
		time += by;
		while (time < 0) {
			time += getDayTime();
			day--;
		}
		while (time > getDayTime()) {
			time -= getDayTime();
			day++;
		}
	}
	public int getDayTime() {
		return 60*60*24;
	}
}