package pl.shockah.terraria.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class ResourceManager<T> {
	public static final Pattern
		patternFileName = Pattern.compile("([^/\\\\]+)\\.([a-zA-Z0-9]+)");
	
	public class ResourceEntry<A> {
		public final String path;
		public final Object[] data;
		private A resource;
		private boolean loaded = false;
		
		public ResourceEntry(String path, Object[] data) {
			this.path = path;
			this.data = data;
		}
		
		public A get() {
			if (!loaded) return null;
			return resource;
		}
		@SuppressWarnings("unchecked") public A load() {
			if (loaded) return resource;
			
			resource = (A)loadResource(path,data);
			loaded = true;
			return resource;
		}
	}
	
	protected List<ResourceEntry<T>> resources;
	protected Loader<T> loader;
	
	public ResourceManager() {
		this(null);
	}
	public ResourceManager(Loader<T> loader) {
		this.resources = Collections.synchronizedList(new ArrayList<ResourceEntry<T>>());
		this.loader = loader;
	}
	
	public int add(String path, Object... data) {
		resources.add(new ResourceEntry<T>(path,data));
		return resources.size()-1;
	}
	public T get(int i) {
		return resources.get(i).get();
	}
	
	public void setLoader(Loader<T> loader) {
		this.loader = loader;
	}
	public boolean updateLoading() {
		for (int i = 0; i < resources.size(); i++) if (resources.get(i).get() == null) {
			resources.get(i).load();
			return false;
		}
		if (loader != null) {
			loader.postLoad();
			loader = null;
		}
		return true;
	}
	
	public abstract T loadResource(String path, Object... info);
	public void autoLoad() {
		if (loader != null) loader.run();
	}
	public Progress getProgress() {
		int loaded = 0, total = 0;
		String text = null;
		for (int i = 0; i < resources.size(); i++) {
			total++;
			if (resources.get(i).get() != null) loaded++; else if (text == null) text = resources.get(i).path;
		}
		return new Progress(1d*loaded/total,text);
	}
}