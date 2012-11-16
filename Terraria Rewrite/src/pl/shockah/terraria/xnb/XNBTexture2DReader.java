package pl.shockah.terraria.xnb;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Point;
import pl.shockah.BinBuffer;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Image;

public class XNBTexture2DReader extends XNBObjectReader<Image> {
	private static enum ESurfaceFormat {
		Color(),Bgr565(),Bgra5551(),Bgra4444(),Dxt1(),Dxt3(),Dxt5(),NormalizedByte2(),NormalizedByte4(),Rgba1010102(),
		Rg32(),Rgba64(),Alpha8(),Single(),Vector2(),Vector4(),HalfSingle(),HalfVector2(),HalfVector4(),HdrBlendable();
	}
	
	protected XNBTexture2DReader() {
		super("Microsoft.Xna.Framework.Content.Texture2DReader");
	}

	public Image read(BinBuffer binb) throws XNBException {
		int iSurfaceFormat = binb.readInt();
		ESurfaceFormat surfaceFormat = iSurfaceFormat >= 0 && iSurfaceFormat < ESurfaceFormat.values().length ? ESurfaceFormat.values()[iSurfaceFormat] : null;
		
		int w = (int)binb.readUInt(), h = (int)binb.readUInt(), mips = (int)binb.readUInt();
		if (w <= 0 || h <= 0) throw new XNBException("Invalid texture width/height.");
		
		switch (surfaceFormat) {
			case Color: {
				Image image = null;
				GraphicsHelper gh = null;
				try {
					image = new Image(w,h);
					gh = new GraphicsHelper(image.getGraphics());
				} catch (Exception e) {App.getApp().handle(e);}
				XNBColorReader reader = (XNBColorReader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.ColorReader");
				
				int x = 0, y = 0;
				while (mips-- > 0) {
					binb.readUInt();
					gh.g().setColor(reader.read(binb));
					gh.g().draw(new Point(x,y));
					
					x++;
					if (x == w) {
						x = 0;
						y++;
					}
				}
				return image;
			}
			case Vector4: {
				Image image = null;
				GraphicsHelper gh = null;
				try {
					image = new Image(w,h);
					gh = new GraphicsHelper(image.getGraphics());
				} catch (Exception e) {App.getApp().handle(e);}
				XNBVector4Reader reader = (XNBVector4Reader)XNBReader.getObjectReader("Microsoft.Xna.Framework.Content.Vector4Reader");
				
				int x = 0, y = 0;
				
				while (mips-- > 0) {
					binb.readUInt();
					float[] vector = reader.read(binb);
					gh.g().setColor(new Color(vector[0],vector[1],vector[2],vector[3]));
					gh.g().draw(new Point(x,y));
					
					x++;
					if (x == w) {
						x = 0;
						y++;
					}
				}
				return image;
			}
			default: throw new XNBException("Unsupported surface format.");
		}
	}
}