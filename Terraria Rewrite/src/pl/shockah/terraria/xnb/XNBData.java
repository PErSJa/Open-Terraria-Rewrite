package pl.shockah.terraria.xnb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.shockah.Pair;
import pl.shockah.Util;

public final class XNBData {
	protected ETargetPlatform targetPlatform = null;
	protected Integer formatVersion = null, sizeTotal = null, sizeDecompressed = null;
	protected Boolean flagHiDef = null, flagCompressed = null;
	
	protected List<Pair<String,Integer>> readers = Collections.synchronizedList(new ArrayList<Pair<String,Integer>>());
	protected Object assetData = null;
	protected List<Object> sharedResources = Util.syncedList(Object.class);
	
	protected XNBData() {}
}