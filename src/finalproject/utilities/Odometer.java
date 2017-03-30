package finalproject.utilities;

/*
 * File: Odometer.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Class which controls the odometer for the robot
 * 
 * Odometer defines cooridinate system as such...
 * 
 * 					90Deg:pos y-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 180Deg:neg x-axis------------------0Deg:pos x-axis
 * 							|
 * 							|
 * 							|
 * 							|
 * 					270Deg:neg y-axis
 * 
 * The odometer is initalized to 90 degrees, assuming the robot is facing up the positive y-axis
 * 
 */

import lejos.utility.Timer;
import lejos.utility.TimerListener;
import finalproject.testing.SquareDriver;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Odometry class for general odometry.
 * @author maxsn
 *
 */
public class Odometer implements TimerListener {

	private Timer timer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int DEFAULT_TIMEOUT_PERIOD = 20;
	private double leftRadius, rightRadius, width;
	private double x, y, theta;
	private double[] oldDH, dDH;
	
	/**
	 * Public constructor for the Odometer class. Must be instantiated using this.
	 * @param leftMotor
	 * @param rightMotor
	 * @param INTERVAL
	 * @param autostart
	 */
	public Odometer (EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int INTERVAL, boolean autostart) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		// default values, modify for your robot
		this.rightRadius = 2.1;
		this.leftRadius = 2.1;
		this.width = 19.00;
		
		this.x = 0.0;
		this.y = 0.0;
		this.theta = 90.0;
		this.oldDH = new double[2];
		this.dDH = new double[2];

		if (autostart) {
			// if the timeout interval is given as <= 0, default to 20ms timeout 
			this.timer = new Timer((INTERVAL <= 0) ? INTERVAL : DEFAULT_TIMEOUT_PERIOD, this);
			this.timer.start();
		} else
			this.timer = null;
	}
	
	/**
	 * Stops the odometer.
	 */
	public void stop() {
		if (this.timer != null)
			this.timer.stop();
	}
	
	/**
	 * Starts the odometer.
	 */
	public void start() {
		if (this.timer != null)
			this.timer.start();
	}
	
	/**
	 * Calculates displacement and heading as title suggests.
	 * @param data
	 */
	private void getDisplacementAndHeading(double[] data) {
		int leftTacho, rightTacho;
		leftTacho = leftMotor.getTachoCount();
		rightTacho = rightMotor.getTachoCount();

		data[0] = (leftTacho * leftRadius + rightTacho * rightRadius) * Math.PI / 360.0;
		data[1] = (leftTacho * leftRadius - rightTacho * rightRadius) / width;
	}
	
	/**
	 * Recompute the odometer values using the displacement and heading changes.
	 */
	public void timedOut() {
		this.getDisplacementAndHeading(dDH);
		dDH[0] -= oldDH[0];
		dDH[1] -= oldDH[1];

		// update the position in a critical region
		synchronized (this) {
			theta += dDH[1];
			theta = fixDegAngle(theta);

			x += dDH[0] * Math.cos(Math.toRadians(theta));
			y += dDH[0] * Math.sin(Math.toRadians(theta));
		}

		oldDH[0] += dDH[0];
		oldDH[1] += dDH[1];
	}

	/**
	 * Set the X coordinate.
	 * @param x The coordinate.
	 */
	public void setX(double x) {
		synchronized (this) {
			this.x = x;
		}
	}
	
	/**
	 * Get the X coordinate.
	 * @return The X coordinate.
	 */
	public double getX() {
		synchronized (this) {
			return x;
		}
	}

	/**
	 * Set the Y coordinate.
	 * @param y The coordinate.
	 */
	public void setY(double y) {
		synchronized (this) {
			this.y = y;
		}
	}
	
	/**
	 * Get the Y coordinate.
	 * @return The Y coordinate.
	 */
	public double getY() {
		synchronized (this) {
			return y;
		}
	}

	/**
	 * Set the theta angle.
	 * @param theta The angle.
	 */
	public void setAng(double theta) {
		synchronized (this) {
			this.theta = theta;
		}
	}
	
	/**
	 * Return the theta angle.
	 * @return The angle.
	 */
	public double getAng() {
		synchronized (this) {
			return theta;
		}
	}

	/**
	 * Set the position of the Odometer. (All coordinates and angle.)
	 * @param position The position array of doubles.
	 * @param update Array of booleans specifiying which values to update.
	 */
	public void setPosition(double[] position, boolean[] update) {
		synchronized (this) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * Returns all position coordinates.
	 * @param position An initialized array of doubles to hold the values.
	 */
	public void getPosition(double[] position) {
		synchronized (this) {
			position[0] = x;
			position[1] = y;
			position[2] = theta;
		}
	}

	/**
	 * Returns all position coordinates in a new array of doubles.
	 * @return An array of doubles containing all coordinates.
	 */
	public double[] getPosition() {
		synchronized (this) {
			return new double[] { x, y, theta };
		}
	}
	
	/**
	 * Accessor method for both motors.
	 * @return An array containing all motors.
	 */
	public EV3LargeRegulatedMotor [] getMotors() {
		return new EV3LargeRegulatedMotor[] {this.leftMotor, this.rightMotor};
	}
	
	/**
	 * Accessor method for the left motor.
	 * @return The left motor.
	 */
	public EV3LargeRegulatedMotor getLeftMotor() {
		return this.leftMotor;
	}
	
	/**
	 * Accessor method for the right motor.
	 * @return The right motor.
	 */
	public EV3LargeRegulatedMotor getRightMotor() {
		return this.rightMotor;
	}

	/**
	 * Fix a potentially overflowed (over 360, or under 0) angle.
	 * @param angle The angle to be fixed.
	 * @return The fixed angle.
	 */
	public static double fixDegAngle(double angle) {
		if (angle < 0.0)
			angle = 360.0 + (angle % 360.0);

		return angle % 360.0;
	}

	/**
	 * Returns the minimum angle between two angles.
	 * @param a The first angle.
	 * @param b The second angle.
	 * @return The minimum angle between the two.
	 */
	public static double minimumAngleFromTo(double a, double b) {
		double d = fixDegAngle(b - a);

		if (d < 180.0)
			return d;
		else
			return d - 360.0;
	}
	
	/**
	 * Drive the robot in a square for testing purposes.
	 */
	public void driveSquare(){
		SquareDriver.drive(leftMotor, rightMotor, leftRadius, rightRadius, width);
	}
}
