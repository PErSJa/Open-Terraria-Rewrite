package pl.shockah.terraria.rooms;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Entity;
import pl.shockah.easyslick.Fonts;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.View;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.gui.GuiMenu;
import pl.shockah.terraria.gui.GuiMenuElButtonText;
import pl.shockah.terraria.sentities.SEntityBackgrounds;
import pl.shockah.terraria.sentities.SEntityLogo;
import pl.shockah.terraria.world.WorldBlank;

public class RoomMainMenu extends Room {
	protected void onCreate() {
		try {
			Terraria.worlds.add(new WorldBlank());
			new SEntityBackgrounds().create();
			
			View view = View.getDefault();
			new SEntityLogo().create(view.pos.x+view.size.x/2f,view.pos.y+96);
			
			GuiMenu guiMenu = new GuiMenu();
			guiMenu.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),Fonts.MiddleCenter,Color.white,"Single-player"){
				protected void onButtonPressed() {}
			});
			guiMenu.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),Fonts.MiddleCenter,Color.white,"Multi-player"){
				protected void onButtonPressed() {}
			});
			guiMenu.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),Fonts.MiddleCenter,Color.white,"Settings"){
				protected void onButtonPressed() {}
			});
			guiMenu.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),Fonts.MiddleCenter,Color.white,"Exit"){
				protected void onButtonPressed() {
					App.stop();
				}
			});
			guiMenu.create(new Vector2f(view.pos.x+view.size.x/2f,view.pos.y+192));
			
			new Entity(){
				protected void onRender(GraphicsHelper gh) {
					try {
						gh.g().setFont(Terraria.managerFont.get(1));
						Fonts.setFontAlign(Fonts.BottomLeft);
						gh.g().setColor(Color.white);
						Fonts.drawStringShadowed(gh,"Open Terraria Rewrite r1",pos.x,pos.y,Color.black);
						Fonts.resetFontAlign();
					} catch (Exception e) {App.getApp().handle(e);}
				}
			}.create(view.pos.x,view.pos.y+view.size.y);
		} catch (Exception e) {App.getApp().handle(e);}
	}
	protected void onEnd() {
		Terraria.worlds.remove(0);
	}
}