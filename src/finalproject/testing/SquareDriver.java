package finalproject.testing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class is provided for testing purposes only. Drives the robot in a square.
 * @author maxsn
 *
 */
public class SquareDriver {
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;
	private static final int LAP_NUMBER = 2;
	private static final double DISTANCE = 60.96;
	
	/**
	 * Test drives the robot in a square 4 times to see the calculate error. Obtained from lab 2. 
	 * Modify parameters in Odometer.java to desired performance.
	 * @param leftMotor 
	 * @param rightMotor 
	 * @param leftRadius radius of left wheel in cm
	 * @param rightRadius radius of right wheel in cm
	 * @param width width of chassis in cm
	 */
	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		
		for (int i = 0; i < LAP_NUMBER * 4; i++) {
			// drive forward two tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(leftRadius, DISTANCE), true);
			rightMotor.rotate(convertDistance(rightRadius, DISTANCE), false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(leftRadius, width, 90.0), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, 90.0), false);
		}
	}
	
	/**
	 * Converts a distance for straight travel.
	 * @param radius Wheel radius.
	 * @param distance Travel distance.
	 * @return
	 */
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	/**
	 * Converts an angle for turning.
	 * @param radius Wheel radius.
	 * @param width Wheel track.
	 * @param angle Turn angle.
	 * @return
	 */
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}