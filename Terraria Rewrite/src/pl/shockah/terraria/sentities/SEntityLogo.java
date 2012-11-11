package pl.shockah.terraria.sentities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pl.shockah.easyslick.App;
import pl.shockah.easyslick.Entity;
import pl.shockah.easyslick.GraphicsHelper;
import pl.shockah.easyslick.anim.AnimMultiple;
import pl.shockah.easyslick.anim.AnimMultipleLine;
import pl.shockah.easyslick.anim.AnimState;
import pl.shockah.terraria.Terraria;

public class SEntityLogo extends Entity {
	public static final List<Integer> logo = Collections.synchronizedList(new ArrayList<Integer>());
	
	protected AnimMultiple anim = new AnimMultiple();
	
	protected void onCreate() {
		checkCollision = false;
		
		AnimMultipleLine
			lineAngle = new AnimMultipleLine().useAngle(true),
			lineScale = new AnimMultipleLine().useScale(true);
		lineAngle.looping = true;
		lineScale.looping = true;
		
		lineAngle.addState(new AnimState().setAngle(10));
		lineAngle.addState(600,new AnimState().setAngle(-10));
		lineAngle.copyFirstState(1200);
		lineAngle.updateStep(300);
		
		lineScale.addState(new AnimState().setScale(.75f,.75f));
		lineScale.addState(900,new AnimState().setScale(1f,1f));
		lineScale.copyFirstState(1800);
		
		anim.addLines(lineAngle,lineScale);
	}
	
	protected void onTick(int delta) {
		anim.updateStep();
	}
	protected void onRender(GraphicsHelper gh) {
		try {
			gh.drawImage(Terraria.managerImage.get(logo.get(0)),pos.x,pos.y,anim);
		} catch (Exception e) {App.getApp().handle(e);}
	}
}
