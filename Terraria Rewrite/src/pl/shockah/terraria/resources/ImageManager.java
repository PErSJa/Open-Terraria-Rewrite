package pl.shockah.terraria.resources;

import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Image;
import pl.shockah.terraria.mods.ModManager;

public class ImageManager extends ResourceManager<Image> {
	public ImageManager() {
		this(null);
	}
	public ImageManager(Loader<Image> loader) {
		super(loader);
	}

	public Image loadResource(String path, Object... info) {
		try {
			if (path.matches(ModManager.patternModProtocol.toString())) path = ModManager.unpackModFile(path).getAbsolutePath();
			
			boolean center = false;
			
			for (int i = 0; i < info.length; i++) {
				assert info[i] instanceof String;
				if (((String)info[i]).equalsIgnoreCase("center")) center = (Boolean)info[++i];
			}
			
			Image image = new Image(path);
			if (center) image.center();
			return image;
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
}