package pl.shockah.terraria.xnb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import org.newdawn.slick.imageout.ImageOut;
import pl.shockah.BinBuffer;
import pl.shockah.BinFile;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Image;

public final class Converter {
	public static File convertTexture2D(File source) {
		return convertTexture2D(source,null);
	}
	public static File convertTexture2D(File source, File target) {
		try {
			if (target == null) target = File.createTempFile(source.getName(),".png");
			
			XNBReader xnbReader = new XNBReader(source);
			XNBData read = xnbReader.read(true);
			
			Image image = (Image)read.assetData;
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
			ImageOut.write(image,ImageOut.PNG,bos);
			bos.close();
			
			return target;
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	public static File convertSoundEffect(File source) {
		return convertSoundEffect(source,null);
	}
	public static File convertSoundEffect(File source, File target) {
		try {
			if (target == null) target = File.createTempFile(source.getName(),".wav");
			
			XNBReader xnbReader = new XNBReader(source);
			XNBData read = xnbReader.read(true);
			
			BinBuffer binb = (BinBuffer)read.assetData;
			new BinFile(target).write(binb);
			
			return target;
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
}