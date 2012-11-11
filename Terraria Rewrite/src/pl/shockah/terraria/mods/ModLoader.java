package pl.shockah.terraria.mods;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pl.shockah.easyslick.App;
import pl.shockah.terraria.resources.Loader;
import pl.shockah.terraria.resources.ResourceManager;

public class ModLoader extends Loader<Mod> {
	public ModLoader(ResourceManager<Mod> resourceManager) {
		super(resourceManager);
	}

	public void run() {
		File dirMods = new File("mods");
		dirMods.mkdirs();
		
		List<File> lookIn = new ArrayList<File>();
		lookIn.add(dirMods);
		while (!lookIn.isEmpty()) {
			File check = lookIn.remove(0);
			if (check.isDirectory()) {
				if (check.getName().matches("\\.{1,2}")) continue;
				for (File file : check.listFiles()) lookIn.add(file);
			} else {
				try {
					if (check.getName().matches(".+\\.(?:zip|jar)")) resourceManager.add(check.getPath());
				} catch (Exception e) {App.getApp().handle(e);}
			}
		}
	}
	public void postLoad() {
		for (Mod mod : ((ModManager)resourceManager).getMods()) mod.onAllModsLoaded();
	}
}