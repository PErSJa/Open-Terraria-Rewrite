package pl.shockah.terraria.xnb;

import org.newdawn.slick.Color;
import pl.shockah.BinBuffer;

public class XNBColorReader extends XNBObjectReader<Color> {
	protected XNBColorReader() {
		super("Microsoft.Xna.Framework.Content.ColorReader");
	}

	public Color read(BinBuffer binb) {
		return new Color(binb.readByte(),binb.readByte(),binb.readByte(),binb.readByte());
	}
}