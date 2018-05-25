import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Particle extends GameObject {
	Ear ear;
	NeuralNetwork net;
	public final int PARTICLE_SIZE = 5; // size of the particle
	
	final float MUTATION_CHANCE = (float) 0.99; // actual mutation chance will be 1 - "mutation_chance"
	final float L = (float) 0.001; // learning constant
	final float PARTICLE_SPEED = 1; // speed of movement
	final int PARTICLE_HEARING_RANGE = 100; // range at which it will hear ping
	final int INPUT_SIZE = 1; // size of the neural network input
	final int OUTPUT_SIZE = 3; // size of the neural network output

	float pX; // x position of the closest frequency detected by the ear
	float pY; // y position of the closest frequency detected by the ear
	float direction; // current movement direction
	float speed;
	String[] actions;
	int[] layers;
	float[] earFreq;
	
	boolean following = false;

	Particle(HiveMind p, float x, float y) {
		parent = p;
		pos = new PVector(x, y);
		diameter = PARTICLE_SIZE;
		layers = new int[] { INPUT_SIZE, 10,5, OUTPUT_SIZE };
		net = new NeuralNetwork(layers, L, parent);
		collidable = true;
		speed = PARTICLE_SPEED;
		hearingRange = PARTICLE_HEARING_RANGE;
		ear = new Ear(this, parent);
	}

	Particle(HiveMind p, float x, float y, NeuralNetwork _n) {
		parent = p;
		pos = new PVector(x, y);
		diameter = PARTICLE_SIZE;
		layers = new int[] { INPUT_SIZE, 10,5,OUTPUT_SIZE };
		net = new NeuralNetwork(layers, _n, L, parent);
		if (parent.random(0, 1) > MUTATION_CHANCE) {
			net.Mutate();
		}
		collidable = true;
		speed = PARTICLE_SPEED;
		hearingRange = PARTICLE_HEARING_RANGE;
		ear = new Ear(this, parent);
	}

	void update() {
		super.update();
		
		
		collisionStuff();
		checkWorldObjects();

		following = false;
		
		earFreq = ear.getClosestFrequency();
		if (earFreq != null) {
			act(net.FeedForward(getSensorData()));
		}

	}

	// function contain collision algorithms. Designed to run in update cycle
	void collisionStuff() {

		// default movement direction is towards the player, calculating vector pointing
		// at player
		direction = PApplet.atan2(getPlayer().pos.y - pos.y, getPlayer().pos.x - pos.x);
	
		// displacement if there is collision with other objects
		if (checkCollision() != null) {
			if (checkCollision().pos.x == pos.x && checkCollision().pos.y == pos.y) {
				// in case if the colliding body is on the same coordinate and it is impossible
				// to calculate the directional vector, the random vector is generated
				direction = parent.random(PConstants.PI * 2);
			} else {
				// vector pointing away from colliding body
				direction = PApplet.atan2(checkCollision().pos.y - pos.y, checkCollision().pos.x - pos.x)
						+ PConstants.PI;
			}

		}

		// set the new position
		float newX = PApplet.cos(direction) * speed + pos.x;
		float newY = PApplet.sin(direction) * speed + pos.y;
		pos.set(newX, newY);

	}
	
	void checkWorldObjects() {
		// checking collision with the world objects and calling the resulting behaviour
		if (checkCollision() instanceof WorldObject && checkCollision().pos.z == 1 && following) {
			checkCollision().pickUp(this);

		}
	}

	float[] getSensorData() {
		float[] data;

		// getting the closest frequency from the ear table
		float freq = earFreq[0];
		pX = earFreq[1];
		pY = earFreq[2];
		float tdistX = pX - pos.x;
		float tdistY = pY - pos.y;
		float pdistX = getPlayer().pos.x - pos.x;
		float pdistY = getPlayer().pos.y - pos.y;
		freq = PApplet.map(freq, 0, 700, -1, 1);
		data = new float[] {freq};
		return data;
	}

	public void split() {
		super.split();
		// parent.println("beep");
		for (int i = 0; i < 2; i++) {
			parent.game.gameObjects.add(new Particle(parent, pos.x, pos.y, net));
	
		}
	}


	
	public void die(String deathType) {
		super.die(deathType);
		switch(deathType){
		case "EXPLOSION": 
			parent.game.gameObjects.add(new Fx(pos.x,pos.y, "BLOOD", parent));
			break; 
		}
		dead = true;
	}

	void render() {
		super.render();
		parent.noStroke();
		parent.fill(0);
		parent.ellipse(pos.x, pos.y, diameter, diameter);

	}

	public GameObject getPlayer() {
		super.getPlayer();
		for (int i = 0; i < parent.game.gameObjects.size(); i++) {
			if (parent.game.gameObjects.get(i) instanceof Player) {
				return parent.game.gameObjects.get(i);
			}
		}
		return null;
	}

	void act(float[] output) {
		
		float maxVal = output[0];
		int num = 0;
		for (int i = 0; i < output.length; i++) {
			if (output[i] > maxVal) {
				maxVal = output[i];
				num = i;
			}
		}
		PApplet.println(output);

		float angle = PApplet.atan2(pY - pos.y, pX - pos.x);
		if (pX == pos.x && pY == pos.y) {
			angle = angle + parent.random(PConstants.PI);
		}
		switch (num) {
		case 0:
			// avoid
			pos.set((float) (PApplet.cos(angle + PConstants.PI) * speed + pos.x),
					(float) (PApplet.sin(angle + PConstants.PI) * speed + pos.y));
			break;
		case 1:
			// follow
			following = true;
			pos.set((float) (PApplet.cos(angle) * speed + pos.x), (float) (PApplet.sin(angle) * speed + pos.y));
			break;

		case 2:
			// do nothing
			break;

		}
	}

	GameObject checkCollision() {
		for (GameObject a : parent.game.gameObjects) {
			if (a != this && a.collidable) {
				if (PApplet.dist(pos.x, pos.y, a.pos.x, a.pos.y) < diameter / 2 + a.diameter / 2) {
					return a;
				}
			}
		}
		return null;
	}

}
