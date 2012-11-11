package pl.shockah.terraria.mods;

public abstract class ModImplementation extends Mod {
	public ModImplementation(String name) {
		super(name);
	}
	
	protected abstract void onModLoad();
	protected abstract void onModUnload();
	protected abstract void onAllModsLoaded();
	protected abstract void onContentLoaded();
}