package pl.shockah.terraria.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Image;
import pl.shockah.terraria.rooms.RoomTitle;
import pl.shockah.terraria.sentities.SEntityLogo;

public class ImageLoader extends Loader<Image> {
	public ImageLoader(ResourceManager<Image> resourceManager) {
		super(resourceManager);
	}

	public void run() {
		File baseDir = new File("content","images");
		try {
			for (String path : loadDirectory(new File(baseDir,"splash"))) RoomTitle.splashes.add(resourceManager.add(path,"center",true));
			for (String path : loadDirectory(new File(baseDir,"logo"))) SEntityLogo.logo.add(resourceManager.add(path,"center",true));
		} catch (Exception e) {App.getApp().handle(e);}
	}
	public void postLoad() {}
	
	public List<String> loadDirectory(File dir) {
		List<String> list = new ArrayList<String>();
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) continue;
			String fname = file.getName();
			if (fname.matches(".*\\.(?:(?:png)|(?:jpg)|(?:bmp)|(?:gif))")) list.add(file.getPath());
		}
		return list;
	}
}