package finalproject.utilities;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * Shooter class uses odometry to turn robot to selected target. Then shoots a ball at the target. Currently uses a fixed angle but 
 * later on if 1.RObot position is known and 2. Target position is known, the angle to turn to could be calculated. 
 * 
 * Tuesday February 14, 2017
 * 9:45am
 * 
 * @author thomaschristinck
 * @author alexmasciotra
 */
public class Shooter {
	//Relevant speeds; note when shooting straight ahead launch speed won't need to be as fast as the "skew speed"
	private static final int ROTATION_SPEED = 80;
	private static final int SMOOTH_ACCELERATION = 8000;
	private static final int STRAIGHT_SHOOTING_SPEED = 12000;
	private static final int STRAIGHT_ACCEL = 15000;
	
	private static final int ARM_RAISED_ANGLE = -125;
	private static final int ARM_COLLECT_ANGLE = -25;
	private static final int ARM_RAISED_WITH_BALL_ANGLE = -60;
	private static final int ARM_SHOT_ANGLE = -140;
	
	private EV3LargeRegulatedMotor shooterMotorL, shooterMotorR;
	
	/**
	 * This is the initializer. It gets the wheel motors from the odometer. The shooter motors should be passed when the
	 * initializer is called. The arm should be raised and the scoop should be clear of objects.
	 */
	public Shooter(EV3LargeRegulatedMotor shooterMotorL, EV3LargeRegulatedMotor shooterMotorR) {
		this.shooterMotorR = shooterMotorR;
		this.shooterMotorL = shooterMotorL;
	}

	/**
	 * Constructor for Shooter class.
	 * @param shooterMotorL Left shooter motor object.
	 * @param shooterMotorR Right shooter motor object.
	 * @param armRaised True if the launch arms are raised, i.e. 90 degrees to the horizon.
	 */
	public Shooter(EV3LargeRegulatedMotor shooterMotorL, EV3LargeRegulatedMotor shooterMotorR, boolean armRaised){
		this.shooterMotorL = shooterMotorL;
		this.shooterMotorR = shooterMotorR;
	}
	
	/**
	 * This can be called when the robot is in a position to shoot.
	 */
	public void shoot() {
		// Sets motor speeds and accelerations to shooting mode
		setShootMode();
		
		// Now shoot from resting position
		rotateTo(ARM_SHOT_ANGLE);
				
		// Sets motor speeds and accelerations to adjustment mode
		setAdjustMode();
		
		// Rotate the arm back to resting position
		lowerArm();
	}
	
	/**
	 * Slowly raises the launch arm to the vertical position to reduce robot size.
	 * Sets armRaised flag.
	 */
	public void raiseArm() {
		rotateTo(ARM_RAISED_ANGLE);
	}
	
	/**
	 * Slowly raises the launch to an angle above the horizon to the robot to move 
	 * with the ball. Removes armRaised flag.
	 */
	public void lowerArm(){
		rotateTo(-5);
	}
	
	/**
	 * Lowers the arm to collect the ball. Sets hasBall flag to true.
	 */
	public void collect(){
		rotateTo(ARM_COLLECT_ANGLE);		
	}
	
	/**
	 * Raises the arm to move with the ball. Only call after lowerArmToCollect has been called.
	 * This method roughly raises the launch arm parallel to the horizon.
	 */
	public void raiseArmWithBall(){
		rotateTo(ARM_RAISED_WITH_BALL_ANGLE);
	}
	
	/**
	 * Stops both motors.
	 */
	public void stop(){
		shooterMotorL.stop(true);
		shooterMotorR.stop();
	}
	
	/**
	 * Floats both launch motors.
	 */
	public void floatMotor(){
		shooterMotorL.flt();
		shooterMotorR.flt();
	}
	
	/**
	 * Set acceleration and speed to smoothly move the launch arm up and down.
	 */
	public void setAdjustMode(){
		shooterMotorL.setAcceleration(SMOOTH_ACCELERATION);
		shooterMotorR.setAcceleration(SMOOTH_ACCELERATION);
		
		shooterMotorL.setSpeed(ROTATION_SPEED);
		shooterMotorR.setSpeed(ROTATION_SPEED);
	}
	
	/**
	 * Set acceleration and speed to shooting mode.
	 */
	public void setShootMode(){
		shooterMotorL.setAcceleration(STRAIGHT_ACCEL);
		shooterMotorR.setAcceleration(STRAIGHT_ACCEL);
		
		shooterMotorL.setSpeed(STRAIGHT_SHOOTING_SPEED);
		shooterMotorR.setSpeed(STRAIGHT_SHOOTING_SPEED);
	}
	
	/**
	 * Rotate arm to an angle.
	 */
	public void rotateTo(int angle) {
		shooterMotorL.rotateTo(angle, true);
		shooterMotorR.rotateTo(angle, false);
		
		shooterMotorL.stop(true);
		shooterMotorR.stop();
	}
}