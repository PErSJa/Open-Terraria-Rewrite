package pl.shockah.terraria.resources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.newdawn.slick.Font;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Fonts;
import pl.shockah.terraria.mods.ModManager;

public class FontManager extends ResourceManager<Font> {
	public FontManager() {
		this(null);
	}
	public FontManager(Loader<Font> loader) {
		super(loader);
	}

	public Font loadResource(String path, Object... info) {
		try {
			if (path.matches(ModManager.patternModProtocol.toString())) path = ModManager.unpackModFile(path).getAbsolutePath();
			
			int size = 12;
			boolean bold = false, italic = false;
			
			for (int i = 0; i < info.length; i++) {
				assert info[i] instanceof String;
				if (((String)info[i]).equalsIgnoreCase("size")) size = (Integer)info[++i];
				else if (((String)info[i]).equalsIgnoreCase("bold")) bold = (Boolean)info[++i];
				else if (((String)info[i]).equalsIgnoreCase("italic")) italic = (Boolean)info[++i];
			}
			
			return Fonts.newUnicodeFontFile(path,size,bold,italic);
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	public Font get(String name) {return get(name,null);}
	public Font get(String name, Integer size) {return get(name,size,null,null);}
	public Font get(String name, Integer size, Boolean bold, Boolean italic) {
		Pattern pattern = Pattern.compile("[\\/]?(?:[^\\/]+[\\/])*([^\\/]+)\\.[a-zA-Z0-9]+");
		for (int i = 0; i < resources.size(); i++) {
			ResourceEntry<Font> entry = resources.get(i);
			Matcher matcher = pattern.matcher(entry.path);
			if (!matcher.find() || !matcher.group(1).equalsIgnoreCase(name)) continue;
			
			int eSize = 12;
			boolean eBold = false, eItalic = false;
			
			for (int j = 0; j < entry.data.length; j++) {
				assert entry.data[j] instanceof String;
				if (((String)entry.data[j]).equalsIgnoreCase("size")) eSize = (Integer)entry.data[++j];
				else if (((String)entry.data[j]).equalsIgnoreCase("bold")) eBold = (Boolean)entry.data[++j];
				else if (((String)entry.data[j]).equalsIgnoreCase("italic")) eItalic = (Boolean)entry.data[++j];
			}
			
			if (size != null && size != eSize) continue;
			if (bold != null && bold != eBold) continue;
			if (italic != null && italic != eItalic) continue;
			return entry.get();
		}
		return null;
	}
}