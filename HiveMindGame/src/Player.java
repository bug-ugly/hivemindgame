import java.util.ArrayList;

import ddf.minim.AudioInput;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Player extends GameObject {
	DeepQNetwork RLNet; // neural net
	// variable for nn initialisation
	final int REPLAY_MEMORY_CAPACITY = 12; // how many replays can be stored in the memory
	final float DISCOUNT = .99f; // discount
	final double EPSILON = 1d; // exploration rate
	final int BATCH_SIZE = 12; // how many replays are processed at a time
	final int UPDATE_FREQ = 1; // update rate of the nn
	final int REPLAY_START_SIZE = 12; // how many replays need to be accumulated for the nn to start processing them
	final float RATED_ACTIONS_NUM = 50; // number of the last actions that are going to be rated by the player, there
	// needs to be many actions (10) for the neural network precision
	final int INPUT_LENGTH = 513; // the size of the input that the agent receives. the sound spectrum is the
	// total of 513 numbers

	final public float L = (float) 0.001; // learning constant

	final float PLAYER_SPEED = 1; // speed with which the player moves
	final float PLAYER_DIAMETER = 10; // diameter of the player

	ArrayList<float[]> lastOutputs;
	String[] actions;
	int NumActions;
	int[] _layers; // declaring the layers of the neural net
	float reward; // current reward
	float direction; // current direction

	Player(HiveMind p, int _x, int _y) {
		parent = p;
		pos = new PVector(_x, _y, 0);
		lastOutputs = new ArrayList<float[]>();

		actions = new String[] { "up", "down", "left", "right", "nothing" };
		NumActions = actions.length;
		_layers = new int[] { INPUT_LENGTH, 50, 25, 10, NumActions };
		RLNet = new DeepQNetwork(_layers, REPLAY_MEMORY_CAPACITY, DISCOUNT, EPSILON, BATCH_SIZE, UPDATE_FREQ,
				REPLAY_START_SIZE, INPUT_LENGTH, NumActions, L, parent);
		diameter = PLAYER_DIAMETER;
		direction = 0;
		collidable = true;
	}

	void update() {
		super.update();
		if(parent.mouseMode == false) {
		playerListen();
		}
		else {
		playerFollowMouse();
		}
		checkWallCollision();
	}

	void render() {
		super.render();
		parent.stroke(255);
		parent.noFill();
		parent.ellipse(pos.x, pos.y, diameter, diameter);
	}
	
	void playerFollowMouse() {
		direction = PApplet.atan2(parent.mouseY - pos.y, parent.mouseX - pos.x);
		float newX = PApplet.cos(direction) * PLAYER_SPEED + pos.x;
		float newY = PApplet.sin(direction) * PLAYER_SPEED + pos.y;
		pos.set(newX, newY);
	}

	// function to respond to sounds from player
	void playerListen() {
		if ((parent.in.left.level() + parent.in.right.level()) / 2 > parent.minimum_s_level) {
			parent.rewardsActive = true;
			int top = RLNet.GetAction(getSoundSpectrum(parent.in), GetActionMask(NumActions)); // get the action from nn
			lastOutputs.add(getSoundSpectrum(parent.in)); // add the action to the list of last outputs
			while (lastOutputs.size() > RATED_ACTIONS_NUM) { // check if number of last outputs is past the limit and
																// remove the excess actions
				lastOutputs.remove(0);
			}
			performAction(top); // perform the top selected action
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
			// do nothing
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
			// setEvent("EVENT_SWIPE_UP");
		}
		if (dir.equals("Down")) {
			act = 1;
			// setEvent("EVENT_SWIPE_DOWN");
		}
		if (dir.equals("Left")) {
			act = 2;
			// setEvent("EVENT_SWIPE_LEFT");
		}
		if (dir.equals("Right")) {
			act = 3;
			// setEvent("EVENT_SWIPE_RIGHT");
		}
		for (int i = 0; i < lastOutputs.size(); i++) {
			RLNet.correctAction(act, lastOutputs.get(i), GetActionMask(NumActions));
		}
	}

	// actions to be selected by the output of the neural network
	void moveUp() {
		direction = PConstants.PI + PConstants.PI / 2;
		float newX = PApplet.cos(direction) * PLAYER_SPEED + pos.x;
		float newY = PApplet.sin(direction) * PLAYER_SPEED + pos.y;
		pos.set(newX, newY);
	}

	void moveLeft() {
		direction = PConstants.PI;
		float newX = PApplet.cos(direction) * PLAYER_SPEED + pos.x;
		float newY = PApplet.sin(direction) * PLAYER_SPEED + pos.y;
		pos.set(newX, newY);
	}

	void moveRight() {
		direction = 0;
		float newX = PApplet.cos(direction) * PLAYER_SPEED + pos.x;
		float newY = PApplet.sin(direction) * PLAYER_SPEED + pos.y;
		pos.set(newX, newY);
	}

	void moveDown() {
		direction = PConstants.PI / 2;

		float newX = PApplet.cos(direction) * PLAYER_SPEED + pos.x;
		float newY = PApplet.sin(direction) * PLAYER_SPEED + pos.y;
		pos.set(newX, newY);
	}

}
