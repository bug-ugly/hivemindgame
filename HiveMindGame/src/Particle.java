import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Particle extends GameObject {
	Ear ear;
	public int particleSize = 5;
	float direction;
	float speed;
	float clusterRange = 100;
	float particleRange = 100;
	String[] actions;
	float explosionRange = 10;
	NeuralNetwork net; 
	int [] layers;
	
	float lookDirection = 0;
	float sensorData = (float) 0.1; 
	float sensorDist = 50;
	
	float pX; 
	float pY;
	
	Particle(HiveMind p, float x, float y) {
		parent = p;
		pos = new PVector(x, y);
		diameter = 5;
		layers = new int[] {3,20,16,3};
		net = new NeuralNetwork(layers,(float) 0.001,parent);
		collidable = true;
		speed = 1;
		hearingRange = 100;
		ear = new Ear(this, parent, 30);
	}
	Particle(HiveMind p, float x, float y, NeuralNetwork _n) {
		net.setParams(_n);
		parent = p;
		pos = new PVector(x, y);
		diameter = 5;
		collidable = true;
		speed = 1;
		hearingRange = 100;
		ear = new Ear(this, parent, 30);
	}

	void update() {
		super.update();
		
		collisionStuff();
		
		act (net.FeedForward (getSensorData()));
	
	}
	
	void collisionStuff() {
		
		
		direction = PApplet.atan2(getPlayer().pos.y - pos.y, getPlayer().pos.x - pos.x);

		if (checkCollision() != null) {
			if (checkCollision().pos.x == pos.x && checkCollision().pos.y == pos.y) {
				direction = parent.random(PConstants.PI * 2);
			} else {
				direction = PApplet.atan2(checkCollision().pos.y - pos.y, checkCollision().pos.x - pos.x)
						+ PConstants.PI;
			}

		}
		if (checkCollision() instanceof WorldObject) {
			if (checkCollision().good) {
				split();
			} else {
				explode();
			}
			checkCollision().dead = true;
			
		}

		float newX = PApplet.cos(direction) * speed + pos.x;
		float newY = PApplet.sin(direction) * speed + pos.y;
		pos.set(newX, newY);
		
	    

	}
	
	float []getSensorData(){
		float [] data;
		
		//getting the closest frequency from the ear table 
		float freq = ear.getClosestFrequency()[0];
		pX = ear.getClosestFrequency()[1];
		pY = ear.getClosestFrequency()[2];
		float tdistX = pX - pos.x; 
		float tdistY = pY - pos.y;
		
		data = new float [] {freq, tdistX, tdistY};
		return data;
	}
	
	void split() {
		for(int i = 0; i< 5; i++) {
		parent.game.gameObjects.add(new Particle(parent,pos.x,pos.y,net));
		}
	}
	
	void explode() {
		for(int i = 0; i< parent.game.gameObjects.size(); i++) {
			GameObject g = parent.game.gameObjects.get(i);
			if ( g instanceof Particle && PApplet.dist(g.pos.x, g.pos.y, pos.x,pos.y)< explosionRange) {
				g.dead = true;
			}
		}
	}

	void render() {
		super.render();
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
		for (int i = 0; i< output.length; i++) {
			if ( output[i]> maxVal) {
				maxVal = output[i];
				num = i;
			}
		}
		
		float angle = PApplet.atan2(pY - pos.y, pX - pos.x);
		switch(num) {
		case 0: 
			//avoid
			
			
			pos.set(
					 (float)(PApplet.cos(angle + PConstants.PI) * speed/1.4 + pos.x), 
					 (float)(PApplet.sin(angle + PConstants.PI) * speed/1.4 + pos.y)
					);
			break; 
		case 1: 
			//follow
			 
			pos.set(
					(float)(PApplet.cos(angle) * speed/1.4 + pos.x), 
					(float)(PApplet.sin(angle) * speed/1.4 + pos.y)
					);
			break; 
		
		case 2: 
			//do nothing
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

