import ddf.minim.AudioOutput;
import ddf.minim.ugens.Oscil;
import processing.core.PVector;

public class GameObject {
	AudioOutput out;
	Oscil wave;
	HiveMind parent;
	PVector pos;

	boolean dead = false;
	float diameter;
	boolean collidable;
	boolean triggerNoise = false;
	boolean soundPlaying = false;
	
	boolean good;

	public int hearingRange;
	
	void update() {
	
	}
	
	void render() {
		
		
	}
	
	
	void backPropagate(String dir) {
		
	}

	public GameObject getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
