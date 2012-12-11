package pl.shockah.terraria.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import pl.shockah.easyslick.App;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.mods.Mod;

public abstract class ResourceManager<T> {
	public static final Pattern
		patternModProtocol = Pattern.compile("mod://([^/\\\\]+)[/\\\\]((?:[^/\\\\]+[/\\\\])*[^/\\\\]+)"),
		patternFileName = Pattern.compile("([^/\\\\]+)\\.([a-zA-Z0-9]+)");
	
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
	
	public int add(String path, Object... data) throws ResourceException {
		resources.add(new ResourceEntry<T>(path,data));
		return resources.size()-1;
	}
	public T get(int i) throws ResourceException {
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