package pl.shockah.terraria.mods;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import pl.shockah.easyslick.App;
import pl.shockah.terraria.resources.Loader;
import pl.shockah.terraria.resources.ResourceManager;

public class ModManager extends ResourceManager<Mod> {
	public static URLClassLoader newClassLoader(String modPath) {
		File dirLibs = new File("libs");
		dirLibs.mkdirs();
		
		List<URL> list = new ArrayList<URL>();
		List<File> lookIn = new ArrayList<File>();
		lookIn.add(dirLibs);
		
		while (!lookIn.isEmpty()) {
			File check = lookIn.remove(0);
			if (check.isDirectory()) {
				if (check.getName().matches("\\.{1,2}")) continue;
				for (File file : check.listFiles()) lookIn.add(file);
			} else {
				try {
					if (check.getName().matches(".+\\.(?:zip|jar)")) list.add(check.toURI().toURL());
				} catch (Exception e) {App.getApp().handle(e);}
			}
		}
		
		try {
			list.add(new File(modPath).toURI().toURL());
		} catch (Exception e) {App.getApp().handle(e);}
		return new URLClassLoader(list.toArray(new URL[list.size()]),ModManager.class.getClassLoader());
	}
	
	public ModManager(int resources) {
		this(resources,null);
	}
	public ModManager(int resources, Loader<Mod> loader) {
		super(resources,loader);
	}
	
	public Mod loadResource(String path, Object... info) {
		try {
			if (path.matches(ModManager.patternModProtocol.toString())) path = ModManager.unpackModFile(path).getAbsolutePath();
			
			URLClassLoader cl = newClassLoader(path);
			Class<?> cls = cl.loadClass("mod.Mod");
			if (cls != null) {
				Constructor<?> ctr;
				try {
					ctr = cls.getConstructor();
					Mod mod = (Mod)ctr.newInstance();
					if (mod != null) {
						mod.onModLoad();
						return mod;
					}
				} catch (NoSuchMethodException e) {
					ctr = cls.getConstructor(String.class);
					String name = new File(path).getName();
					Mod mod = (Mod)ctr.newInstance(name.substring(0,name.length()-4));
					if (mod != null) {
						mod.onModLoad();
						return mod;
					}
				}
			}
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	public void unload() {
		for (ResourceEntry<Mod> entry : resources) if (entry != null) entry.get().onModUnload();
	}
	
	public Mod getMod(String name) {
		for (ResourceEntry<Mod> entry : resources) if (entry != null && entry.get().name.equalsIgnoreCase(name)) return entry.get();
		return null;
	}
	public List<Mod> getMods() {
		List<Mod> ret = new ArrayList<Mod>();
		for (ResourceEntry<Mod> entry : resources) if (entry != null) ret.add(entry.get());
		return ret;
	}
	
	
	
	public void onContentLoaded() {
		for (ResourceEntry<Mod> entry : resources) if (entry != null) entry.get().onContentLoaded();
	}
}