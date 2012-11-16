package pl.shockah.terraria.xnb;

import java.io.File;
import pl.shockah.BinBuffer;
import pl.shockah.BinFile;
import pl.shockah.easyslick.App;

public class Converter {
	public static File convertWAV(File source) {return convertWAV(source,null);}
	public static File convertWAV(File source, File destination) {
		try {
			if (destination == null) destination = File.createTempFile(source.getName(),".wav");
			BinBuffer binb = new BinFile(source).read();
			binb.setPos(0);
			
			if (!binb.readChars(3).equals("XNB")) throw new XNBException("Invalid file format.");
			if (!binb.readChars(1).equals("w")) throw new XNBException("Invalid platform.");
			if (binb.readByte() != 5) throw new XNBException("Unimplemented XNA version.");
			if (binb.readByte() != 0) throw new XNBException("Unimplemented profile.");
			if ((int)binb.readUInt() != binb.getSize()) throw new XNBException("File length mismatch.");
			if (binb.readByte() != 1) throw new XNBException("Too many types.");
			if (!binb.readChars(binb.readByte()).equals("Microsoft.Xna.Framework.Content.SoundEffectReader")) throw new XNBException("Wrong type reader name.");
			if (binb.readInt() != 0) throw new XNBException("Wrong type reader version.");
			if (binb.readByte() != 0) throw new XNBException("Too many shared resources.");
			if (read7BitEncodedInt(binb) != 1) throw new XNBException("???");
			
			long nSamplesPerSec, nAvgBytesPerSec;
			int wFormatTag, nChannels, nBlockAlign, wBitsPerSample, dataChunkSize;
			BinBuffer waveData = new BinBuffer();
			
			if (binb.readUInt() != 18) throw new XNBException("Wrong format chunk size.");
			if ((wFormatTag = binb.readUShort()) != 1) throw new XNBException("Unimplemented WAV codec (must be PCM).");
			nChannels = binb.readUShort();
			nSamplesPerSec = binb.readUInt();
			nAvgBytesPerSec = binb.readUInt();
			nBlockAlign = binb.readUShort();
			wBitsPerSample = binb.readUShort();
			
			if (nAvgBytesPerSec != nSamplesPerSec*nChannels*(wBitsPerSample/8)) throw new XNBException("Average bytes per second number incorrect.");
			if (nBlockAlign != nChannels*(wBitsPerSample/8)) throw new XNBException("Block align number incorrect.");
			binb.setPos(binb.getPos()+2);
			
			waveData.writeBinBuffer(binb,dataChunkSize = binb.readInt());
			
			
			
			binb.clear();
			waveData.setPos(0);
			binb.writeChars("RIFF");
			binb.writeInt(dataChunkSize+36);
			binb.writeChars("WAVE");
			binb.writeChars("fmt ");
			binb.writeInt(16);
			binb.writeUShort(wFormatTag);
			binb.writeUShort(nChannels);
			binb.writeUInt(nSamplesPerSec);
			binb.writeUInt(nAvgBytesPerSec);
			binb.writeUShort(nBlockAlign);
			binb.writeUShort(wBitsPerSample);
			binb.writeChars("data");
			binb.writeInt(dataChunkSize);
			binb.writeBinBuffer(waveData);
			
			binb.setPos(0);
			new BinFile(destination).write(binb);
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	public static File convertPNG(File source) {return convertPNG(source,null);}
	public static File convertPNG(File source, File destination) {
		try {
			if (destination == null) destination = File.createTempFile(source.getName(),".png");
			BinBuffer binb = new BinFile(source).read();
			binb.setPos(0);
			
			if (!binb.readChars(3).equals("XNB")) throw new XNBException("Invalid file format.");
			if (!binb.readChars(1).equals("w")) throw new XNBException("Invalid platform.");
			if (binb.readByte() != 5) throw new XNBException("Unimplemented XNA version.");
			if (binb.readByte() != 128) throw new XNBException("Unimplemented profile.");
			if ((int)binb.readUInt() != binb.getSize()) throw new XNBException("File length mismatch.");
			
			//TODO: implement XNB -> PNG conversion [ http://xbox.create.msdn.com/en-US/sample/xnb_format ]
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	//TODO: implement XNB -> font conversion [ http://xbox.create.msdn.com/en-US/sample/xnb_format ]
	
	private static int read7BitEncodedInt(BinBuffer binb) {
	    int result = 0, bitsRead = 0, value;
	    do {
	        value = binb.readByte();
	        result |= (value & 0x7f) << bitsRead;
	        bitsRead += 7;
	    } while ((value & 0x80) != 0);
	    return result;
	}
}