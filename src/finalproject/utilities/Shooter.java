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
	
	//Angle shooting arm rotates through to shoot
	private static final int SHOOTING_ANGLE = 120;
	//Angle at which the dispenser is located from the horizon
	private static final int DISPENSER_ANGLE = -30;
	//Angle to raise to vertical from resting position
	private static final int RAISE_ANGLE = 160;
	//Angle to lower the arm to resting position
	private static final int REST_ANGLE = -70;
	
	private EV3LargeRegulatedMotor shooterMotorL, shooterMotorR;
	
	private boolean armRaised, hasBall;
	/**
	 * This is the initializer. It gets the wheel motors from the odometer. The shooter motors should be passed when the
	 * initializer is called. The arm should be raised and the scoop should be clear of objects.
	 */
	public Shooter(EV3LargeRegulatedMotor shooterMotorL, EV3LargeRegulatedMotor shooterMotorR) {
		this.armRaised = true;
		this.hasBall = false;
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
		this.armRaised = armRaised;
		this.hasBall = false;
		this.shooterMotorL = shooterMotorL;
		this.shooterMotorR = shooterMotorR;
	}
	
	/**
	 * This can be called when the robot is in a position to shoot.
	 */
	public void shoot() {
		// Sets motor speeds and accelerations to shooting mode
		setShootMode();
		
		// Make adjustment if the robot has a ball
		// Compensated for ForwardGameRole
		if(hasBall){
			rotate(REST_ANGLE);
		}

		// Now shoot from resting position
		rotate(SHOOTING_ANGLE);
				
		// Sets motor speeds and accelerations to adjustment mode
		setAdjustMode();
		
		// Rotate the arm back to resting position
		rotate(-SHOOTING_ANGLE);
		
		hasBall = false;
		armRaised = false;
	}
	
	/**
	 * Slowly raises the launch arm to the vertical position to reduce robot size.
	 * Sets armRaised flag.
	 */
	public void raiseArm() {
		if(!armRaised){
			setAdjustMode();
			rotate(RAISE_ANGLE);
			armRaised = true;
		}
	}
	
	/**
	 * Slowly raises the launch to an angle above the horizon to the robot to move 
	 * with the ball. Removes armRaised flag.
	 */
	public void lowerArm(){
		if(armRaised){
			setAdjustMode();
			rotate(-RAISE_ANGLE);
			armRaised = false;
		}
	}
	
	/**
	 * Lowers the arm to collect the ball. Sets hasBall flag to true.
	 */
	public void collect(){
		setAdjustMode();
		if(armRaised){
			rotate(-RAISE_ANGLE + (90 + DISPENSER_ANGLE));
			armRaised = false;
		}
		else{
			rotate(RAISE_ANGLE - (90 - DISPENSER_ANGLE));
		}
		hasBall = true;
	}
	
	/**
	 * Raises the arm to move with the ball. Only call after lowerArmToCollect has been called.
	 * This method roughly raises the launch arm parallel to the horizon.
	 */
	public void raiseArmWithBall(){
		rotate(-DISPENSER_ANGLE);
	}
	
	/**
	 * Returns true if the robot believes it has a ball.
	 * @return Boolean determining whether or not the robot has a ball.
	 */
	public boolean getBallState(){
		return this.hasBall;
	}
	
	/**
	 * Returns true if the arm is raised.
	 * @return Boolean determining whether or not the launching arm is raised.
	 */
	public boolean getArmState(){
		return this.armRaised;
	}
	
	/**
	 * Rotate the motor by a specified angle and lock the motors to the new angle.
	 * @param angle Rotation angle in degrees.
	 */
	public void rotate(int angle){
		shooterMotorL.rotate(-angle, true);
		shooterMotorR.rotate(-angle, false);
		
		shooterMotorL.stop(true);
		shooterMotorR.stop();
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
}