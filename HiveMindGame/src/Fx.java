import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Fx extends GameObject{
	HiveMind parent;
	int duration; 
	int durationCounter;
	String type;
	int number;
	float direction; 
	float speed;
	PVector[] relP;
	
	Fx (float _x, float _y, String _type, HiveMind p){
		parent = p;
		pos = new PVector (_x, _y,-1);
		type = _type;
		
		switch(type) {
		case "BLOOD": 
			duration = 1000;
			number = (int) parent.random(20,50);
			relP = new PVector[number];
			
			speed = parent.random((float) 0.1,2);
			direction = (float) parent.random(PConstants.PI*2);
			
			for(int i = 0; i< number; i++) {
				relP[i] = new PVector (pos.x, pos.y,pos.z);
			}
			break;
		case "EXPLOSION": 
			duration = 50;
			break;
		}
	}
	
	void update() {
		super.update();
		
		durationCounter++;
		
		switch(type) {
		case "BLOOD": 
			if(speed > 0) {
			for(int i = 0; i<number; i++) {
				relP[i].x = relP[i].x + speed*PApplet.cos(direction);
				relP[i].y = relP[i].y + speed*PApplet.sin(direction);
			}
			speed = (float) (speed - 0.04);
			}

			break;
		case "EXPLOSION": 
			
			break;
		}
		
		if(durationCounter>=duration) {
			dead = true;
		}
	}
	
	void render() {
		super.render();
		
		switch(type) {
		case "BLOOD": 
			parent.rectMode(PConstants.CENTER);
			parent.fill(100,0,100,PApplet.map(durationCounter, 1000,0,0,255));
			for(int i = 0; i<number; i++) {
			   parent.rect(relP[i].x, relP[i].y, 5, 5);
			}
			break;
		case "EXPLOSION": 
			
			break;
		}
		
		
	}
}
