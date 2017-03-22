package finalproject.utilities;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.utility.Delay;

/**
 * This is the Shooter class which controls the launch arm motors.
 * @author steven
 *
 */
public class Shooter {
	// Modify these to desired performance
	private final int SHOOT_ACCELERATION = 6000;
	private final int SMOOTH_ACCELERATION = 1000;
	private final int SMOOTH_SPEED = 150;
	private final int SHOOT_SPEED = 700;
	private final int ANGLE_FROM_HOR = -20;
	private final int LAUNCH_ANGLE = 90;
	
	private EV3LargeRegulatedMotor leftLaunchMotor, rightLaunchMotor;
	
	/**
	 * This is the public constructor for the Shooter class.
	 * @param leftLaunchMotor Left Launch Motor object.
	 * @param rightLaunchMotor Right Launch Motor object.
	 */
	public Shooter(EV3LargeRegulatedMotor leftLaunchMotor, EV3LargeRegulatedMotor rightLaunchMotor){
		this.leftLaunchMotor = leftLaunchMotor;
		this.rightLaunchMotor = rightLaunchMotor;
	}
	
	/**
	 * Launches the ball.
	 */
	public void shoot(){
		leftLaunchMotor.setAcceleration(SHOOT_ACCELERATION);
		rightLaunchMotor.setAcceleration(SHOOT_ACCELERATION);
		
		leftLaunchMotor.setSpeed(SHOOT_SPEED);
		rightLaunchMotor.setSpeed(SHOOT_SPEED);
		
		leftLaunchMotor.rotate((int)(LAUNCH_ANGLE - ANGLE_FROM_HOR), true);
		rightLaunchMotor.rotate((int)(LAUNCH_ANGLE - ANGLE_FROM_HOR), false);
		
		// Wait for a bit for wobbling to settle down
		Delay.msDelay(1000);
	}
	
	/**
	 * Slowly raises the launch arm to the vertical position to reduce robot size.
	 */
	public void raiseArm() {
		smoothAcceleration();
		rotate((int)(90 - ANGLE_FROM_HOR));
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
		leftLaunchMotor.rotate(angle, true);
		rightLaunchMotor.rotate(angle, false);
		
		leftLaunchMotor.stop(true);
		rightLaunchMotor.stop();
	}
	
	/**
	 * Floats both launch motors.
	 */
	public void floatMotor(){
		leftLaunchMotor.flt();
		rightLaunchMotor.flt();
	}
	
	/**
	 * Slowly lowers the launch arm to a horizontal position.
	 */
	public void lowerArm() {
		smoothAcceleration();
		rotate(-90);
		floatMotor();
		
		// Wait a bit for arm to reset to its natural position
		Delay.msDelay(2000);
	}
	
	/**
	 * Set acceleration and speed to a lower value.
	 */
	public void smoothAcceleration(){
		leftLaunchMotor.setAcceleration(SMOOTH_ACCELERATION);
		rightLaunchMotor.setAcceleration(SMOOTH_ACCELERATION);
		
		leftLaunchMotor.setSpeed(SMOOTH_SPEED);
		rightLaunchMotor.setSpeed(SMOOTH_SPEED);
	}
}
