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
		if (!binb.readChars(3).equals("XNB")) throw new XNBException("Invalid file format.");
		
		data.targetPlatform = ETargetPlatform.getByChar((char)binb.readByte());
		if (data.targetPlatform == null || (strict && data.targetPlatform != ETargetPlatform.Windows)) throw new XNBException("Invalid platform.");
		
		data.formatVersion = binb.readByte();
		if (strict && data.formatVersion != 5) throw new XNBException("Unimplemented XNA version.");
		
		int b = binb.readByte();
		data.flagHiDef = (b & 0x01) != 0;
		data.flagCompressed = (b & 0x80) != 0;
		if (strict && data.flagCompressed) throw new XNBException("Unimplemented flag.");
		
		data.sizeTotal = (int)binb.readUInt();
		if (data.flagCompressed) data.sizeDecompressed = (int)binb.readUInt();
		
		int count;
		
		count = read7BitEncodedInt(binb);
		if (count == 0) throw new XNBException("No readers.");
		if (strict && count > 1) throw new XNBException("Too many readers.");
		while (count-- > 0) data.readers.add(new Pair<String,Integer>(readCSharpString(binb),binb.readInt()));
		
		count = read7BitEncodedInt(binb);
		if (strict && count > 0) throw new XNBException("Too many shared resources.");
		
		XNBObjectReader<?> reader = getObjectReader(data.readers.get(0).get1());
		if (reader == null) throw new XNBException("No applicable readers.");
		
		data.assetData = reader.read(binb);
		while (count-- > 0) data.sharedResources.add(reader.read(binb));
		return data;
	}
	
	protected static String readCSharpString(BinBuffer binb) {
		int length = read7BitEncodedInt(binb);
		StringBuilder sb = new StringBuilder();
		while (length-- > 0) sb.append(readCSharpChar(binb));
		return sb.toString();
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
	    int result = 0, bitsRead = 0, value;
	    do {
	        value = binb.readByte();
	        result |= (value & 0x7f) << bitsRead;
	        bitsRead += 7;
	    } while ((value & 0x80) != 0);
	    return result;
	}
}