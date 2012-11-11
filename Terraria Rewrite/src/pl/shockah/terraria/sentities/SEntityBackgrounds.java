package pl.shockah.terraria.sentities;

import java.util.Random;
import org.newdawn.slick.geom.Vector2f;
import pl.shockah.easyslick.Entity;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.game.Biome;

public class SEntityBackgrounds extends Entity {
	public Biome[] biomes = new Biome[2];
	public float biomeTransition = 0f;
	public Vector2f offset = new Vector2f();
	
	protected void onCreate() {
		checkCollision = false;
		setBiome();
		
		offset = new Vector2f(new Random().nextInt(100000),0);
	}

	protected void onTick(int delta) {
		Terraria.worlds.get(0).updateTime(100);
		offset.x--;
	}
	protected void onRender(GraphicsHelper gh) {
		gh.g().setAntiAlias(true);
		if (biomeTransition < 1f && biomes[0] != null) biomes[0].onRender(gh,offset,1f-biomeTransition);
		if (biomeTransition > 0f && biomes[1] != null) biomes[1].onRender(gh,offset,biomeTransition);
		gh.g().setAntiAlias(false);
	}
	
	public void setBiome() {
		setBiome(Biome.biomeList.isEmpty() ? null : Biome.biomeList.get(0).name);
	}
	public void setBiome(String name) {setBiome(name,name,0f);}
	public void setBiome(String name1, String name2, float transition) {
		if (name1 != null || name2 != null) {
			for (Biome biome : Biome.biomeList) {
				if (name1 != null && biome.name.equalsIgnoreCase(name1)) biomes[0] = biome;
				if (name2 != null && biome.name.equalsIgnoreCase(name2)) biomes[1] = biome;
			}
		}
		biomeTransition = transition;
	}
}