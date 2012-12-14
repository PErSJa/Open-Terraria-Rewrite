package pl.shockah.terraria.xnb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.shockah.BinBuffer;
import pl.shockah.BinFile;
import pl.shockah.Pair;
import pl.shockah.easyslick.App;

public class XNBReader {
	private static final List<XNBObjectReader<?>> readers = Collections.synchronizedList(new ArrayList<XNBObjectReader<?>>());
	
	static {
		readers.add(new XNBCharReader());
		readers.add(XNBListReader.createReader(readers.get(readers.size()-1)));
		readers.add(XNBNullableReader.createReader(readers.get(readers.size()-2)));
		
		readers.add(new XNBRectangleReader());
		readers.add(XNBListReader.createReader(readers.get(readers.size()-1)));
		
		readers.add(new XNBVector2Reader());
		
		readers.add(new XNBVector3Reader());
		readers.add(XNBListReader.createReader(readers.get(readers.size()-1)));
		
		readers.add(new XNBVector4Reader());
		
		readers.add(new XNBColorReader());
		
		readers.add(new XNBTexture2DReader());
		
		readers.add(new XNBSpriteFontReader());
		
		readers.add(new XNBSoundEffectReader());
	}
	
	public static XNBObjectReader<?> getObjectReader(String type) {
		for (XNBObjectReader<?> reader : readers) if (reader.type.equals(type)) return reader;
		return null;
	}
	
	public final File file;
	
	public XNBReader(File file) {
		this.file = file;
	}
	
	public XNBData read(boolean strict) throws XNBException,FileNotFoundException,IOException {
		XNBData data = new XNBData();
		
		BinBuffer binb = new BinFile(file).read();
		binb.setPos(0);
		if (!binb.readChars(3).equals("XNB")) throw new XNBException("Invalid file format.");
		
		data.targetPlatform = ETargetPlatform.getByChar((char)binb.readByte());
		if (data.targetPlatform == null || (strict && data.targetPlatform != ETargetPlatform.Windows)) throw new XNBException("Invalid platform.");
		
		data.formatVersion = binb.readByte();
		if (strict && data.formatVersion != 5) throw new XNBException("Unimplemented XNA version.");
		
		int b = binb.readByte();
		data.flagHiDef = (b & 0x01) != 0;
		data.flagCompressed = (b & 0x80) != 0;
		
		data.sizeTotal = (int)binb.readUInt();
		if (strict && data.sizeTotal != binb.getSize()) throw new XNBException("Corrupted file.");
		
		if (data.flagCompressed) {
			data.sizeDecompressed = (int)binb.readUInt();
			
			try {
				LZXDecoder decoder = new LZXDecoder(16);
				BinBuffer binb2 = new BinBuffer();
				int pos = binb.getPos();
				while (binb.bytesLeft() > 0) {
					int hi, lo, block_size, frame_size;
					hi = binb.readByte();
					
					if (hi == 0xFF) {
                        hi = binb.readByte();
                        lo = binb.readByte();
                        frame_size = (hi << 8) | lo;
                        hi = binb.readByte();
                        lo = binb.readByte();
                        block_size = (hi << 8) | lo;
                    } else {
                        lo = binb.readByte();
                        block_size = (hi << 8) | lo;
                        frame_size = 32768;
                    }
					
					if (block_size == 0 || frame_size == 0) break;
					decoder.Decompress(binb,block_size,binb2,frame_size);
					binb.setPos(pos);
				}
				binb2.setPos(0);
				binb = binb2;
			} catch (LZXException e) {App.getApp().handle(e);}
			
			if (strict && data.sizeDecompressed != binb.getSize()) throw new XNBException("Corrupted file.");
		}
		
		int count;
		
		count = read7BitEncodedInt(binb);
		if (count == 0) throw new XNBException("No readers.");
		if (strict && count > 1) throw new XNBException("Too many readers.");
		while (count-- > 0) data.readers.add(new Pair<String,Integer>(readCSharpString(binb),binb.readInt()));
		
		count = read7BitEncodedInt(binb);
		if (strict && count > 0) throw new XNBException("Too many shared resources.");
		
		XNBObjectReader<?> reader = getObjectReader(data.readers.get(0).get1());
		if (reader == null) throw new XNBException("No applicable readers for "+data.readers.get(0).get1()+".");
		
		data.assetData = reader.read(binb);
		while (count-- > 0) data.sharedResources.add(reader.read(binb));
		return data;
	}
	
	protected String readCSharpString(BinBuffer binb) {
		byte[] buffer = new byte[read7BitEncodedInt(binb)];
		for (int i = 0; i < buffer.length; i++) buffer[i] = (byte)binb.readSByte();
		return new String(buffer);
	}
	protected static char readCSharpChar(BinBuffer binb) {
		char result = (char)binb.readByte();
		if ((result & 0x80) != 0) {
			int bytes = 1;
			while ((result & (0x80 >> bytes)) != 0) bytes++;
			result &= (1 << (8-bytes))-1;
			while (--bytes > 0) {
				result <<= 6;
				result |= binb.readByte() & 0x3F;
			}
		}
		return result;
	}
	protected static int read7BitEncodedInt(BinBuffer binb) {
	    int result = 0, bitsRead = 0;
	    while (true) {
	    	int value = binb.readByte();
	    	result += (value%128) << bitsRead;
	    	bitsRead += 7;
	    	if (result <= 127) return result;
	    }
	}
	
	protected native byte[] nativeDecompressLZX(byte[] bytes);
}