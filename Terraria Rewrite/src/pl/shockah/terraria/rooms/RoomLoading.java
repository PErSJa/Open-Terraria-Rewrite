package pl.shockah.terraria.rooms;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import org.newdawn.slick.Color;
import org.newdawn.slick.imageout.ImageOut;
import pl.shockah.WinRegistry;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.Image;
import pl.shockah.easyslick.gui.GuiMessage;
import pl.shockah.easyslick.gui.GuiYesNo;
import pl.shockah.terraria.Terraria;
import pl.shockah.terraria.mods.ModManager;
import pl.shockah.terraria.resources.Progress;
import pl.shockah.terraria.resources.ResourceManager;
import pl.shockah.terraria.xnb.XNBData;
import pl.shockah.terraria.xnb.XNBReader;

public class RoomLoading extends Room {
	private static final Map<String,String> contentMapping = new HashMap<String,String>();
	
	static {
		//region mappings
		Map<String,String> m = contentMapping; String s1, s2;
		
		s1 = "Images/";
		s2 = "images/";
		m.put(s1+"Logo.xnb",s2+"logo/1.png");
		for (int i = 2; i <= 3; i++) m.put(s1+"Logo"+i+".xnb",s2+"logo/"+i+".png");
		for (int i = 1; i <= 6; i++) m.put(s1+"logo_"+i+".xnb",s2+"splash/"+i+".png");
		//endregion mapping
	}
	
	private boolean copied = false;
	
	protected void onCreate() {
		new Entity().create();
		for (ResourceManager<?> manager : Terraria.managers) manager.autoLoad();
	}
	protected void onTick(int delta) {
		if (!copied) return;
		
		for (ResourceManager<?> manager : Terraria.managers) if (!manager.updateLoading()) return;
		Terraria.managerMod.onContentLoaded();
		Room.set(new RoomTitle());
	}
	protected void onRender(GraphicsHelper gh) {
		if (!copied) return;
		
		Progress progress = null;
		for (ResourceManager<?> manager : Terraria.managers) if (progress == null || progress.percent >= 1d) progress = manager.getProgress();
		
		gh.g().setColor(Color.white);
		gh.g().drawString("Loading: "+progress.text,10,10);
	}
	
	private void copyOriginalResources(File dir) {
		new GuiMessage("Copying from "+dir.getAbsolutePath()).create();
		
		List<File> dirs = new LinkedList<File>();
		dirs.add(dir);
		
		while (!dirs.isEmpty()) {
			File f = dirs.remove(0);
			if (f.isDirectory()) {
				if (f.getName().matches("\\.{1,2}")) continue;
				for (File f2 : f.listFiles()) dirs.add(f2);
			} else if (f.getName().endsWith(".xnb")) {
				String mapping = getMapping(dir,f);
				if (mapping != null) {
					try {
						XNBReader xnbr = new XNBReader(f);
						XNBData xnbd = xnbr.read(false);
						
						Object o = xnbd.getAssetData();
						if (o == null) continue;
						if (o instanceof Image) {
							FileOutputStream fos = new FileOutputStream(new File(ModManager.getPath("modd://__vanilla/"+mapping)));
							ImageOut.write((Image)o,ImageOut.PNG,fos);
							fos.close();
						}
					} catch (Exception e) {App.getApp().handle(e);}
				}
			}
		}
		
		copied = true;
	}
	private String getMapping(File base, File file) {
		return contentMapping.get(getRelativePath(base,file));
	}
	private String getRelativePath(File base, File file) {
		String key = "";
		while (true) {
			if (file.equals(base)) return "";
			if (file.getParentFile() == null) return null;
			if (file.getParentFile().equals(base)) {
				String ret = file.getName()+"/"+key;
				return ret.substring(0,ret.length()-1);
			}
			
			key = file.getName()+"/"+key;
			file = file.getParentFile();			
		}
	}
	
	private class Entity extends pl.shockah.easyslick.Entity {
		public void onCreate() {
			File f = new File(ModManager.getPath("modd://__vanilla/images/logo/1.png"));
			if (!f.exists()) {
				GuiYesNo gui = new GuiYesNo("Open Terraria Rewrite needs to access resources from the original Terraria in order to continue. Do you want to do it now?");
				gui.create();
				
				if (gui.getValue()) {
					try {
						String pathSteam = System.getProperty("os.name").startsWith("Windows") ? WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER,"Software\\Valve\\Steam","SteamPath") : null;
						if (pathSteam == null || !new File(pathSteam).exists()) {
							pathSteam = System.getProperty("os.name").startsWith("Mac OS X") ? "/Library/Application Support/Steam" : null;
							if (pathSteam == null || !new File(pathSteam).exists()) {
								new GuiMessage("Couldn't find Steam install directory. Please specify the directory in which Terraria is installed (Steam\\steamapps\\common\\Terraria).").create();
								
								JFileChooser chooser = new JFileChooser();
								chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								chooser.setAcceptAllFileFilterUsed(false);
								if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
									File dir = new File(chooser.getSelectedFile(),"Content");
									if (dir.exists() && dir.isDirectory() && new File(dir.getParentFile(),"Terraria.exe").exists()) {
										copyOriginalResources(dir);
										return;
									}
								}
							} else {
								File dir = new File(new File(new File(new File(pathSteam,"steamapps"),"common"),"Terraria"),"Content");
								if (dir.exists() && dir.isDirectory() && new File(dir.getParentFile(),"Terraria.exe").exists()) {
									copyOriginalResources(dir);
									return;
								}
							}
						} else {
							File dir = new File(new File(new File(new File(pathSteam,"steamapps"),"common"),"Terraria"),"Content");
							if (dir.exists() && dir.isDirectory() && new File(dir.getParentFile(),"Terraria.exe").exists()) {
								copyOriginalResources(dir);
								return;
							}
						}
					} catch (Exception e) {App.getApp().handle(e);}
				}
			}
			
			new GuiMessage("Couldn't access Terraria resources. Exiting.").create();
			App.stop();
		}
	}
}