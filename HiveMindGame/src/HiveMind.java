

import ddf.minim.*;
import processing.core.PApplet;
import ddf.minim.analysis.FFT;
import controlP5.*;

public class HiveMind extends PApplet {
	public Swiper swipeController; 
	public ControlP5 cp5;
	public GameLoop game;
	
	public Minim minim;
	public AudioInput in; // sound from mic
	public FFT fftLin;
	public Hud hud;
	
	
	
	// minimum sound level of the mic to be perceived by the aliens
	public float minimum_s_level = (float) 0.09;
	public boolean rewardsActive = false;

	public static void main(String[] args) {
		PApplet.main("HiveMind");
		
	}

	public void settings() {
		size(1280, 720, P3D);
	
	}

	public void setup() {
		
		cp5 = new ControlP5(this);
		minim = new Minim(this);
		in = minim.getLineIn(); // mic
		fftLin = new FFT(in.bufferSize(), in.sampleRate());
		fftLin.linAverages(30);
		hud = new Hud(this);
		swipeController = new Swiper(this);
		game = new GameLoop(this);


	}

	public void draw() {
		background(255);
		game.update();
		game.render();
		hud.update();
		swipeController.update();
		
	}
	

	public void mouseDragged() {
		if (mouseY < height - 70) { // only register swipe event when not intercepting the levels control bar
			swipeController.swipeEvent();
		}
	}

	public void mouseReleased() {
		swipeController.swipeFinished();
	}

	float[] getaRow(float[][] array, int i) {
		float myRow[] = new float[array[i].length];
		for (int l = 0; l < array[i].length; l++) {
			myRow[l] = array[i][l];
		}
		return myRow;
	}

}
