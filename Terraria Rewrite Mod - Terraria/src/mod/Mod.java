package mod;

import java.util.List;

import mod.game.BiomeForest;
import mod.world.WorldGen;
import pl.shockah.Util;
import pl.shockah.easyslick.App;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.game.Biome;

public class Mod extends pl.shockah.terraria.mods.Mod {
	public static Mod mod;
	
	public static List<Integer>
		imageBack = Util.syncedList(Integer.class),
		imageFar = Util.syncedList(Integer.class),
		imageNear = Util.syncedList(Integer.class);
	public static List<Biome> biomes = Util.syncedList(Biome.class);
	
	public Mod() {
		super("terraria");
	}
	
	public String getTitle() {
		return "Terraria";
	}
	public String getDescription() {
		return "'Vanilla' Terraria content.";
	}
	
	protected void onModLoad() {
		mod = this;
		
		Biome biome;
		
		biome = new BiomeForest();
		biomes.add(biome);
		Biome.biomeList.add(biome);
		
		new WorldGen().register();
		
		try {
			for (int i = 1; i <= 1; i++) imageBack.add(Terraria.managerImage.add(getContentPath()+"images/background/back"+i+".png"));
			for (int i = 1; i <= 2; i++) imageFar.add(Terraria.managerImage.add(getContentPath()+"images/background/far"+i+".png"));
			for (int i = 1; i <= 6; i++) imageNear.add(Terraria.managerImage.add(getContentPath()+"images/background/near"+i+".png"));
		} catch (Exception e) {App.getApp().handle(e);}
	}
	protected void onModUnload() {
		for (Biome biome : biomes) Biome.biomeList.remove(biome);
		
		mod = null;
	}
}