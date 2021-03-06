package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class USLocalizer implements ILocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	private final static int ROTATION_SPEED = 175;
	private final static int UPPER_NOISE_BOUND = 60;
	private final static int LOWER_NOISE_BOUND = 45;
	
	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private Navigation navigation;

	
	public USLocalizer(Odometer odo, Navigation navigation, SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.navigation = navigation;
	}
	
	@Override
	public void doLocalization() {
		
		double angleA, angleB;
		double deltaTheta = 0.0;
		
		float[] someData = new float[10];
		
		for (int i = 0; i < someData.length; i++) {
			someData[i] = getFilteredData();
			Delay.msDelay(200);
		}
		
		double averageDistance = average(someData);
		
		boolean robotIsFacingWall = averageDistance < ((UPPER_NOISE_BOUND + LOWER_NOISE_BOUND)/2.0);
		
		Delay.msDelay(1000);
		
		navigation.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			// rotate the robot until it sees no wall			
			while (robotIsFacingWall) {
				// Robot will keep rotating until it no longer faces the wall
				if (getFilteredData() > UPPER_NOISE_BOUND) {
					robotIsFacingWall = false;
				}
			}
			
			// No wall
			Sound.beep();
			
			boolean wasWithinMargin = false;
			double angleOne = 0.0;
			double angleTwo = 0.0;
			
			// keep rotating until the robot sees a wall, then latch the angle
			while (!robotIsFacingWall) {
				float distance = getFilteredData();
				if (wasWithinMargin && distance < LOWER_NOISE_BOUND) {
					angleTwo = odo.getAng();
					robotIsFacingWall = true;
				} else if (distance < UPPER_NOISE_BOUND) {
					wasWithinMargin = true;
					angleOne = odo.getAng();
				}
			}
			
			// Wall
			Sound.twoBeeps();
			
			angleA = (angleOne + angleTwo)/2.0;
			
			// switch direction and wait until it sees no wall
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
			
			Delay.msDelay(1000);
			
			while (robotIsFacingWall) {
				// Robot will keep rotating until it no longer faces the wall
				if (getFilteredData() > UPPER_NOISE_BOUND) {
					robotIsFacingWall = false;
				}
			}
			
			Sound.beep();
			
			wasWithinMargin = false;
			
			// keep rotating until the robot sees a wall, then latch the angle
			while (!robotIsFacingWall) {
				float distance = getFilteredData();
				if (wasWithinMargin && distance < LOWER_NOISE_BOUND) {
					angleTwo = odo.getAng();
					robotIsFacingWall = true;
				} else if (distance < UPPER_NOISE_BOUND) {
					wasWithinMargin = true;
					angleOne = odo.getAng();
				}
			}
			Sound.twoBeeps();
			navigation.setSpeeds(0, 0);
			angleB = (angleOne + angleTwo)/2.0;
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			if (angleA < angleB) {
				deltaTheta = 45.0 - ((angleA + angleB) / 2.0);
			} else {
				deltaTheta = 225.0 - ((angleA + angleB) / 2.0);
			}
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
						
			while (!robotIsFacingWall) {
				if (getFilteredData() < LOWER_NOISE_BOUND) {
					robotIsFacingWall = true;
					Sound.twoBeeps();
					Delay.msDelay(1000);
				}
			}
			
			double angleOne = 0.0;
			double angleTwo = 0.0;
			boolean wasWithinMargin = false;
			
			while (robotIsFacingWall) {
				float distance = getFilteredData();
				if (wasWithinMargin && distance > UPPER_NOISE_BOUND) {
					angleTwo = odo.getAng();
					robotIsFacingWall = false;
					Sound.beep();
				} else if (distance > LOWER_NOISE_BOUND) {
					wasWithinMargin = true;
					angleOne = odo.getAng();
				}
			}
			
			angleA = (angleOne + angleTwo)/2.0;
			navigation.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
			
			Delay.msDelay(1000);
			
			while (!robotIsFacingWall) {
				if (getFilteredData() < LOWER_NOISE_BOUND) {
					robotIsFacingWall = true;
					Sound.twoBeeps();
				}
			}
			
			wasWithinMargin = false;
			
			while (robotIsFacingWall) {
				float distance = getFilteredData();
				if (wasWithinMargin && distance > UPPER_NOISE_BOUND) {
					angleTwo = odo.getAng();
					robotIsFacingWall = false;
					Sound.beep();
				} else if (distance > LOWER_NOISE_BOUND) {
					wasWithinMargin = true;
					angleOne = odo.getAng();
				}
			}
			
			angleB = (angleOne + angleTwo)/2.0;
			navigation.setSpeeds(0, 0);
			
			if (angleA < angleB) {
				deltaTheta = 220.0 - ((angleA + angleB) / 2.0);
//				Sound.twoBeeps();
			} else {
				deltaTheta = 40.0 - ((angleA + angleB) / 2.0);
			}
		}
		odo.setPosition(new double[] {0, 0, Odometer.fixDegAngle(odo.getAng() + deltaTheta)}, new boolean[] {false, false, true});
	}
	
	private double average(float[] data) {
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
		}
		return sum/data.length;
	}
	
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * (float)100.0;
		return distance;
	}

}
