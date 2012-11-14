package pl.shockah.terraria.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.geom.Vector2f;

import pl.shockah.easyslick.Colors;
import pl.shockah.easyslick.Fonts;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Window;

public abstract class GuiMenuElButtonText extends GuiMenuEl {
	public final Font font;
	public final int fontAlign;
	public final Color color;
	public String text;
	
	protected float scale = .75f;
	
	public GuiMenuElButtonText(Font font, int fontAlign, Color color, String text) {
		super();
		checkCollision = false;
		
		this.font = font;
		this.fontAlign = fontAlign;
		this.color = color;
		this.text = text;
	}
	
	protected void onCreate() {
		scale = .75f;
	}
	protected void onTick(int delta) {
		Fonts.setFontAlign(fontAlign);
		Vector2f pos = Fonts.getActualStringXY(font,text,this.pos.x,this.pos.y);
		Vector2f size = new Vector2f(font.getWidth(text),font.getHeight(text));
		Fonts.resetFontAlign();
		
		if (Window.mouseInRegion(pos,pos.copy().add(size))) {
			scale = Math.min(scale+.025f,1f);
			if (Window.mbLeft.pressed()) onButtonPressed();
		} else scale = Math.max(scale-.025f,.75f);
	}
	protected void onRender(GraphicsHelper gh) {
		gh.g().setFont(font);
		Fonts.setFontAlign(fontAlign);
		gh.g().setColor(Colors.alpha(Color.white,scale));
		
		Fonts.drawStringShadowed(gh,text,pos.x,pos.y,Colors.alpha(Color.black,scale));
		
		Fonts.resetFontAlign();
	}
	protected int getElHeight() {
		return font.getHeight("y");
	}
	
	protected abstract void onButtonPressed();
}