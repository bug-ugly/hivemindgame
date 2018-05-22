import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

public class Particle extends GameObject {
	Ear ear;
	public int particleSize = 5;
	float direction;
	float speed;
	//float clusterRange = 100;
	//float particleRange = 100;
	String[] actions;
	float explosionRange = 10;
	NeuralNetwork net; 
	int [] layers;
	
	float pX; 
	float pY;
	
	float [] earFreq;
	Particle(HiveMind p, float x, float y) {
		parent = p;
		pos = new PVector(x, y,0);
		diameter = 5;
		layers = new int[] {3,20,16,3};
		net = new NeuralNetwork(layers,(float) 0.001,parent);
		collidable = true;
		speed = 1;
		hearingRange = 200;
		ear = new Ear(this, parent);
	}
	Particle(HiveMind p, float x, float y, NeuralNetwork _n) {
		parent = p;
		layers = new int[] {3,20,16,3};
		net = new NeuralNetwork(layers,_n,(float)0.001,parent);
		if(parent.random(0,1)>0.7) {
		net.Mutate();
		}
		pos = new PVector(x, y);
		diameter = 5;
		collidable = true;
		speed = 1;
		hearingRange = 100;
		ear = new Ear(this, parent);
	}

	void update() {
		super.update();
		
		collisionStuff();
		
		earFreq = ear.getClosestFrequency();
		if(earFreq != null) {
			act (net.FeedForward (getSensorData()));
		}
	
	}
	
	void collisionStuff() {
		
		
		direction = PApplet.atan2(getPlayer().pos.y - pos.y, getPlayer().pos.x - pos.x);

		if (checkCollision() instanceof WorldObject && checkCollision().pos.z == 1) {
			if (checkCollision().good == true) {
				split();
			} else {
				explode();
			}
			checkCollision().dead = true;
			
		}
		
		if (checkCollision() != null) {
			if (checkCollision().pos.x == pos.x && checkCollision().pos.y == pos.y) {
				direction = parent.random(PConstants.PI * 2);
			} else {
				direction = PApplet.atan2(checkCollision().pos.y - pos.y, checkCollision().pos.x - pos.x)
						+ PConstants.PI;
			}

		}
		

		float newX = PApplet.cos(direction) * speed + pos.x;
		float newY = PApplet.sin(direction) * speed + pos.y;
		pos.set(newX, newY);
		
	    

	}
	
	float []getSensorData(){
		float [] data;
		
		//getting the closest frequency from the ear table 
		float freq = earFreq[0];
		pX = earFreq[1];
		pY = earFreq[2];
		float tdistX = pX - pos.x; 
		float tdistY = pY - pos.y;
		
		data = new float [] {freq, tdistX, tdistY};
		return data;
	}
	
	void split() {
		//parent.println("beep");
		for(int i = 0; i< 2; i++) {
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
		parent.noStroke();
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
		if ( pX == pos.x && pY == pos.y) {
				angle = angle + parent.random(PConstants.PI);
				}
		switch(num) {
		case 0: 
			//avoid
			
			
			pos.set(
					 (float)(PApplet.cos(angle + PConstants.PI) * speed + pos.x), 
					 (float)(PApplet.sin(angle + PConstants.PI) * speed + pos.y)
					);
			break; 
		case 1: 
			//follow
			 
			pos.set(
					(float)(PApplet.cos(angle) * speed + pos.x), 
					(float)(PApplet.sin(angle) * speed + pos.y)
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

