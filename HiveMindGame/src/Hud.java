
import controlP5.Slider;

//Hud elements on the screen
class Hud {
	HiveMind parent;
	Slider levels;
	final int levelsX = 100;
	final int levelsY;
	final int levelsWidth = 500;

	public Hud(HiveMind p) {
		parent = p;
		levelsY = parent.height - 50;
		createSoundLevelControl();
	}

	void update() {
		showMicSoundLevels();
		
	}

	// control the sensitivity of the microphone in order to remove the unnecessary
	// noise
	void showMicSoundLevels() {
		float current_level = ((parent.in.left.level() + parent.in.right.level()) / 2);
		float endLineX = HiveMind.map(current_level, 0, 1, levelsX, levelsX + levelsWidth); // position of the tip of
																								// the line indicator
		if (current_level > parent.minimum_s_level) {
			parent.stroke(0);
			parent.fill(200, 200, 200);
		} else if (endLineX > levelsX + levelsWidth) {
			parent.fill(255, 0, 0);
			parent.stroke(255, 0, 0);
		} else {
			parent.noFill();
			parent.stroke(100, 100, 100);
		}
		parent.line(levelsX, levelsY + 10, endLineX, levelsY + 10);
		parent.noStroke();
		parent.ellipse(levelsX + 5, levelsY + 20, 10, 10);
		// text ( "levels: " + current_level,levelsX, levelsY + 30);
	}

	
	void createSoundLevelControl() {
		parent.cp5.addSlider("minimum_s_level").setPosition(levelsX, levelsY).setRange(0, 1).setWidth(levelsWidth);

	}

	void slider(float level) {
		parent.minimum_s_level = level;
	}


}
