package pl.shockah.terraria.xnb;

public enum ETargetPlatform {
	Windows('w'), WindowsPhone7('m'), Xbox360('x');
	
	public final char targetPlatform;
	
	private ETargetPlatform(char targetPlatform) {
		this.targetPlatform = targetPlatform;
	}
	
	public static ETargetPlatform getByChar(char targetPlatform) {
		for (ETargetPlatform etp : values()) if (etp.targetPlatform == targetPlatform) return etp;
		return null;
	}
}