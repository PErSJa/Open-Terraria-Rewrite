package pl.shockah.terraria.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.easyslick.Entity;
import pl.shockah.easyslick.Fonts;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Window;

public abstract class GuiElButtonText extends Entity {
	private static final Vector2f[] shadowOffset = new Vector2f[]{new Vector2f(-1,-1),new Vector2f(1,-1),new Vector2f(-1,1),new Vector2f(1,1)};
	
	public final Font font;
	public final int fontAlign;
	public final Color color;
	public String text;
	
	protected float scale = .75f;
	
	public GuiElButtonText(Font font, int fontAlign, Color color, String text) {
		this.font = font;
		this.fontAlign = fontAlign;
		this.color = color;
		this.text = text;
	}
	
	protected void onTick(int delta) {
		Vector2f pos = new Vector2f(this.pos.x-Fonts.getFontWidthSubtract(font,fontAlign,text),this.pos.y-Fonts.getFontHeightSubtract(font,fontAlign,text));
		Vector2f size = new Vector2f(font.getWidth(text),font.getHeight(text));
		
		if (Window.mouseInRegion(pos,pos.copy().add(size))) {
			scale = Math.min(scale+.025f,1f);
			if (Window.mbLeft.pressed()) onButtonPressed();
		} else {
			scale = Math.max(scale-.025f,.75f);
		}
	}
	protected void onRender(GraphicsHelper gh) {
		float xx = pos.x-Fonts.getFontWidthSubtract(font,fontAlign,text)+font.getWidth(text), yy = pos.y-Fonts.getFontHeightSubtract(font,fontAlign,text)-font.getHeight(text);
		
		gh.g().setFont(font);
		Fonts.setFontAlign(Fonts.MiddleCenter);
		gh.g().setColor(color);
		
		gh.g().setAntiAlias(true);
		gh.g().translate(-xx,-yy);
		gh.g().scale(1f/scale,1f/scale);
		
		drawStringShadowed(gh,text);
		
		gh.g().scale(scale,scale);
		gh.g().translate(xx,yy);
		gh.g().setAntiAlias(false);
		
		Fonts.resetFontAlign();
	}
	
	protected abstract void onButtonPressed();
	
	protected void drawStringShadowed(GraphicsHelper gh, String text) {
		Color c = gh.g().getColor();
		
		gh.g().setColor(Color.black);
		for (Vector2f v : shadowOffset) gh.g().drawString(text,pos.x-v.x,pos.y-v.y);
		
		gh.g().setColor(c);
		gh.g().drawString(text,pos.x,pos.y);
	}
}