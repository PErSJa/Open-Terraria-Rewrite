package pl.shockah.terraria.xnb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.imageout.ImageOut;
import pl.shockah.BinBuffer;
import pl.shockah.BinBufferInputStream;
import pl.shockah.BinBufferOutputStream;
import pl.shockah.BinFile;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Image;

public class XNBSpriteFont implements Font {
	public static XNBSpriteFont read(File file) {
		try {
			Image xImage;
			List<Rectangle> xGlyphs = new ArrayList<Rectangle>(), xCropping = new ArrayList<Rectangle>();
			List<Character> xChars = new ArrayList<Character>();
			int xSpacingV;
			float xSpacingH;
			List<float[]> xKerning = new ArrayList<float[]>();
			
			BinBuffer binb = new BinFile(file).read();
			
			BinBuffer imageData = new BinBuffer();
			imageData.writeBytes(binb.readBytes((int)binb.readUInt()));
			
			imageData.setPos(0);
			xImage = new Image(new BinBufferInputStream(imageData),file.getName(),false);
			
			int count = (int)binb.readUInt();
			while (count-- > 0) {
				xGlyphs.add(new Rectangle(binb.readInt(),binb.readInt(),binb.readInt(),binb.readInt()));
				xCropping.add(new Rectangle(binb.readInt(),binb.readInt(),binb.readInt(),binb.readInt()));
				xChars.add(binb.readJavaString().charAt(0));
			}
			
			xSpacingV = binb.readInt();
			xSpacingH = binb.readFloat();
			
			count = (int)binb.readUInt();
			while (count-- > 0) xKerning.add(new float[]{binb.readFloat(),binb.readFloat(),binb.readFloat()});
			
			return new XNBSpriteFont(xImage,xGlyphs,xCropping,xChars,xSpacingV,xSpacingH,xKerning);
		} catch (Exception e) {App.getApp().handle(e);}
		return null;
	}
	
	protected final Image image;
	protected final Rectangle[] glyphs, cropping;
	protected final char[] chars;
	protected final int spacingV;
	protected final float spacingH;
	protected final float[][] kerning;
	
	protected final int maxCharY;
	
	public XNBSpriteFont(Image image, List<Rectangle> glyphs, List<Rectangle> cropping, List<Character> chars, int spacingV, float spacingH, List<float[]> kerning) {
		this.image = image;
		this.glyphs = glyphs.toArray(new Rectangle[glyphs.size()]);
		this.cropping = cropping.toArray(new Rectangle[cropping.size()]);
		
		this.chars = new char[chars.size()];
		for (int i = 0; i < chars.size(); i++) this.chars[i] = chars.get(i);
		
		this.spacingV = spacingV;
		this.spacingH = spacingH;
		
		this.kerning = kerning.toArray(new float[kerning.size()][3]);
		
		Integer max = null;
		for (Rectangle rect : glyphs) if (max == null || rect.getHeight() > max) max = (int)rect.getHeight();
		maxCharY = max;
	}
	
	protected void drawChar(GraphicsHelper gh, float x, float y, Rectangle glyph, Rectangle cropping, Color color) {
		gh.g().drawImage(image,x,y,x+glyph.getWidth(),y+glyph.getHeight(),glyph.getX(),glyph.getY(),glyph.getX()+glyph.getWidth(),glyph.getY()+glyph.getHeight(),color);
	}
	
	public void drawString(float x, float y, CharSequence cs) {
		drawString(x,y,cs,Color.white);
	}
	public void drawString(float x, float y, CharSequence cs, Color color) {
		GraphicsHelper gh = new GraphicsHelper(GraphicsHelper.getGraphics());
		
		float xx = 0, yy = 0;
		for (int i = 0; i < cs.length(); i++) {
			char c = cs.charAt(i), c2 = i != 0 ? cs.charAt(i-1) : 0;
			if (c2 != 0) for (int j = 0; i < kerning.length; j++) {
				if (kerning[j][1] == c && kerning[j][0] == c2) {
					xx += kerning[j][2];
					break;
				}
			}
			for (int j = 0; j < chars.length; j++) {
				if (chars[j] != c) continue;
				drawChar(gh,x+xx,y+yy,glyphs[j],cropping[j],color);
				xx += glyphs[j].getWidth()+spacingH;
			}
			if (c == '\n') {
				xx = 0;
				yy += getLineHeight();
			}
		}
	}
	public void drawString(float x, float y, CharSequence cs, Color color, int startIndex, int endIndex) {
		drawString(x,y,cs.subSequence(startIndex,endIndex),color);
	}
	
	public int getWidth(CharSequence cs) {
		List<Float> list = new ArrayList<Float>();
		int y = 0;
		for (int i = 0; i < cs.length(); i++) {
			if (list.size() <= y) list.add(0f);
			char c = cs.charAt(i), c2 = i != 0 ? cs.charAt(i-1) : 0;
			if (c2 != 0) for (int j = 0; i < kerning.length; j++) {
				if (kerning[j][1] == c && kerning[j][0] == c2) {
					list.set(y,list.get(y)+kerning[j][2]);
					break;
				}
			}
			for (int j = 0; j < chars.length; j++) {
				if (chars[j] != c) continue;
				list.set(y,list.get(y)+glyphs[j].getWidth());
				if (i != cs.length()-1) list.set(y,list.get(y)+spacingH);
			}
			if (c == '\n') y++;
		}
		Float maxX = null;
		for (int j = 0; j < list.size(); j++) if (maxX == null || list.get(j) > maxX) maxX = list.get(j);
		return (int)(float)maxX;
	}
	public int getHeight(CharSequence cs) {
		return cs.toString().split("\\\\n").length*getLineHeight();
	}
	public int getLineHeight() {
		return maxCharY+spacingV;
	}
	
	public void write(File file) {
		try {
			BinBuffer binb = new BinBuffer();
			
			BinBuffer imageData = new BinBuffer();
			ImageOut.write(image,ImageOut.PNG,new BinBufferOutputStream(imageData));
			imageData.setPos(0);
			binb.writeUInt(imageData.getSize());
			binb.writeBinBuffer(imageData);
			
			binb.writeUInt(glyphs.length);
			for (int i = 0; i < glyphs.length; i++) {
				binb.writeInt((int)glyphs[i].getX());
				binb.writeInt((int)glyphs[i].getY());
				binb.writeInt((int)glyphs[i].getWidth());
				binb.writeInt((int)glyphs[i].getHeight());
				
				binb.writeInt((int)cropping[i].getX());
				binb.writeInt((int)cropping[i].getY());
				binb.writeInt((int)cropping[i].getWidth());
				binb.writeInt((int)cropping[i].getHeight());
				
				binb.writeJavaString(""+chars[i]);
			}
			
			binb.writeInt(spacingV);
			binb.writeFloat(spacingH);
			
			binb.writeUInt(kerning.length);
			for (int i = 0; i < kerning.length; i++) for (int j = 0; j < kerning[0].length; j++) binb.writeFloat(kerning[i][j]);
			
			binb.setPos(0);
			new BinFile(file).write(binb);
		} catch (Exception e) {App.getApp().handle(e);}
	}
}