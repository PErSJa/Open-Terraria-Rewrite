package pl.shockah.terraria.xnb;

public final class Native {
	static {
		System.loadLibrary("lzx");
	}
	
	protected native byte[] nativeDecompressLZX(byte[] bytes);
	
	//TODO: get the compiled library from Daneos
}