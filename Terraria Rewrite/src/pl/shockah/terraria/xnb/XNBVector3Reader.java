package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBVector3Reader extends XNBObjectReader<float[]> {
	protected XNBVector3Reader() {
		super("Microsoft.Xna.Framework.Content.Vector3Reader");
	}

	public float[] read(BinBuffer binb) {
		return new float[]{binb.readFloat(),binb.readFloat(),binb.readFloat()};
	}
}