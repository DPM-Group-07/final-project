package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import lejos.robotics.SampleProvider;

public class LightLocalizer implements ILocalizer {
	private static final double COLOR_SENSOR_RADIUS = 14;
	private static final int ROTATION_SPEED = 175;
	private static final double COLOR_SENSOR_BOUND = 0.38;
	private static final double START_TURN_TO = 45.0;
	private static final double START_GO_FORWARD = 7.5;
	private static final double X_CORRECTION = 0.0;
	private static final double Y_CORRECTION = 2.0;
	
	private Odometer odo;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigation navigation;
	
	public LightLocalizer(Odometer odo, Navigation navigation, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.navigation = navigation;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
	}
	
	@Override
	public void doLocalization() {
		// drive to location listed in tutorial
		navigation.turnTo(START_TURN_TO, true);
		navigation.goForward(START_GO_FORWARD);
		
		double finalX = 0.0;
		double finalY = 0.0;

		// start rotating and clock all 4 gridlines
		navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		int clockedLines = 0;
		
		boolean sensorAboveLine = false;
		
		double angleOne = 0.0;
		double angleTwo = 0.0;
		
		double[] angles = new double[4];
		
		while (clockedLines < 4) {
			// Get the color reading
			colorSensor.fetchSample(this.colorData, 0);

			if (this.colorData[0] < COLOR_SENSOR_BOUND && !sensorAboveLine) {
				sensorAboveLine = true;
				angleOne = odo.getAng();
			}
			
			else if (this.colorData[0] > COLOR_SENSOR_BOUND && sensorAboveLine) {
				angleTwo = odo.getAng();
				angles[clockedLines++] = (angleOne + angleTwo)/2.0;
				sensorAboveLine = false;
			}
		}
		
		navigation.stop();
		
		// do trig to compute (0,0) and 0 degrees
		// X
		double thetaX = Math.abs(angles[0] - angles[2]);

		finalX = -COLOR_SENSOR_RADIUS * Math.cos(Math.toRadians(thetaX / 2.0));
		
		// Y
		double thetaY = Math.abs(angles[1] - angles[3]);
		
		finalY = -COLOR_SENSOR_RADIUS * Math.cos(Math.toRadians(thetaY / 2.0));
		
		odo.setPosition(new double[] {finalX + X_CORRECTION, finalY + Y_CORRECTION, 0.0}, 
				new boolean[] {true, true, false});
		// when done travel to (0,0) and turn to 0 degrees
		navigation.travelTo(0.0, 0.0);
		navigation.turnTo(0.0, true);

//		
//		// odometry correction based on values collected
//		odo.setPosition(new double[] {0.0, -3.5, 5.0}, new boolean[] {true, true, true});
//		navigation.travelTo(0.0, 0.0);
//		navigation.turnTo(0.0, true);

		
		odo.setPosition(new double[] {0.0, 0.0, 0.0}, new boolean[] {true, true, false});
	}
}
