package pl.shockah.terraria.rooms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.shockah.Util;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Colors;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.anim.Anim;
import pl.shockah.easyslick.anim.AnimState;
import pl.shockah.easyslick.transitions.TransitionBlack;
import pl.shockah.terraria.Terraria;

public class RoomTitle extends Room {
	public static final List<Integer> splashes = Collections.synchronizedList(new ArrayList<Integer>());
	
	protected final Anim anim = new Anim();
	protected int splash;
	
	protected void onCreate() {
		splash = Util.getRandom(splashes);
		
		anim.addState(new AnimState().setColor(Colors.alpha(0f)));
		anim.addState(90,new AnimState());
		anim.addState(180,new AnimState());
		anim.addState(270,new AnimState().setColor(Colors.alpha(0f)));
	}
	protected void onTick(int delta) {
		anim.updateStep();
		if (anim.isLastStep()) Room.set(new RoomMainMenu(),null,new TransitionBlack(false,90){
			public boolean stopsGameplay() {return false;}
		});
	}
	protected void onRender(GraphicsHelper gh) {
		try {
			gh.drawImage(Terraria.managerImage.get(splash),size.x/2f,size.y/2f,anim.getCurrentState());
		} catch (Exception e) {App.getApp().handle(e);}
	}
}