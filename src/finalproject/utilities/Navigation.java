package finalproject.utilities;

import lejos.hardware.lcd.LCD;
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
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

/**
 * This is the navigation class which drives the robot from one position on the field
 * to another.
 * @author maxsn
 *
 */
public class Navigation {
	// Modify these to change the performance of the EV3
	private static final int MOTOR_STRAIGHT = 240;
	private static final int MOTOR_ROTATE = 175;
	private static final int MOTOR_ACCELERATION = 1250;
	private static final double DEG_ERR = 3.0;
	private final double LEFTWHEEL_RADIUS;
	private final double RIGHTWHEEL_RADIUS;
	private final double BASE;
	
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3UltrasonicSensor midUS, rightUS;
		
	// Flags for navigation
	private boolean avoiding;
	private boolean enableAvoid;
	
	// Stores current target
	private double x, y;

	// Modify these to change obstacle avoidance performance
	private double FORWARD_DISTANCE_LENGTH = 40.0;
	private double FORWARD_DISTANCE_THICKNESS = 40.0;
	private double DETECTION_DISTANCE = 15.0;

	/**
	 * The public constructor for this class.
	 * @param odometer The active Odometer object currently tracking the robot's position.
	 * @param midUS Reference to the middle US sensor.
	 * @param rightUS Reference to the right US sensor.
	 */
	public Navigation(Odometer odometer, EV3UltrasonicSensor midUS, EV3UltrasonicSensor rightUS){
		this.enableAvoid = false;
		this.avoiding = false;
		this.odometer = odometer;
		this.midUS = midUS;
		this.rightUS = rightUS;
		
		this.LEFTWHEEL_RADIUS = odometer.getLeftRadius();
		this.RIGHTWHEEL_RADIUS = odometer.getRightRadius();
		this.BASE = odometer.getTrack();
		
		this.leftMotor = odometer.getLeftMotor();
		this.leftMotor.setAcceleration(MOTOR_ACCELERATION);
		this.rightMotor = odometer.getRightMotor();
		this.rightMotor.setAcceleration(MOTOR_ACCELERATION);
	}
	
	/**
	 * Set the speeds of both motor to an integer value.
	 * @param lSpd The desired left motor speed.
	 * @param rSpd The desired right motor speed.
	 */
	public void setSpeeds(int lSpd, int rSpd){
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
	 * Stops both motors.
	 */
	public void stop(){
		this.leftMotor.stop(true);
		this.rightMotor.stop(false);
	}
	
	/**
	 * Travels to a specified coordinate that's related to the robot's starting position.
	 * @param x coordinate x in cm
	 * @param y coordinate y in cm
	 */
	public void travelTo(double x, double y){
		// Set navigating flag to true and store current target coordinate
		this.x = x;
		this.y = y;
		
		// Computes the difference in X and in Y from current position
		// and programmed location
		double xOdo = odometer.getX();
		double yOdo = odometer.getY();
		double xDelta = x - xOdo;
		double yDelta = y - yOdo;
				
		// Computes distance needed
		double distance = Math.sqrt(xDelta * xDelta + yDelta * yDelta);
				
		// Computes the heading from 0 degrees
		double thetaDesired = Math.atan2(yDelta, xDelta) * 180 / Math.PI;
				
		// Rotate the robot to the desired angle
		turnTo(thetaDesired, true);
		
		// Drive straight by a calculated magnitude
		goForward(distance);			
			
		// Reset the avoiding flag and call the method itself to calculate the 
		// new trajectory to path
		if(avoiding){
			avoiding = false;
			travelTo(this.x, this.y);
		}
		
		Delay.msDelay(500);
	}
	
	/**
	 * 
	 * @param theta angle from 0 to 359.999... in degrees
	 */
	/**
	 * Turns to a specified angle using the minimum angle.
	 * @param angle Angle in [0,360) in degress.
	 * @param stop Stop motors after turning or not.
	 */
	public void turnTo(double angle, boolean stop){
		double error = getDifference(angle, this.odometer.getAng());
		while (Math.abs(error) > DEG_ERR) {

			error =  getDifference(angle, this.odometer.getAng());
			
			if (error > 0) {
				this.setSpeeds(MOTOR_ROTATE, -MOTOR_ROTATE);
			} else {
				this.setSpeeds(-MOTOR_ROTATE, MOTOR_ROTATE);
			}
		}
		if(stop) this.stop();
	}
	
	/**
	 * Turns the robot by a specified angle
	 * @param theta angle in degrees
	 */
	public void turn(double theta){
		leftMotor.setSpeed(MOTOR_ROTATE);
		rightMotor.setSpeed(MOTOR_ROTATE);
		
		leftMotor.rotate(-convertAngle(LEFTWHEEL_RADIUS, BASE, theta), true);
		rightMotor.rotate(convertAngle(RIGHTWHEEL_RADIUS, BASE, theta), false);

	}
	
	/**
	 * Drive forward by the input distance
	 * @param distance in centimeters
	 */
	public void goForward(double distance){
		leftMotor.setSpeed(MOTOR_STRAIGHT);
		rightMotor.setSpeed(MOTOR_STRAIGHT);
		
		// Checks if the enableAvoid flag has been set. If it has, unblock both motors. If not,
		// unblock the left motor.
		leftMotor.rotate(convertDistance(LEFTWHEEL_RADIUS, distance), true);
		if(enableAvoid)	rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS, distance), true);
		else rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS, distance), false);
				
		// If enableAvoid is true, call avoidWall repeatedly
		if(enableAvoid){
			while(leftMotor.isMoving() || rightMotor.isMoving()){
				avoidWall();
				// If the avoid flag has been set, leave method.
				if(avoiding) return;
			}
		}
	}
	
	/**
	 * Drive backward by the input distance
	 * @param distance in centimeters
	 */
	public void goBackward(double distance){
		leftMotor.setSpeed(-MOTOR_STRAIGHT);
		rightMotor.setSpeed(-MOTOR_STRAIGHT);
		
		leftMotor.rotate(convertDistance(LEFTWHEEL_RADIUS, distance), true);
		rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS, distance), false);
	}
	
	/**
	 * Avoids the wall.
	 */
	public void avoidWall(){
		boolean rightClear = false;
		
		// Obtain data from ultrasonic sensor
		SampleProvider provider = midUS.getDistanceMode();
		float[] sample = new float[3];
		provider.fetchSample(sample, 0);
		
		SampleProvider provider2 = rightUS.getDistanceMode();
		float[] sample2 = new float[3];
		provider2.fetchSample(sample2, 0);
		
		if(sample2[0] * 100 > 60){
			rightClear = true;
		}
		
		// Check if collision is imminent		
		while(sample[0] * 100.0 < DETECTION_DISTANCE){
			// Set flag to force the EV3 to recalculate the heading after the avoiding maneuvers
			// have been completed. 
			this.avoiding = true;
			
			boolean avoidComplete = false;
			int forwardCount = 0;
			
			// Turn right by 90 degrees and advance by FORWARD_DISTANCE_LENGTH
			// Turn left by 90 degrees and check if there is an obstacle
			// If not, exit loop
			// If so, repeat maneuver until there isn't an obstacle
			while(!avoidComplete){
				turn(-90.0);
				leftMotor.rotate(convertDistance(LEFTWHEEL_RADIUS, FORWARD_DISTANCE_LENGTH), true);
				rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS, FORWARD_DISTANCE_LENGTH), false);
				turn(90.0);
				forwardCount++;
				
				provider.fetchSample(sample, 0);
				LCD.drawString("" + (sample[0] * 100), 0, 5);
				
				if(sample[0] * 100.0 > 2 * DETECTION_DISTANCE){
					break;
				}
			}
			
			// Advance by FORWARD_DISTANCE_THICKNESS and turn left by 90 degrees
			// Check if there is an obstacle
			// If not, exit loop
			// If so, turn right by 90 degrees and repeat maneuver until there isn't an obstacle
			while(!avoidComplete){
				leftMotor.rotate(convertDistance(LEFTWHEEL_RADIUS, FORWARD_DISTANCE_THICKNESS), true);
				rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS,FORWARD_DISTANCE_THICKNESS), false);
				turn(90.0);
				
				provider.fetchSample(sample, 0);
				
				LCD.drawString("" + (sample[0] * 100), 0, 5);
				
				if(sample[0] * 100.0 > 2 * DETECTION_DISTANCE){
					break;
				}
				
				turn(-90.0);
			}
			
			// Undo advancements done during the first turn and reset avoidComplete flag
			leftMotor.rotate(convertDistance(LEFTWHEEL_RADIUS, forwardCount * FORWARD_DISTANCE_LENGTH), true);
			rightMotor.rotate(convertDistance(RIGHTWHEEL_RADIUS, forwardCount * FORWARD_DISTANCE_LENGTH), false);
			avoidComplete = true;
			
			// Fetch a new sample to potentially exit the loop
			// If a new obstacle has been detected, do the above maneuvers
			provider.fetchSample(sample, 0);
		}		
		Delay.msDelay(100);
	}
	
	/**
	 * Used to enable avoidance system
	 * @param enableAvoid true to enable avoidance system
	 */
	public void enableAvoid(boolean enableAvoid){
		this.enableAvoid = enableAvoid;
		if(enableAvoid){
			if(!this.midUS.isEnabled()) this.midUS.enable();
			if(!this.rightUS.isEnabled()) this.rightUS.enable();
		}
		else{
			this.midUS.disable();
			this.rightUS.disable();
		}
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
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
