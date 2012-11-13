package pl.shockah.terraria.gui;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.easyslick.Entity;

public class GuiMenu extends Entity {
	public List<GuiMenuEl> list = new ArrayList<GuiMenuEl>();
	
	protected void onCreate() {
		Vector2f v = pos.copy();
		for (GuiMenuEl el : list) {
			el.create(v.copy());
			v.y += el.getElHeight()+8;
		}
	}
	protected void onDestroy() {
		for (GuiMenuEl el : list) el.destroy();
	}
}