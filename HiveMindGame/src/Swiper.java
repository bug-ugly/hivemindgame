import java.util.ArrayList;

import processing.core.PVector;

//controlling the swipe direction and shape
class Swiper {
	HiveMind parent;
	ArrayList<PVector> mouseCoordinates;
	boolean swiping;

	public Swiper(HiveMind p) {
		parent = p;
		mouseCoordinates = new ArrayList<PVector>();
		swiping = false;
	}

	void update() {
		if (mouseCoordinates != null) {
			for (int i = 1; i < mouseCoordinates.size(); i++) {
				PVector p = mouseCoordinates.get(i);
				PVector p2 = mouseCoordinates.get(i - 1);
				parent.strokeWeight((float) 0.5);
				parent.stroke(255, 0, 0);
				parent.line(p.x, p.y, p2.x, p2.y);
			}
		}
	}

	// adding new coordinates to the swipe
	void swipeEvent() {
		if (swiping == false) {
			mouseCoordinates = new ArrayList<PVector>();
		}
		swiping = true;
		mouseCoordinates.add(new PVector(parent.mouseX, parent.mouseY));
	}

	// end of the swipe
	void swipeFinished() {
		if (swiping) {
			determineSwipe(mouseCoordinates);
			swiping = false;
			mouseCoordinates = new ArrayList<PVector>();
		}
	}

	// end swipe calculation and determining the direction
	void determineSwipe(ArrayList<PVector> coordinates) {
		String swipe = "";
		if (coordinates != null) {
			int xDiff = (int) ((int) coordinates.get(coordinates.size() - 1).x - coordinates.get(0).x);
			int yDiff = (int) ((int) coordinates.get(coordinates.size() - 1).y - coordinates.get(0).y);
			// using square to compare because we want to compare the positive values only
			if (HiveMind.sq(yDiff) > HiveMind.sq(xDiff)) {
				if (coordinates.get(coordinates.size() - 1).y > coordinates.get(0).y) {
					swipe = "Down";
				} else {
					swipe = "Up";
				}
			} else if (HiveMind.sq(xDiff) > HiveMind.sq(yDiff)) {
				if (coordinates.get(coordinates.size() - 1).x > coordinates.get(0).x) {
					swipe = "Right";
				} else {
					swipe = "Left";
				}
			}
		}
		sendSwipe(swipe);
	}

	// sending the calculated direction to the player
	void sendSwipe(String swipe) {
		HiveMind.println(swipe);
		//parent.userController.sayDirection(swipe);
		if (parent.rewardsActive) {
			if (parent.game.gameObjects != null) {
				for (int i = 0; i < parent.game.gameObjects.size(); i++) {
					if (parent.game.gameObjects.get(i) instanceof Player) {
						parent.game.gameObjects.get(i).backPropagate(swipe);
					}
				}
			}
		}
	}
}
