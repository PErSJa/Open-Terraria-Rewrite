package pl.shockah.terraria.xnb;

import org.newdawn.slick.geom.Rectangle;
import pl.shockah.BinBuffer;

public class XNBRectangleReader extends XNBObjectReader<Rectangle> {
	protected XNBRectangleReader() {
		super("Microsoft.Xna.Framework.Content.RectangleReader");
	}

	public Rectangle read(BinBuffer binb) {
		return new Rectangle(binb.readInt(),binb.readInt(),binb.readInt(),binb.readInt());
	}
}