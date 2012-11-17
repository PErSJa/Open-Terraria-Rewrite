package pl.shockah.terraria.xnb;

import pl.shockah.BinBuffer;

public class XNBSoundEffectReader extends XNBObjectReader<BinBuffer> {
	protected XNBSoundEffectReader() {
		super("Microsoft.Xna.Framework.Content.SoundEffectReader");
	}

	public BinBuffer read(BinBuffer binb) throws XNBException {
		BinBuffer waveData = new BinBuffer();
		
		long nSamplesPerSec, nAvgBytesPerSec;
		int wFormatTag, nChannels, nBlockAlign, wBitsPerSample, dataChunkSize;
		
		if (binb.readUInt() != 18) throw new XNBException("Wrong format chunk size.");
		if ((wFormatTag = binb.readUShort()) != 1) throw new XNBException("Unimplemented WAV codec (must be PCM).");
		nChannels = binb.readUShort();
		nSamplesPerSec = binb.readUInt();
		nAvgBytesPerSec = binb.readUInt();
		nBlockAlign = binb.readUShort();
		wBitsPerSample = binb.readUShort();
		
		if (nAvgBytesPerSec != nSamplesPerSec*nChannels*(wBitsPerSample/8)) throw new XNBException("Average bytes per second number incorrect.");
		if (nBlockAlign != nChannels*(wBitsPerSample/8)) throw new XNBException("Block align number incorrect.");
		binb.setPos(binb.getPos()+2);
		
		waveData.writeBinBuffer(binb,dataChunkSize = binb.readInt());
		
		binb.clear();
		waveData.setPos(0);
		binb.writeChars("RIFF");
		binb.writeInt(dataChunkSize+36);
		binb.writeChars("WAVE");
		binb.writeChars("fmt ");
		binb.writeInt(16);
		binb.writeUShort(wFormatTag);
		binb.writeUShort(nChannels);
		binb.writeUInt(nSamplesPerSec);
		binb.writeUInt(nAvgBytesPerSec);
		binb.writeUShort(nBlockAlign);
		binb.writeUShort(wBitsPerSample);
		binb.writeChars("data");
		binb.writeInt(dataChunkSize);
		binb.writeBinBuffer(waveData);
		
		waveData.setPos(0);
		return waveData;
	}
}