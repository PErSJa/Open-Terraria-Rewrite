package pl.shockah.terraria.rooms;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.EFontAlign;
import pl.shockah.easyslick.Entity;
import pl.shockah.easyslick.Fonts;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.View;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.gui.GuiMenu;
import pl.shockah.terraria.gui.GuiMenuElButtonText;
import pl.shockah.terraria.gui.GuiMenuElButtonTextOption;
import pl.shockah.terraria.mods.Mod;
import pl.shockah.terraria.sentities.SEntityBackgrounds;
import pl.shockah.terraria.sentities.SEntityLogo;
import pl.shockah.terraria.world.WorldBlank;
import pl.shockah.terraria.world.WorldGen;

public class RoomMainMenu extends Room {
	protected GuiMenu main, settings, mods;
	
	protected void onCreate() {
		try {
			Terraria.worlds.add(new WorldBlank(WorldGen.worldGenList.get(0)));
			new SEntityBackgrounds().create();
			
			View view = View.getDefault();
			new SEntityLogo().create(view.pos.x+view.size.x/2f,view.pos.y+80);
			
			main = new GuiMenu();
			main.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Single-player"){
				protected void onButtonPressed() {}
			});
			main.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Multi-player"){
				protected void onButtonPressed() {}
			});
			main.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Settings"){
				protected void onButtonPressed() {
					main.destroy();
					settings.create();
				}
			});
			main.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Mods"){
				protected void onButtonPressed() {
					main.destroy();
					mods.create();
				}
			});
			main.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Exit"){
				protected void onButtonPressed() {
					App.stop();
				}
			});
			
			settings = new GuiMenu();
			settings.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Back"){
				protected void onButtonPressed() {
					settings.destroy();
					main.create();
				}
			});
			
			mods = new GuiMenu();
			for (Mod mod : Terraria.managerMod.getMods()) {
				mods.list.add(new GuiMenuElButtonTextOption<Boolean>(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,mod.getTitle(),Mod.isModLoaded(mod)){
					protected void onButtonPressed() {
						value = !value;
					}
					protected String getValueText() {
						return value ? "On" : "Off";
					}
				});
			}
			mods.list.add(new GuiMenuElButtonText(Terraria.managerFont.get(0),EFontAlign.MiddleCenter,Color.white,"Back"){
				protected void onButtonPressed() {
					mods.destroy();
					main.create();
				}
			});
			
			Vector2f pos = new Vector2f(view.pos.x+view.size.x/2f,view.pos.y+192);
			main.pos = pos.copy();
			settings.pos = pos.copy();
			mods.pos = pos.copy();
			
			main.create();
			
			new Entity(){
				protected void onRender(GraphicsHelper gh) {
					try {
						gh.g().setFont(Terraria.managerFont.get(1));
						Fonts.setFontAlign(Fonts.BottomLeft);
						gh.g().setColor(Color.white);
						Fonts.drawStringShadowed(gh,"Open Terraria Rewrite "+Terraria.version,pos.x,pos.y,Color.black);
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