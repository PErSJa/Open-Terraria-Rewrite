package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBVector2Reader extends XNBObjectReader<float[]> {
	protected XNBVector2Reader() {
		super("Microsoft.Xna.Framework.Content.Vector2Reader");
	}

	public float[] read(BinBuffer binb) {
		return new float[]{binb.readFloat(),binb.readFloat()};
	}
}