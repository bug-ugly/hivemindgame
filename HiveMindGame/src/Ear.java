import processing.core.PApplet;

class Ear {
	HiveMind parent;
	Particle currentObj;
	 int sensorSize; 
	 int memoryTime;
	 float[][]sensorMatrix;
	 float [][] positionsX;
	 float [][] positionsY;
	 int [][]matrixTimers;
	 int matrixLength;

	
	Ear(Particle _a, HiveMind p, int s_sze) {
		parent = p;
		currentObj = _a;
		sensorSize = s_sze; 
		memoryTime = 30;
		sensorMatrix = new float [currentObj.hearingRange/sensorSize*2][currentObj.hearingRange/sensorSize*2];
		matrixTimers = new int [sensorMatrix.length][sensorMatrix.length];
		matrixLength = sensorMatrix.length*sensorMatrix.length;
		positionsX = new float[sensorMatrix.length][sensorMatrix.length];
		positionsY = new float[sensorMatrix.length][sensorMatrix.length];
		
		for ( int i = 0; i<sensorMatrix.length; i++){
			for ( int j = 0; j < sensorMatrix[i].length; j++) {
				sensorMatrix[i][j] = (float) 0.01;
			}
		}
	}

	// analyse the sounds produced by other aliens
	float [] readSound() {
	   float []inp = new float [matrixLength];
	   //ArrayList <Float> values = new ArrayList <Float>();
	    int inpCounter = 0;
	    
	    for ( int i = 0; i < sensorMatrix.length; i++){
	      for ( int j = 0; j < sensorMatrix[i].length; j++){
	      positionsX[i][j] = (currentObj.pos.x - currentObj.hearingRange) + (sensorSize * i);
	      positionsY[i][j] = (currentObj.pos.y - currentObj.hearingRange) + (sensorSize * j);
	      
	      //stroke(0);
	      //point ( matX, matY);
	        for ( int k = 0; k < parent.game.gameObjects.size(); k++){
	          GameObject a = parent.game.gameObjects.get(k); 
	          if ( a != currentObj && a.soundPlaying && a.pos.x <= positionsX[i][j] + sensorSize/2 && a.pos.x > positionsX[i][j] - sensorSize/2 && a.pos.y <= positionsY[i][j] + sensorSize/2 && a.pos.y > positionsY[i][j] - sensorSize/2){
	            sensorMatrix[i][j] = PApplet.map(a.wave.frequency.getLastValue(),0,1000,(float) 0.1,1);
	            matrixTimers[i][j]= 1; 
	          }
	        }
	       if (matrixTimers[i][j] > 0){
	         matrixTimers[i][j] ++ ; 
	         if ( matrixTimers[i][j] >= memoryTime){
	           sensorMatrix[i][j] = (float) 0.01;
	           matrixTimers[i][j]= 0;
	         }
	       }
	       
	       
	       inp [inpCounter] = sensorMatrix[i][j];

	    	   
	        inpCounter ++ ;
	        
	      }
	    }
	    
	    return inp;

	}

	float[] getClosestFrequency() {
		float value = (float) 0.01;
		float posX = 1000; 
		float posY = 1000;
		readSound();
		for ( int i = 0; i< positionsX.length; i++) {
			for(int j = 0; j<positionsX[i].length; j++) {
				if (sensorMatrix[i][j]> 0.01 && PApplet.dist(positionsX[i][j], positionsY[i][j], currentObj.pos.x, currentObj.pos.y) <= currentObj.sensorDist) {
					value = sensorMatrix[i][j];
					posX = positionsX[i][j];
					posY = positionsY[i][j];
					
				}
			}
		}
		float [] vals = new float [] {value, posX, posY};
		return vals;
	}
	
	void drawEar() {
		for (int i = 0; i< positionsX.length; i++) {
			for (int j = 0; j< positionsX[i].length; j++) {
				//parent.point (positionsX[i][j], positionsY[i][j]);
				parent.textSize(10);
				parent.text((int)(sensorMatrix[i][j]*100), positionsX[i][j], positionsY[i][j]);
			}
		}
	}

}
