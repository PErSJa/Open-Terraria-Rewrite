package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBSpriteFontReader extends XNBObjectReader<float[]> {
	protected XNBSpriteFontReader() {
		super("Microsoft.Xna.Framework.Content.SpriteFontReader");
	}

	public float[] read(BinBuffer binb) {
		return new float[]{binb.readFloat(),binb.readFloat(),binb.readFloat(),binb.readFloat()};
	}
}