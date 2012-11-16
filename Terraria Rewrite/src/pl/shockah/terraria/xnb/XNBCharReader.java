package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBCharReader extends XNBObjectReader<Character> {
	protected XNBCharReader() {
		super("Microsoft.Xna.Framework.Content.CharReader");
	}

	public Character read(BinBuffer binb) {
		return XNBReader.readCSharpChar(binb);
	}
}