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
	
	public ETargetPlatform getTargetPlatform() {return targetPlatform;}
	public Integer getFormatVersion() {return formatVersion;}
	public Integer getSizeTotal() {return sizeTotal;}
	public Integer getSizeDecompressed() {return sizeDecompressed;}
	public Boolean getFlagHiDef() {return flagHiDef;}
	public Boolean getFlagCompressed() {return flagCompressed;}
	public List<Pair<String,Integer>> getReaders() {
		List<Pair<String,Integer>> ret = new ArrayList<Pair<String,Integer>>();
		for (Pair<String,Integer> pair : readers) ret.add(new Pair<String,Integer>(pair.get1(),pair.get2()));
		return ret;
	}
	public Object getAssetData() {return assetData;}
	public List<Object> getSharedResources() {
		return new ArrayList<Object>(sharedResources);
	}
}