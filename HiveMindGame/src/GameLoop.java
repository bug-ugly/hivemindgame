import java.util.ArrayList;
//game loop takes care of all the game objects, making sure they are updated and rendered every frame, it also manages dead objects
public class GameLoop {
	int objectsTimer;
	
	HiveMind parent;
	ArrayList <GameObject> gameObjects; //list of all game objects
	
	int counter;
	
	GameLoop(HiveMind p){
		parent = p;
		gameObjects = new ArrayList <GameObject>();
		
		gameObjects.add(new Player(parent, parent.width/2, parent.height/2));
		for ( int i = 0; i< 100; i++) {
			gameObjects.add(new Particle(parent,parent.width/2, parent.height/2));
		}
		
		gameObjects.add(new WorldObject(parent, parent.random(parent.width/2-100, parent.width/2+100),parent.random(parent.height/2-100, parent.height/2+100),true));
	}
	
	//separating update and render cycles in order to avoid unpredictable visual artifacts and preserve frame rate
	void update() {
		objectsTimer ++ ;
		if ( objectsTimer > 100) {
			for(int i = 0; i < gameObjects.size(); i++) {
				if ( gameObjects.get(i) instanceof WorldObject) {
					gameObjects.get(i).dead = true;
					
				}
			}
			

			objectsTimer = 0;
		}
		clearDead();
		counter = 0;
		for (GameObject g : gameObjects) {
			g.update();
			if ( g instanceof WorldObject) {
				counter ++ ;
			}
		}
		if (counter < 1) {
			float rand = parent.random(0,10);
				if (rand > 5) {
					gameObjects.add(new WorldObject(parent,  parent.random(parent.width/2-50, parent.width/2+50),parent.random(parent.height/2-50, parent.height/2+50),true));
				}
				else {
					gameObjects.add(new WorldObject(parent,  parent.random(parent.width/2-50, parent.width/2+50),parent.random(parent.height/2-50, parent.height/2+50),false));
				}
			
			
		}
		
	}
	
	//only responsible for drawing functions
	void render() {
		for (GameObject g : gameObjects) {
			g.render();
		}
	}
	
	//function removes objects designated as "dead" objects
	void clearDead() {
		//cycling backwards through the array list when removing objects from it
		//also when modifying the array list, running a normal for cycle
		for (int i = gameObjects.size()-1; i >= 0; i--) {
			if (gameObjects.get(i).dead) {
				gameObjects.remove(i);
				//a.removeObserver(parent.tutorial); // important to remove the observers in order to preserve framerate
			}
		}
	}
}
