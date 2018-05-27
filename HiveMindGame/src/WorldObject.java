import ddf.minim.ugens.Oscil;
import ddf.minim.ugens.Waves;
import processing.core.PApplet;
import processing.core.PVector;

//objects that can be picked up
public class WorldObject extends GameObject {

	final float EXPLOSION_RANGE = 50; // range at which other particles get wiped out on explosion
	
	int soundInterval = 20;
	int growthTimer;
	int intervalCounter = 0;
	int soundCounter = 0;
	float freq;
	int OUTER_DIAMETER = 20;
	String type; 
	
	final int SOUND_TIMER = 2; // how long one sound lasts
	final int FULL_GROWN_TIME = 500; // time at which the object becomes fully grown

	WorldObject(HiveMind p, float f, float g, String _type) {
		parent = p;
		pos = new PVector(f, g, 0); //when z becomes 1, its fully grown
		type = _type;
		out = parent.minim.getLineOut();
		diameter = OUTER_DIAMETER;
		
		
		switch(type) {
		case "bomb": 
			freq = 400;
			break;
		case "split": 
			freq = 150;
			break;
		default: 
			freq = 100;
			break;
		}
		
		// create a sine wave Oscil, set to 440 Hz, at 0.5 amplitude
		wave = new Oscil(freq, 0.5f, Waves.SINE);
		collidable = true;
		growthTimer = 0;

	}

	void update() {
		super.update();

		if (pos.z == 1) {
			producingSound();
		}

		if (growthTimer >= FULL_GROWN_TIME) {
			pos.z = 1;

		} else {
			growthTimer++;
		}
	}
	
	public void pickUp(GameObject particle) {
		super.pickUp(particle);
		switch(type) {
		case "bomb": 
			explode();
			break;
		case "split":
			particle.split();
			break;
		}
		
		
		die("CONSUMED");
	}
	
	void explode() {
		for (int i = 0; i < parent.game.gameObjects.size(); i++) {
			GameObject g = parent.game.gameObjects.get(i);
			if (g instanceof Particle && PApplet.dist(g.pos.x, g.pos.y, pos.x, pos.y) < EXPLOSION_RANGE) {
				g.die("EXPLOSION");
			}
		}
	}

	void producingSound() {
		intervalCounter++;
		if (intervalCounter > soundInterval) {
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
		if (soundCounter > SOUND_TIMER && soundPlaying) {
			stopSound();
			soundPlaying = false;
		}
	}

	void render() {
		super.render();

		if (pos.z == 0) {
			parent.stroke(255);
			parent.noFill();
		} else {
			parent.stroke(255);
			parent.noFill();
			parent.ellipse(pos.x,pos.y,diameter, diameter);
			
			parent.noStroke();
			switch(type) {
			case "bomb":
				parent.fill(255,0,0);
				break; 
			case "split": 
				parent.fill(0,0,255);
				break;
			}
			
		}

		parent.ellipse(pos.x, pos.y, PApplet.map(growthTimer, 0, FULL_GROWN_TIME, 1, diameter),
				PApplet.map(growthTimer, 0, FULL_GROWN_TIME, 1, diameter));

	}

	public void die(String type) {
		super.die(type);
		stopSound();
		dead = true;
		stopSound();
	}
	void produceSound() {
		parent.game.gameObjects.add(new Fx (pos.x,pos.y,"SOUND", parent, this));
		wave.unpatch(out);
		// patch the Oscil to the output
		wave.patch(out);
	}

	void stopSound() {
		wave.unpatch(out);
	}

}
