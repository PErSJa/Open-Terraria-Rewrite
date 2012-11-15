package pl.shockah.terraria.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.Helper;
import pl.shockah.easyslick.Colors;
import pl.shockah.easyslick.EFontAlign;
import pl.shockah.easyslick.Fonts;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Window;

public abstract class GuiMenuElButtonTextOption<T> extends GuiMenuElButtonText {
	public static int getFontAlignLeft(int fontAlign) {
		if (Helper.equalsOR(fontAlign,Fonts.TopLeft,Fonts.TopCenter,Fonts.TopRight)) return Fonts.TopLeft;
		if (Helper.equalsOR(fontAlign,Fonts.MiddleLeft,Fonts.MiddleCenter,Fonts.MiddleRight)) return Fonts.MiddleLeft;
		if (Helper.equalsOR(fontAlign,Fonts.BottomLeft,Fonts.BottomCenter,Fonts.BottomRight)) return Fonts.BottomLeft;
		return -1;
	}
	
	public T value;
	
	public GuiMenuElButtonTextOption(Font font, EFontAlign fontAlign, Color color, String text, T value) {
		super(font,fontAlign,color,text);
		this.value = value;
	}
	
	protected void onTick(int delta) {
		Vector2f npos = pos.copy();
		if (fontAlign.x == 2) npos.x -= getElWidth();
		if (fontAlign.x == 1) npos.x -= getElWidth()/2;
		
		Fonts.setFontAlign(fontAlign);
		Vector2f pos = Fonts.getActualStringXY(font,text,npos.x,npos.y);
		Vector2f size = new Vector2f(getElWidth(),font.getHeight(text));
		Fonts.resetFontAlign();
		
		if (Window.mouseInRegion(pos,pos.copy().add(size))) {
			scale = Math.min(scale+.025f,1f);
			if (Window.mbLeft.pressed()) onButtonPressed();
		} else scale = Math.max(scale-.025f,.75f);
	}
	protected void onRender(GraphicsHelper gh) {
		Vector2f npos = pos.copy();
		if (fontAlign.x == 2) npos.x -= getElWidth();
		if (fontAlign.x == 1) npos.x -= getElWidth()/2;
		
		gh.g().setFont(font);
		gh.g().setColor(Colors.alpha(Color.white,scale));
		
		Fonts.setFontAlign(EFontAlign.pos[0][fontAlign.y]);
		Fonts.drawStringShadowed(gh,text,npos.x,pos.y,Colors.alpha(Color.black,scale));
		
		Fonts.setFontAlign(EFontAlign.pos[2][fontAlign.y]);
		npos.x += getElWidth();
		Fonts.drawStringShadowed(gh,getValueText(),npos.x,pos.y,Colors.alpha(Color.black,scale));
		
		Fonts.resetFontAlign();
	}
	protected int getElWidth() {
		return 480;
	}
	
	protected abstract String getValueText();
	public T getValue() {
		return value;
	}
}