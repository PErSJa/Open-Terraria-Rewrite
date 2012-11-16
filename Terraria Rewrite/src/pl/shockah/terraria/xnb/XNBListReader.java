package pl.shockah.terraria.xnb;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pl.shockah.BinBuffer;
import pl.shockah.Util;

public class XNBListReader extends XNBObjectReader<List<Object>> {
	protected static XNBListReader createReader(XNBObjectReader<?> reader) {
		String[] split = reader.getClass().getName().split("\\.");
		Matcher matcher = Pattern.compile("XNB(.+)Reader").matcher(split[split.length-1]);
		if (matcher.find()) return new XNBListReader(matcher.group(1),reader.type);
		return null;
	}
	
	private final String reader;
	
	protected XNBListReader(String type, String reader) {
		super("Microsoft.Xna.Framework.Content.ListReader`1[["+type+"]]");
		this.reader = reader;
	}

	public List<Object> read(BinBuffer binb) throws XNBException {
		List<Object> list = Util.syncedList(Object.class);
		XNBObjectReader<?> xnbor = XNBReader.getObjectReader(reader);
		int count = (int)binb.readUInt();
		while (count-- > 0) list.add(xnbor.read(binb));
		return list;
	}
}