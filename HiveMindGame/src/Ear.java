import processing.core.PApplet;

class Ear {
	HiveMind parent;
	Particle currentObj;
	int memoryTime; // time during which the ping is remembered
	int memoryCounter; // counts the frames during which the temporary memory stays
	float pingX; // position X of the detected ping
	float pingY; // position Y of the detected ping
	float pingF; // frequency of the detected ping

	Ear(Particle _a, HiveMind p) {
		parent = p;
		currentObj = _a;
		memoryTime = 20; // number of frames during which the last ping is remembered before switching
						// back to 0.01
		memoryCounter = 0; // setting the temporary memory counter to 0
		pingF = (float) 0.01; // default minimum frequency is set to 0.01 instead of 0 so the neural network
								// calculations are more efficient (currently this value is not used by neural
								// network)
	}

	// find the closest frequency, save it in the temporary memory if there are no
	// closer frequencies
	float[] getClosestFrequency() {

		for (int i = 0; i < parent.game.gameObjects.size(); i++) {
			GameObject g = parent.game.gameObjects.get(i);
			if (g instanceof WorldObject
					&& PApplet.dist(g.pos.x, g.pos.y, currentObj.pos.x, currentObj.pos.y) < currentObj.hearingRange
					&& g.soundPlaying && g.pos.z == 1) {
				if (memoryCounter == 0) {
					pingF = g.wave.frequency.getLastValue();
					pingX = g.pos.x;
					pingY = g.pos.y;
					memoryCounter = 1; // activating the counter by setting it higher than 0
				} else if (PApplet.dist(g.pos.x, g.pos.y, currentObj.pos.x, currentObj.pos.y) < PApplet.dist(pingX,
						pingY, currentObj.pos.x, currentObj.pos.y)) {
					pingF = g.wave.frequency.getLastValue();
					pingX = g.pos.x;
					pingY = g.pos.y;
					memoryCounter = 1; // activating the counter by setting it higher than 0
				}
			}
		}

		// counter only counts when set to higher than 0
		if (memoryCounter > 0) {
			memoryCounter++;
		}

		if (memoryCounter > memoryTime) { // resetting the counter and ping, forgetting the last frequency
			pingF = (float) 0.01;
			memoryCounter = 0;
		}

		if (pingF > 0.01) { // only return when the ping has a value which is not default
			float[] vals = new float[] { pingF, pingX, pingY };
			return vals;
		} else {
			return null;
		}
	}

}
