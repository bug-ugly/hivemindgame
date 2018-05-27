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
	int cor;
	PVector[] relP;
	GameObject currentObject;
	
	Fx (float _x, float _y, String _type, HiveMind p, GameObject cObj){
		currentObject = cObj;
		parent = p;
		pos = new PVector (_x, _y,-1);
		type = _type;
		duration = 50;
		
		switch(type) {
		case "BLOOD": 
			duration = 1000;
			number = (int) parent.random(100,200);
			relP = new PVector[number];
			diameter = parent.random(1,5);
			speed = parent.random((float) 0.1,2);
			direction = (float) parent.random(PConstants.PI*2);
			
			for(int i = 0; i< number; i++) {
				relP[i] = new PVector (pos.x, pos.y,pos.z);
			}
			break;
		case "EXPLOSION": 
			duration = 50;
			break;
		case "SOUND": 
			duration = 50;
			diameter = currentObject.diameter;
			if(currentObject instanceof WorldObject) {
				
				cor = (int) PApplet.map(((WorldObject) currentObject).freq,0,700,0,300);
				
				
			}
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
		case "SOUND":
			
			diameter++;
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
			parent.noStroke();
			parent.rectMode(PConstants.CENTER);
			parent.fill(200,0,100,PApplet.map(durationCounter, 1000,0,0,255));
			for(int i = 0; i<number; i++) {
			   parent.rect(relP[i].x, relP[i].y, diameter, diameter);
			}
			break;
		case "EXPLOSION": 
			
			break;
			
		case "SOUND": 
			parent.noFill();
			parent.colorMode(PConstants.HSB);
			parent.stroke(cor,100,100);
			parent.ellipse(pos.x,pos.y,diameter,diameter);
			parent.colorMode(PConstants.RGB);
			
			break;
		}
		
		
	}
}
