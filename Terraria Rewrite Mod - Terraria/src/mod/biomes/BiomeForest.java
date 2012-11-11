package mod.biomes;

import mod.Mod;
import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Colors;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Image;
import pl.shockah.easyslick.View;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.game.Biome;
import pl.shockah.terraria.rooms.Room;

public class BiomeForest extends Biome {
	public BiomeForest() {
		super("Forest");
	}
	
	public void onRender(GraphicsHelper gh, Vector2f offset, float alpha) {
		try {
			View view = View.getDefault();
			Image image;
			
			image = Terraria.managerImage.get(Mod.imageBack.get(0));
			gh.g().drawImage(image,view.pos.x,view.pos.y,view.pos.x+view.size.x,view.pos.y+view.size.y,0,0,image.getWidth(),image.getHeight());
			
			drawTiledX(gh,Terraria.managerImage.get(Mod.imageFar.get(0)),new Vector2f(offset.x/15f,Room.get().size.y-236-320-offset.y/30f),Colors.alpha(alpha));
			drawTiledX(gh,Terraria.managerImage.get(Mod.imageFar.get(1)),new Vector2f(offset.x/10f,Room.get().size.y-236-256-offset.y/10f),Colors.alpha(alpha));
			
			drawTiledX(gh,Terraria.managerImage.get(Mod.imageNear.get(0)),new Vector2f(offset.x/3f,Room.get().size.y-236-128-offset.y/3f),Colors.alpha(alpha));
			drawTiledX(gh,Terraria.managerImage.get(Mod.imageNear.get(1)),new Vector2f(offset.x/2f,Room.get().size.y-236-64-offset.y/2f),Colors.alpha(alpha));
			drawTiledX(gh,Terraria.managerImage.get(Mod.imageNear.get(2)),new Vector2f(offset.x,Room.get().size.y-236-offset.y),Colors.alpha(alpha));
		} catch (Exception e) {App.getApp().handle(e);}
	}
	
	protected void drawTiledX(GraphicsHelper gh, Image image, Vector2f pos, Color color) {
		int count = (int)Math.ceil(1f*View.getDefault().size.x/image.getWidth())+1;
		float xx = pos.x%image.getWidth();
		while (xx > 0) xx -= image.getWidth();
		while (count-- > 0) {
			gh.g().drawImage(image,(int)Math.floor(xx),(int)Math.floor(pos.y),color);
			xx += image.getWidth();
		}
	}
}