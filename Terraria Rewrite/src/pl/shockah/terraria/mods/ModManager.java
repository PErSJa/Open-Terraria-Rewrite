package pl.shockah.terraria.mods;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;
import pl.shockah.easyslick.App;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.resources.Loader;
import pl.shockah.terraria.resources.ResourceManager;

public class ModManager extends ResourceManager<Mod> {
	public static final Pattern
		patternModProtocol = Pattern.compile("mod://([^/\\\\]+)[/\\\\]((?:[^/\\\\]+[/\\\\])*[^/\\\\]+)"),
		patternModDataProtocol = Pattern.compile("modd://([^/\\\\]+)[/\\\\]((?:[^/\\\\]+[/\\\\])*[^/\\\\]+)");
	
	public static File unpackModFile(String pathModProtocol) {
		Matcher matcher = patternModProtocol.matcher(pathModProtocol);
		if (matcher.find()) {
			Mod mod = Terraria.managerMod.getMod(matcher.group(1));
			if (mod == null) return null;
			
			try {
				ZipFile zf = new ZipFile(new File("mods",mod.name+".jar"));
				BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(zf.getEntry(matcher.group(2))));
				
				int b;
                byte[] buffer = new byte[1024];
                
                String[] spl = matcher.group(2).split("[/\\\\]");
                
                Matcher matcher2 = patternFileName.matcher(spl[spl.length-1]);
                if (matcher2.find()) {
	                File ret = File.createTempFile(matcher2.group(1),"."+matcher2.group(2));
	                
	                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ret),buffer.length);
	                while ((b = bis.read(buffer,0,1024)) != -1) bos.write(buffer,0,b);
	                bos.flush(); bos.close();
	                bis.close();
	                
	                return ret;
                }
			} catch (Exception e) {App.getApp().handle(e);}
		}
		return null;
	}
	
	public static String getPath(String path) {
		if (path.matches(patternModProtocol.toString())) path = ModManager.unpackModFile(path).getAbsolutePath();
		else {
			Matcher matcher = patternModDataProtocol.matcher(path);
			if (matcher.find()) {
				File f = new File(new File(new File("mods","data"),matcher.group(1)),matcher.group(2));
				f.getParentFile().mkdirs();
				path = f.getAbsolutePath();
			}
		}
		return path;
	}
	
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
	
	public ModManager() {
		this(null);
	}
	public ModManager(Loader<Mod> loader) {
		super(loader);
	}
	
	public Mod loadResource(String path, Object... info) {
		try {
			path = getPath(path);
			
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