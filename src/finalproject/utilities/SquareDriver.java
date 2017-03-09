/*
 * SquareDriver.java
 */
package finalproject.utilities;

import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class SquareDriver {
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;

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
		
		for (int i = 0; i < 4; i++) {
			// drive forward two tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(leftRadius, 91.44), true);
			rightMotor.rotate(convertDistance(rightRadius, 91.44), false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(leftRadius, width, 90.0), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, 90.0), false);
		}
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}