import processing.core.PApplet;

class Ear {
	HiveMind parent;
	Particle currentObj;
	 int memoryTime;
	 int memoryCounter;
	 float pingX; 
	 float pingY; 
	 float pingF;
	
	Ear(Particle _a, HiveMind p) {
		parent = p;
		currentObj = _a;
		memoryTime = 5;
		memoryCounter = 0;
		pingF = (float) 0.01;
	}

	// analyse the sounds produced by other aliens


	float[] getClosestFrequency() {
		
		for ( int i = 0; i< parent.game.gameObjects.size(); i++) {
			GameObject g = parent.game.gameObjects.get(i);
			if(g instanceof WorldObject && PApplet.dist(g.pos.x,g.pos.y,currentObj.pos.x,currentObj.pos.y)< currentObj.hearingRange && g.soundPlaying && g.pos.z == 1) {
				if(memoryCounter == 0) {
					pingF = g.wave.frequency.getLastValue();
					pingX = g.pos.x; 
					pingY = g.pos.y;
					memoryCounter = 1;
				}
				else if(PApplet.dist(g.pos.x,g.pos.y, currentObj.pos.x, currentObj.pos.y)<PApplet.dist(pingX, pingY, currentObj.pos.x, currentObj.pos.y)) {
					pingF = g.wave.frequency.getLastValue();
					pingX = g.pos.x;
					pingY = g.pos.y;
					memoryCounter =1;
				}
			}
		}
		
		if(memoryCounter > 0) {
			memoryCounter ++ ; 
		}
		
		if (memoryCounter > memoryTime) {
			pingF = (float) 0.01; 
			memoryCounter= 0;
		}
		
		if ( pingF > 0.01) {
		float [] vals = new float [] {pingF, pingX, pingY};
		return vals;
		}
		else {
			return null;
		}
	}
	

}
