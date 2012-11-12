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
			
			if (!binb.readChars(3).equals("XNB")) throw new ImportException("Invalid file format.");
			if (!binb.readChars(1).equals("w")) throw new ImportException("Invalid platform.");
			if (binb.readByte() != 5) throw new ImportException("Unimplemented XNA version.");
			if (binb.readByte() != 0) throw new ImportException("Unimplemented profile.");
			if ((int)binb.readUInt() != binb.getSize()) throw new ImportException("File length mismatch.");
			if (binb.readByte() != 1) throw new ImportException("Too many types.");
			if (!binb.readChars(binb.readByte()).equals("Microsoft.Xna.Framework.Content.SoundEffectReader")) throw new ImportException("Wrong type reader name.");
			if (binb.readInt() != 0) throw new ImportException("Wrong type reader version.");
			if (binb.readByte() != 0) throw new ImportException("Too many shared resources.");
			if (read7BitEncodedInt(binb) != 1) throw new ImportException("???");
			
			long nSamplesPerSec, nAvgBytesPerSec;
			int wFormatTag, nChannels, nBlockAlign, wBitsPerSample, dataChunkSize;
			BinBuffer waveData = new BinBuffer();
			
			if (binb.readUInt() != 18) throw new ImportException("Wrong format chunk size.");
			if ((wFormatTag = binb.readUShort()) != 1) throw new ImportException("Unimplemented WAV codec (must be PCM).");
			nChannels = binb.readUShort();
			nSamplesPerSec = binb.readUInt();
			nAvgBytesPerSec = binb.readUInt();
			nBlockAlign = binb.readUShort();
			wBitsPerSample = binb.readUShort();
			
			if (nAvgBytesPerSec != nSamplesPerSec*nChannels*(wBitsPerSample/8)) throw new ImportException("Average bytes per second number incorrect.");
			if (nBlockAlign != nChannels*(wBitsPerSample/8)) throw new ImportException("Block align number incorrect.");
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
	
	private static int read7BitEncodedInt(BinBuffer binb) {
		int num = 0;
		int num2 = 0;
		while (num2 != 35) {
			int b = binb.readByte();
			num |= (b & 127) << num2;
			num2 += 7;
			if ((b & 128) == 0) return num;
		}
		try {
			throw new ImportException("Failed to read a Microsoft 7-bit encoded integer.");
		} catch (Exception e) {App.getApp().handle(e);}
		return -1;
	}
}