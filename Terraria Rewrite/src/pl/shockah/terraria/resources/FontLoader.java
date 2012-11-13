package pl.shockah.terraria.resources;

import java.io.File;
import org.newdawn.slick.Font;
import pl.shockah.easyslick.App;

public class FontLoader extends Loader<Font> {
	public FontLoader(ResourceManager<Font> resourceManager) {
		super(resourceManager);
	}

	public void run() {
		File baseDir = new File("content","fonts");
		try {
			resourceManager.add(new File(baseDir,"AstersHand.ttf").getPath(),"size",32);
			resourceManager.add(new File(baseDir,"AstersHand.ttf").getPath(),"size",16);
		} catch (Exception e) {App.getApp().handle(e);}
	}
	public void postLoad() {}
}