package pl.shockah.terraria;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pl.shockah.Config;
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
	public static final String version;
	
	public static final List<String> titles = Util.syncedList(String.class);
	public static final List<ResourceManager<?>> managers = Collections.synchronizedList(new ArrayList<ResourceManager<?>>());
	public static final ModManager managerMod = new ModManager(256);
	public static final ImageManager managerImage = new ImageManager(256);
	public static final FontManager managerFont = new FontManager(4);
	
	public static List<World> worlds = Util.syncedList(World.class);
	
	static {
		Config cfg = null;
		try {
			cfg = new Config().load(Terraria.class.getClassLoader().getResourceAsStream("buildinfo.cfg"));
		} catch (Exception e) {App.getApp().handle(e);}
		version = cfg == null ? null : "build #"+cfg.getInt("build")+", built on "+cfg.getString("dateText");
		
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
		setupLWJGL();
		App.start(new Terraria(),new RoomLoading(),Util.getRandom(titles),false);
	}
	
	private static void setupLWJGL() {
		String osName = System.getProperty("os.name");
		String nativeDir = "";
		try {
			nativeDir = new File(Terraria.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
		} catch (Exception e) {
			try {
				e.printStackTrace();
				nativeDir = new File(".").getCanonicalPath();
			} catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("Failed to locate native library directory. Error:\n"+e2);
				System.exit(-1);
			}
		}
		nativeDir += File.separator+"libs"+File.separator;
		if (osName.startsWith("Windows")) nativeDir += "windows";
		else if (osName.startsWith("Linux") || osName.startsWith("FreeBSD")) nativeDir += "linux";
		else if (osName.startsWith("Mac OS X")) nativeDir += "macosx";
		else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) nativeDir += "solaris";
		else {
			System.out.println("Unsupported OS: "+osName+". Exiting.");
			System.exit(-1);
		}
		System.setProperty("org.lwjgl.librarypath",nativeDir);
	}
	
	public void onInit() {}
	public void onDeinit() {}
	public void onException(Throwable t) {}
	public void preRender(GraphicsHelper gh) {}
	public void onRender(GraphicsHelper gh) {}
	public void onTick(int delta) {}
}