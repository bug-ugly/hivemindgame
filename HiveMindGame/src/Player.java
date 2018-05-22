import java.util.ArrayList;

import ddf.minim.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Player extends GameObject {
	DeepQNetwork RLNet; // neural net
	// variable for nn initialisation
	int replayMemoryCapacity = 12; // how many replays can be stored in the memory
	float discount = .99f; // discount
	double epsilon = 1d; // exploration rate
	int batchSize = 12; // how many replays are processed at a time
	int updateFreq = 1; // update rate of the nn
	int replayStartSize = 12; // how many replays need to be accumulated for the nn to start processing them

	public float L = (float) 0.001; // learning constant
	
	ArrayList<float[]> lastOutputs;
	String[] actions;
	int NumActions;
	int[] _layers;
	final float ratedActionsNum = 50; // number of the last actions that are going to be rated by the player, there
	// needs to be many actions (10) for the neural network precision
	float reward; // current reward
	final int InputLength = 513; // the size of the input that the agent receives. the sound spectrum is the
	// total of 513 numbers
	
	 final float player_speed = 3;
	 final float player_diameter = 10;
	 float direction;
	 
	Player(HiveMind p, int _x, int _y) {
		parent = p;
		pos = new PVector(_x, _y,0);

		lastOutputs = new ArrayList<float[]>();

		actions = new String[] { "up", "down", "left", "right", "nothing" };
		NumActions = actions.length;
		_layers = new int[] { InputLength, 50, 25, 10, NumActions };
	    RLNet = new DeepQNetwork(_layers, replayMemoryCapacity, discount, epsilon, batchSize, updateFreq, replayStartSize, InputLength, NumActions, L, parent);
	    diameter = player_diameter; 
	    direction = 0;
	    collidable = true;
	}
	
	void update() {
		super.update();
		playerListen();
		checkWallCollision();
	}
	
	void render() {
		super.render();
		parent.noStroke();
		parent.fill(0);
		parent.ellipse (pos.x, pos.y, diameter, diameter);
	}
	
	 //function to respond to sounds from player
	  void playerListen() {
	    if ((parent.in.left.level() + parent.in.right.level()) /2 >parent.minimum_s_level) {
	      parent.rewardsActive = true;
	      int top = RLNet.GetAction(getSoundSpectrum(parent.in), GetActionMask(NumActions)); //get the action from nn
	      lastOutputs.add(getSoundSpectrum(parent.in)); //add the action to the list of last outputs
	      while (lastOutputs.size() > ratedActionsNum) { //check if number of last outputs is past the limit and remove the excess actions
	        lastOutputs.remove(0);
	      }
	      performAction(top); //perform the top selected action
	    }
	  }
	  
	  void performAction(int t) {
		    switch (actions[t]) {
		    case "up": 
		      moveUp();
		      break; 
		    case "down": 
		      moveDown();
		      break; 
		    case "left": 
		      moveLeft();
		      break; 
		    case "right": 
		      moveRight();
		      break; 
		    case "nothing": 
		      //do nothing
		      break;
		    }
		  }
	  
	// makes sure that the agent doesnt leave the screen borders
		void checkWallCollision() {
			if (pos.x < 0) {
				pos.x = 0;
			}
			if (pos.x > parent.width) {
				pos.x = parent.width;
			}
			if (pos.y > parent.height) {
				pos.y = parent.height;
			}
			if (pos.y < 0) {
				pos.y = 0;
			}
		}
	  
	  float[] getSoundSpectrum(AudioInput in) {
			parent.fftLin.forward(in.mix);
			float[] spectrum = new float[parent.fftLin.specSize()];
			for (int i = 0; i < parent.fftLin.specSize(); i++) {
				spectrum[i] = parent.fftLin.getBand(i);
			}
			return spectrum;
		}
	  
	  int[] GetActionMask(int numActions) {
			int retVal[] = new int[numActions];
			for (int i = 0; i < numActions; i++) {
				retVal[i] = 1;
			}
			return retVal;
		}
	  
	  void backPropagate(String dir) {
		  super.backPropagate(dir);
			rewardPrevious(dir);
		}
	  
	// reward in case if the correct action is given by the player
		void rewardPrevious(String dir) {
			int act = 4;
			if (dir.equals("Up")) {
				act = 0;
				//setEvent("EVENT_SWIPE_UP");
			}
			if (dir.equals("Down")) {
				act = 1;
				//setEvent("EVENT_SWIPE_DOWN");
			}
			if (dir.equals("Left")) {
				act = 2;
				//setEvent("EVENT_SWIPE_LEFT");
			}
			if (dir.equals("Right")) {
				act = 3;
				//setEvent("EVENT_SWIPE_RIGHT");
			}
			for (int i = 0; i < lastOutputs.size(); i++) {
				RLNet.correctAction(act, lastOutputs.get(i), GetActionMask(NumActions));
			}
		}
		
		// actions to be selected by the output of the neural network
		void moveUp() {
			direction = PConstants.PI + PConstants.PI / 2;
			float newX = PApplet.cos(direction) * player_speed + pos.x;
			float newY = PApplet.sin(direction) * player_speed + pos.y;
			pos.set(newX, newY);
		}

		void moveLeft() {
			direction = PConstants.PI;
			float newX = PApplet.cos(direction) * player_speed + pos.x;
			float newY = PApplet.sin(direction) * player_speed + pos.y;
			pos.set(newX, newY);
		}

		void moveRight() {
			direction = 0;
			float newX = PApplet.cos(direction) * player_speed + pos.x;
			float newY = PApplet.sin(direction) * player_speed + pos.y;
			pos.set(newX, newY);
		}

		void moveDown() {
			direction = PConstants.PI / 2;

			float newX = PApplet.cos(direction) * player_speed + pos.x;
			float newY = PApplet.sin(direction) * player_speed + pos.y;
			pos.set(newX, newY);
		}

		
		

}
