package pl.shockah.terraria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pl.shockah.Util;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.IAppHooks;
import pl.shockah.terraria.mods.ModLoader;
import pl.shockah.terraria.mods.ModManager;
import pl.shockah.terraria.resources.FontLoader;
import pl.shockah.terraria.resources.FontManager;
import pl.shockah.terraria.resources.ImageLoader;
import pl.shockah.terraria.resources.ImageManager;
import pl.shockah.terraria.resources.ResourceManager;
import pl.shockah.terraria.rooms.RoomLoading;
import pl.shockah.terraria.world.World;

public class Terraria implements IAppHooks {
	public static final List<String> titles = Util.syncedList(String.class);
	public static final List<ResourceManager<?>> managers = Collections.synchronizedList(new ArrayList<ResourceManager<?>>());
	public static final ModManager managerMod = new ModManager(256);
	public static final ImageManager managerImage = new ImageManager(256);
	public static final FontManager managerFont = new FontManager(4);
	
	public static List<World> worlds = Util.syncedList(World.class);
	
	static {
		titles.addAll(Arrays.asList(new String[]{
				"Terraria: Dig Peon, Dig!",
				"Terraria: Epic Dirt",
				"Terraria: Hey Guys!",
				"Terraria: Sand is Overpowered",
				"Terraria Part 3: The Return of the Guide",
				"Terraria: A Bunnies Tale",
				"Terraria: Dr. Bones and The Temple of Blood Moon",
				"Terraria: Slimeassic Park",
				"Terraria: The Grass is Greener on This Side",
				"Terraria: Small Blocks, Not for Children Under the Age of 5",
				"Terraria: Digger T' Blocks",
				"Terraria: There is No Cow Layer",
				"Terraria: Suspicous Looking Eyeballs",
				"Terraria: Purple Grass!",
				"Terraria: Noone Dug Behind!",
				"Terraria: Shut Up and Dig Gaiden!"
		}));
		
		managerMod.setLoader(new ModLoader(managerMod));
		managers.add(managerMod);
		
		managerImage.setLoader(new ImageLoader(managerImage));
		managers.add(managerImage);
		
		managerFont.setLoader(new FontLoader(managerFont));
		managers.add(managerFont);
	}
	
	public static void main(String[] args) {
		App.start(new Terraria(),new RoomLoading(),Util.getRandom(titles),false);
	}
	
	public void onInit() {}
	public void onDeinit() {}
	public void onException(Throwable t) {}
	public void preRender(GraphicsHelper gh) {}
	public void onRender(GraphicsHelper gh) {}
	public void onTick(int delta) {}
}