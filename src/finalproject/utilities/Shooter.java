package finalproject.utilities;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

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
	private static final int STRAIGHT_SHOOTING_SPEED = 4000;
	private static final int STRAIGHT_ACCEL = 8000;
	private static final int ANGLE_FROM_HOR = 20;
	private static final int SMOOTH_ACCELERATION = 2000;
	
	//Angle shooting arm rotates through to shoot
	private static final int SHOOTING_ANGLE = - 140;
	
	private EV3LargeRegulatedMotor shooterMotorL,shooterMotorR;
		
	/**
	 * This is the initializer. It gets the wheel motors from the odometer. The shooter motors should be passed when the
	 * initializer is called.
	 */
	public Shooter(EV3LargeRegulatedMotor shooterMotorR, EV3LargeRegulatedMotor shooterMotorL) {
		this.shooterMotorR = shooterMotorR;
		this.shooterMotorL = shooterMotorL;
	}

	/**
	 * This can be called when the robot is in a position to shoot.
	 */
	public void shoot() {
		Delay.msDelay(500);
		//Sets motor speeds and accelerations
		shooterMotorL.setSpeed(STRAIGHT_SHOOTING_SPEED);
		shooterMotorL.setAcceleration(STRAIGHT_ACCEL);
		shooterMotorR.setSpeed(STRAIGHT_SHOOTING_SPEED);
		shooterMotorR.setAcceleration(STRAIGHT_ACCEL);

		//Possibly:
		//aim();
		
		//Now shoot
		shooterMotorL.rotate(SHOOTING_ANGLE, true);
		shooterMotorR.rotate(SHOOTING_ANGLE, false);
		
		//Return to resting position
		shooterMotorL.setSpeed(ROTATION_SPEED);
		shooterMotorR.setSpeed(ROTATION_SPEED);
		shooterMotorL.rotate(-SHOOTING_ANGLE, true);
		shooterMotorR.rotate(-SHOOTING_ANGLE, false);
	}
	
	/**
	 * Slowly raises the launch arm to the vertical position to reduce robot size.
	 */
	public void raiseArm() {
		smoothAcceleration();
		rotate((int)(135 - ANGLE_FROM_HOR));
	}
	
	/**
	 * Slowly raises the launch to an angle above the horizon to the robot to move 
	 * with the ball.
	 */
	public void raiseArmToMove(){
		smoothAcceleration();
		rotate((int) (60 - ANGLE_FROM_HOR));
	}
	
	/**
	 * Rotate the motor by a specified angle.
	 * @param angle Rotation angle in degrees.
	 */
	public void rotate(int angle){
		shooterMotorL.rotate(-angle, true);
		shooterMotorR.rotate(-angle, false);
		
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
	 * Slowly lowers the launch arm to a horizontal position.
	 */
	public void lowerArm() {
		smoothAcceleration();
		rotate(-125);
		floatMotor();
		
		// Wait a bit for arm to reset to its natural position
		Delay.msDelay(2000);
	}
	
	/**
	 * Set acceleration and speed to a lower value.
	 */
	public void smoothAcceleration(){
		shooterMotorL.setAcceleration(SMOOTH_ACCELERATION);
		shooterMotorR.setAcceleration(SMOOTH_ACCELERATION);
		
		shooterMotorL.setSpeed(ROTATION_SPEED);
		shooterMotorR.setSpeed(ROTATION_SPEED);
	}
	
}