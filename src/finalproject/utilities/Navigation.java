package finalproject.utilities;

/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This is the navigation class which drives the robot from one position on the field
 * to another.
 * @author maxsn
 *
 */
public class Navigation {
	final static int FAST = 240, SLOW = 175, ACCELERATION = 1250;
	final static double DEG_ERR = 4.5, CM_ERR = 1.5;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	/**
	 * The public constructor for this class
	 * @param odo The active Odometer object currently tracking the robot's position.
	 */
	public Navigation(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
		this.leftMotor.setAcceleration(ACCELERATION);
		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/**
	 * Set the speeds of both motors to a float value.
	 * @param lSpd The desired left motor speed.
	 * @param rSpd The desired right motor speed.
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Set the speeds of both motor to an integer value.
	 * @param lSpd The desired left motor speed.
	 * @param rSpd The desired right motor speed.
	 */
	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/**
	 * Float both motors so that the wheels can be freely moved by a human.
	 */
	public void setFloat() {
		this.leftMotor.stop(true);
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/**
	 * Move the robot to a specified position on the field while constantly updating its heading.
	 * @param x The desired x-coordinate of the new position.
	 * @param y The desired y-coordinate of the new position.
	 */
	public void travelTo(double x, double y) {
		double minAng;
		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
			minAng = (Math.atan2(y - odometer.getY(), x - odometer.getX())) * (180.0 / Math.PI);
			if (minAng < 0)
				minAng += 360.0;
			this.turnTo(minAng, false);
			this.setSpeeds(FAST, FAST);
		}
		stop();
	}

	/**
	 * Turn to a specified angle theta using the North-South-West-East logic. Angle zero is
	 * North, 90 is East, 180 South and 270 West.
	 * @param angle The desired final heading of the robot.
	 * @param stop Whether to stop the motors at the end of the turn or not.
	 */
	public void turnTo(double angle, boolean stop) {

		double error = getDifference(angle, this.odometer.getAng());

		while (Math.abs(error) > DEG_ERR) {

			error =  getDifference(angle, this.odometer.getAng());
			
			if (error > 0) {
				this.setSpeeds(SLOW, -SLOW);
			} else {
				this.setSpeeds(-SLOW, SLOW);
			}
		}

		if (stop) {
			stop();
		}
	}
	
	/**
	 * Stops both motors.
	 */
	public void stop(){
		this.leftMotor.stop(true);
		this.rightMotor.stop();
	}
	
	/**
	 * Travel a specified distance forward.
	 * @param distance The distance to travel.
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);
	}

	/**
	 * Computes the minimal angle between two angles in the range [0,360).
	 * @param a1 The first angle.
	 * @param a2 The second angle.
	 * @return The minimal angle.
	 */
	private double getDifference(double a1, double a2) {
		double rotationAngle = a1 - a2;
		if (rotationAngle > 180)
			rotationAngle -= 360;
		else if (rotationAngle < -180)
			rotationAngle += 360;
		return rotationAngle;
	}
}
