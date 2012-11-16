package pl.shockah.terraria.xnb;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Rectangle;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Image;

public class XNBFont implements Font {
	protected final Image image;
	protected final Rectangle[] glyphs, cropping;
	protected final char[] chars;
	protected final int spacingV;
	protected final float spacingH;
	protected final float[][] kerning;
	
	public XNBFont(Image image, List<Rectangle> glyphs, List<Rectangle> cropping, List<Character> chars, int spacingV, float spacingH, List<float[]> kerning) {
		this.image = image;
		this.glyphs = glyphs.toArray(new Rectangle[glyphs.size()]);
		this.cropping = cropping.toArray(new Rectangle[cropping.size()]);
		
		this.chars = new char[chars.size()];
		for (int i = 0; i < chars.size(); i++) this.chars[i] = chars.get(i);
		
		this.spacingV = spacingV;
		this.spacingH = spacingH;
		
		this.kerning = kerning.toArray(new float[kerning.size()][3]);
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
			char c = cs.charAt(i);
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
			char c = cs.charAt(i);
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
		return 0; //TODO getHeight(CharSequence)
	}
	public int getLineHeight() {
		return 0; //TODO getLineHeight()
	}
}