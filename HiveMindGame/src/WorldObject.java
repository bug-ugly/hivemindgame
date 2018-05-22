import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;
import processing.core.PApplet;
import processing.core.PVector;

public class WorldObject extends GameObject{
	
	int soundInterval;
	int intervalCounter = 0;
	int soundCounter = 0;
	int soundTimer = 1;
	int growthTimer;
	int fullGrownTime = 500;

	WorldObject(HiveMind p, float f, float g, boolean _good){
		parent = p; 
		pos = new PVector(f, g, 0);
		good = _good;
		out = parent.minim.getLineOut();
		diameter = 20;
		float freq; 
		if ( good) {
			freq = 150;
		}
		else {
			freq = 400;
		}
	    // create a sine wave Oscil, set to 440 Hz, at 0.5 amplitude
	    wave = new Oscil( freq, 0.5f, Waves.SINE );
	    soundInterval = 5;
	    
	    collidable = true;
	    growthTimer = 0;
	    
	}
	
	void update() {
		super.update();
		
		if(pos.z == 1) {
			producingSound();
		}
		
		if(growthTimer>=fullGrownTime) {
		pos.z = 1;
		
		}else {
			growthTimer ++; 
		}
	}
	
	void producingSound() {
		 intervalCounter ++; 
		    if ( intervalCounter > soundInterval){
		     triggerNoise = true;
		     intervalCounter = 0; 
		    }
		    if (triggerNoise) {
				produceSound();
				triggerNoise = false;
				soundPlaying = true;
				soundCounter = 0;
			}
			soundCounter++;
			if (soundCounter > soundTimer && soundPlaying) {
				stopSound();
				soundPlaying = false;
			}
	}
	
	void render() {
		super.render();
		
		if(pos.z == 0) {
			parent.stroke(1);
			parent.noFill();
	}else {
		parent.noStroke();
		if ( good) {
			parent.fill (0,0,255);
		}
		else {
			parent.fill(255,0,0);
		}
		
	}

		parent.ellipse(pos.x,pos.y, PApplet.map(growthTimer, 0,fullGrownTime, 1,diameter),PApplet.map(growthTimer, 0,fullGrownTime, 1,diameter));
		

		if (soundPlaying) {
			parent.stroke(1);
			parent.noFill();
			parent.ellipse(pos.x,pos.y,diameter+5,diameter+5);
		}
	}
	

	void produceSound() {

		wave.unpatch(out);
		// patch the Oscil to the output
		wave.patch(out);
	}

	void stopSound() {
		wave.unpatch(out);
	}

}
