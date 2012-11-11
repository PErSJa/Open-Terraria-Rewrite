package pl.shockah.terraria.resources;

public abstract class Loader<T> {
	protected final ResourceManager<T> resourceManager;
	
	public Loader(ResourceManager<T> resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	public abstract void run();
	public abstract void postLoad();
}