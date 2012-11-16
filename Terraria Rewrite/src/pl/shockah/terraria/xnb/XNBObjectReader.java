package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public abstract class XNBObjectReader<T> {
	public final String type;
	
	protected XNBObjectReader(String type) {
		this.type = type;
	}
	
	public abstract T read(BinBuffer binb) throws XNBException;
}