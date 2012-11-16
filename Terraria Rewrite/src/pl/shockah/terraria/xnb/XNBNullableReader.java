package pl.shockah.terraria.xnb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.shockah.BinBuffer;

public class XNBNullableReader extends XNBObjectReader<Object> {
	protected static XNBNullableReader createReader(XNBObjectReader<?> reader) {
		String[] split = reader.getClass().getName().split("\\.");
		Matcher matcher = Pattern.compile("XNB(.+)Reader").matcher(split[split.length-1]);
		if (matcher.find()) return new XNBNullableReader(matcher.group(1),reader.type);
		return null;
	}
	
	private final String reader;
	
	protected XNBNullableReader(String type, String reader) {
		super("Microsoft.Xna.Framework.Content.NullableReader`1[["+type+"]]");
		this.reader = reader;
	}

	public Object read(BinBuffer binb) throws XNBException {
		if (binb.readByte() == 0) return null;
		XNBObjectReader<?> xnbor = XNBReader.getObjectReader(reader);
		return xnbor.read(binb);
	}
}