package mod.world;

import mod.Mod;
import pl.shockah.terraria.Vector2i;
import pl.shockah.terraria.world.World;
import pl.shockah.terraria.world.WorldBlank;

public class WorldGen extends pl.shockah.terraria.world.WorldGen {
	public WorldGen() {
		super(Mod.mod.name);
	}

	public Vector2i getWorldSize() {
		return null;
	}

	public World generateWorld() {
		return new WorldBlank(this);
	}
}