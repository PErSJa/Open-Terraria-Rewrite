package pl.shockah.terraria.mods;

import pl.shockah.Config;
import pl.shockah.terraria.Terraria;

public abstract class Mod {
	public static Config config = new Config();
	
	public static boolean isModLoaded(Mod mod) {
		return isModLoaded(mod.name);
	}
	public static boolean isModLoaded(String name) {
		for (Mod mod : Terraria.managerMod.getMods()) {
			if (mod.name.equals(name)) {
				if (!config.exists(name)) config.set(name,true);
				return config.getBoolean(name);
			}
		}
		return false;
	}
	
	public final String name;
	
	public Mod(String name) {
		this.name = name;
	}
	
	public final String getContentPath() {
		return "mod://"+name+"/content/";
	}
	
	public abstract String getTitle();
	public abstract String getDescription();
	
	protected void onModLoad() {}
	protected void onModUnload() {}
	protected void onAllModsLoaded() {}
	protected void onContentLoaded() {}
}