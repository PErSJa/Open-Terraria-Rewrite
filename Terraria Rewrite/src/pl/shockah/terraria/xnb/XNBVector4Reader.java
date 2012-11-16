package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBVector4Reader extends XNBObjectReader<float[]> {
	protected XNBVector4Reader() {
		super("Microsoft.Xna.Framework.Content.Vector4Reader");
	}

	public float[] read(BinBuffer binb) {
		return new float[]{binb.readFloat(),binb.readFloat(),binb.readFloat(),binb.readFloat()};
	}
}