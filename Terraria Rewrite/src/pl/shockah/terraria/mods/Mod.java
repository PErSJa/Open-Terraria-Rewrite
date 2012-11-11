package pl.shockah.terraria.mods;

public class Mod {
	public final String name;
	
	public Mod(String name) {
		this.name = name;
	}
	
	public final String getContentPath() {
		return "mod://"+name+"/content/";
	}
	
	protected void onModLoad() {}
	protected void onModUnload() {}
	protected void onAllModsLoaded() {}
	protected void onContentLoaded() {}
}