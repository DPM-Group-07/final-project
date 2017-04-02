package finalproject.utilities.obstacleavoidance;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class ObstacleAvoidance extends Thread {
	
	private final static int OBJECT_DISTANCE = 15;
	final static int WF_BANDCENTER = 20;			// Offset from the wall (cm)
	final static int WF_BANDWIDTH = 5;
	final static int WF_MOTOR_VERYLOW = -200;
	final static int WF_MOTOR_LOW = 0;			// Speed of slower rotating wheel (deg/sec)
	final static int WF_MOTOR_HIGH = 200;
	final static int WF_FINAL_ANGLE_THRESHOLD = 20;
	
	private EV3UltrasonicSensor usSensor;
	private EV3UltrasonicSensor leftSensor;
	private EV3UltrasonicSensor rightSensor;
	private Navigation navigation;
	private Odometer odometer;
	private ObstaclePoller op;
	
	private boolean isRunning;
	
	public ObstacleAvoidance(EV3UltrasonicSensor usSensor, EV3UltrasonicSensor leftSensor, EV3UltrasonicSensor rightSensor, Navigation navigation, Odometer odometer) {
		this.usSensor = usSensor;
		this.leftSensor = leftSensor;
		this.rightSensor = rightSensor;
		this.isRunning = false;
		this.navigation = navigation;
		this.odometer = odometer;
		
		this.op = new ObstaclePoller(usSensor);
	}
	
	/**
	 * This is the main method of this class.
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (true) {
			if (isRunning) {
				int distance = op.getDistance();
				if (distance < OBJECT_DISTANCE && !navigation.getObstacleDetected()) {
					Sound.beep();
					navigation.setObstacleDetected(true);
					op.pause();
					wallFollow();
					op.unpause();
				} else if (distance > OBJECT_DISTANCE && navigation.getObstacleDetected()) {
					navigation.setObstacleDetected(false);
					Sound.twoBeeps();
					Object lock = navigation.getLock();
					synchronized(lock) {
						navigation.getLock().notify();
					}
				}
			}
		}
	}
	
	public void disable() {
		this.isRunning = false;
	}
	
	public void enable() {
		this.isRunning = true;
	}
	
	private void wallFollow() {
		ObstaclePoller opWall = new ObstaclePoller(leftSensor);
		
		navigation.turnTo(odometer.getAng() + 90.0, true);
				
		// Start following the wall
		double finalAngle = odometer.getAng() - 180.0;
		
		EV3LargeRegulatedMotor leftMotor = odometer.getLeftMotor();
		EV3LargeRegulatedMotor rightMotor = odometer.getRightMotor();
				
		// Follow the wall until you make a full 180 turn
		int angle = (int) Math.abs(odometer.getAng() - finalAngle);
		while (angle > WF_FINAL_ANGLE_THRESHOLD) {
			int distance = opWall.getDistance();
			
			int bandCenter = WF_BANDCENTER;
			int bandWidth = WF_BANDWIDTH;
			
			// Right turn
			if (distance < (bandCenter - bandWidth)) {
				leftMotor.setSpeed(150);
				rightMotor.setSpeed(-150);
			}
			
			// Left turn
			else if (distance > (bandCenter + bandWidth)) {
				leftMotor.setSpeed(100);
				rightMotor.setSpeed(250);
			}
			
			// Straight
			else {
				leftMotor.setSpeed(200);
				rightMotor.setSpeed(200);
			}
			
			leftMotor.forward();
			rightMotor.forward();
			
			angle = (int) Math.abs(odometer.getAng() - finalAngle);
			if (angle > 360) {
				angle -= 360;
			}
		}
		
		leftMotor.stop(true);
		rightMotor.stop();
		
		opWall.pause();
		opWall.stop();
	}
}
