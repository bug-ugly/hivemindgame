import ddf.minim.AudioOutput;
import ddf.minim.ugens.Oscil;
import processing.core.PVector;

//various shared variables of game objects 
//needed in order to perform update and render cycles
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
	
	final int PLAYER_RANGE = 150;

	public int hearingRange;

	void update() {

	}

	void render() {

	}

	void backPropagate(String dir) {

	}

	public GameObject getPlayer() {
		for (int i = 0; i < parent.game.gameObjects.size(); i++) {
			if (parent.game.gameObjects.get(i) instanceof Player) {
				return parent.game.gameObjects.get(i);
			}
		}
		return null;
	}

	public void die(String string) {
		// TODO Auto-generated method stub
		
	}

	public void split() {
		// TODO Auto-generated method stub
		
	}

	public void pickUp(GameObject particle) {
		// TODO Auto-generated method stub
		
	}

}
