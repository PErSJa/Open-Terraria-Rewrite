package pl.shockah.terraria.xnb;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.geom.Rectangle;
import pl.shockah.BinBuffer;
import pl.shockah.easyslick.Image;

public class XNBSpriteFontReader extends XNBObjectReader<XNBSpriteFont> {
	protected XNBSpriteFontReader() {
		super("Microsoft.Xna.Framework.Content.SpriteFontReader");
	}

	public XNBSpriteFont read(BinBuffer binb) throws XNBException {
		Image image = ((XNBTexture2DReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.Texture2DReader")).read(binb);
		
		List<Rectangle> list1 = new ArrayList<Rectangle>();
		for (Object o : ((XNBListReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.ListReader`1[[Rectangle]]")).read(binb)) list1.add((Rectangle)o);
		
		List<Rectangle> list2 = new ArrayList<Rectangle>();
		for (Object o : ((XNBListReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.ListReader`1[[Rectangle]]")).read(binb)) list2.add((Rectangle)o);
		
		List<Character> list3 = new ArrayList<Character>();
		for (Object o : ((XNBListReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.ListReader`1[[Char]]")).read(binb)) list3.add((Character)o);
		
		int spacingV = binb.readInt();
		float spacingH = binb.readFloat();
		
		List<float[]> list4 = new ArrayList<float[]>();
		for (Object o : ((XNBListReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.ListReader`1[[Vector3]]")).read(binb)) list4.add((float[])o);
		
		/*Character defaultChar = (Character)*/((XNBNullableReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.NullableReader`1[[Char]]")).read(binb);
		
		return new XNBSpriteFont(image,list1,list2,list3,spacingV,spacingH,list4/*,defaultChar*/);
	}
}