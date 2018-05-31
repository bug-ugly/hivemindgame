import java.util.ArrayList;

//game loop takes care of all the game objects, making sure they are updated and rendered every frame, it also manages dead objects
public class GameLoop {

	HiveMind parent;
	ArrayList<GameObject> gameObjects; // list of all game objects
	int counter;

	GameLoop(HiveMind p) {
		parent = p;
		gameObjects = new ArrayList<GameObject>();

		gameObjects.add(new Player(parent, parent.width / 2, parent.height / 2));
		for (int i = 0; i < 20; i++) {
			gameObjects.add(new Particle(parent, parent.width / 2, parent.height / 2));
		}

	
	}

	// separating update and render cycles in order to avoid unpredictable visual
	// artifacts and preserve frame rate
	void update() {

		clearDead(); // clearing dead objects routine

		counter = 0;
		for (int i = 0; i < gameObjects.size(); i++) {
			GameObject g = gameObjects.get(i);
			g.update();
			if (g instanceof WorldObject) {
				counter++;
			}
		}
		if (counter < 30 && parent.random(1) > 0.98) {
			float rand = parent.random(0, 10);
			if (rand > 5) {
				gameObjects.add(new WorldObject(parent, parent.random(parent.width),
						parent.random(parent.height), "split"));
			} else {
				gameObjects.add(new WorldObject(parent, parent.random(parent.width),
						parent.random(parent.height), "bomb"));
			}

		}

	}

	// only responsible for drawing functions
	void render() {
		for (GameObject g : gameObjects) {
			if(g instanceof Fx) {
			g.render();
			}
		}
		for (GameObject g : gameObjects) {
			if(g instanceof Fx) {
			
			}else {
			g.render();
			}
		}
	}

	// function removes objects designated as "dead" objects
	void clearDead() {
		// cycling backwards through the array list when removing objects from it
		// also when modifying the array list, running a normal for cycle
		for (int i = gameObjects.size() - 1; i >= 0; i--) {
			if (gameObjects.get(i).dead) {
				
				gameObjects.remove(i);
				// a.removeObserver(parent.tutorial); // important to remove the observers in
				// order to preserve framerate
			}
		}
	}
}
