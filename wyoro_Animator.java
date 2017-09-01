import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class wyoro_Animator {

	private EZImage Actor;
	private int x, y, startx, starty, destx, desty;
	private double startscale, scalefinal, scaling;
	private double rotationfinal, startRotation, rotate;
	private long starttime, duration;
	private boolean reading = true;
	private boolean initialstate = true;
	private boolean controlState = false;
	private String txtfilename;
	private Scanner txtScan;

	public wyoro_Animator(String txtfile, String picfile, int posx, int posy)
			throws java.io.IOException {
		txtfilename = txtfile;
		x = posx;
		y = posy;
		Actor = EZ.addImage(picfile, posx, posy);

	}

	public void setControl(int posX, int posY, float scaleSize,
			float rotationDeg, long dur) {
		destx = posX;
		desty = posY;
		scalefinal = scaleSize;
		rotationfinal = rotationDeg;
		duration = dur * 500;
		starttime = System.currentTimeMillis();
		startx = x;
		starty = y;
		startscale = Actor.getScale();
		startRotation = Actor.getRotation();
		controlState = true;
	}

	public void control() {
		if (controlState == true) {
			float normTime = (float) (System.currentTimeMillis() - starttime)
					/ (float) duration;

			x = (int) (startx + ((float) (destx - startx) * normTime));
			y = (int) (starty + ((float) (desty - starty) * normTime));

			scaling = (float) (startscale + ((float) (scalefinal - startscale) * normTime));

			rotate = (float) (startRotation + ((float) (rotationfinal - startRotation) * normTime));

			if (System.currentTimeMillis() - starttime >= duration) {
				controlState = false;
				x = destx;
				y = desty;
				scaling = scalefinal;
				rotate = rotationfinal;
			}
			Actor.translateTo(x, y);
			Actor.scaleTo(scaling);
			Actor.rotateTo(rotate);

		}
	}

	public boolean iscontrolling() {
		return controlState;
	}

	public void read() throws IOException {
		String command;
		command = this.txtScan.next();
		int x, y;
		float scaleSize, rotateDeg;
		long dur;
		switch (command) {
		case "CONTROL":
			x = this.txtScan.nextInt();
			y = this.txtScan.nextInt();
			scaleSize = this.txtScan.nextFloat();
			rotateDeg = this.txtScan.nextFloat();
			dur = this.txtScan.nextLong();
			setControl(x, y, scaleSize, rotateDeg, dur);
			System.out.println("Move X: " + x + " Move Y: " + y
					+ " ScaleSize: " + scaleSize + " RotateDeg: " + rotateDeg
					+ " Duration: " + dur);
			break;
		default:
			System.out.print("Broken");
			break;
		}
	}

	public void stillReading() {
		if (this.txtScan.hasNextLine()) {
			startRead();
		}
	}

	public void setReading(boolean read) {
		reading = read;
	}

	public void stopRead() {
		setReading(false);
	}

	public void startRead() {
		setReading(true);
	}

	public boolean isReading() {
		return reading;
	}

	public void act() {
		if (iscontrolling()) {
			control();
		}

		else {
			stillReading();
		}
	}

	public boolean init() {
		try {
			txtScan = new Scanner(new FileReader(txtfilename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		initialstate = false;
		return initialstate;
	}

	public void go() {
		if (initialstate == true) {
			init();
		}
		if (isReading() == true) {
			try {
				read();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		stopRead();

		if (isReading() == false) {
			act();
		}

	}

}
