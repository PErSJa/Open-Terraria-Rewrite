package pl.shockah.terraria.importer;

import java.io.File;
import pl.shockah.BinBuffer;
import pl.shockah.BinFile;
import pl.shockah.easyslick.App;

public class Importer {
	public static File importWAV(File source) {return importWAV(source,null);}
	public static File importWAV(File source, File destination) {
		try {
			if (destination == null) destination = File.createTempFile(source.getName(),".wav");
			BinBuffer binb = new BinFile(source).read();
			binb.setPos(0);
			
			if (!binb.readUChars(3).equals("XNB")) throw new ImportException("Invalid file format");
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
}