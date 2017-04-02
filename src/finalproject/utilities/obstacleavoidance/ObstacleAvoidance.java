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
	
	final static int WF_MOTOR_STRAIGHT = 200;
	final static int WF_MOTOR_LEFT_L = 100;
	final static int WF_MOTOR_LEFT_R = 250;
	final static int WF_MOTOR_RIGHT_L = 150;
	final static int WF_MOTOR_RIGHT_R = -150;
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
		double minAng = (Math.atan2(4 - odometer.getY(), 4 - odometer.getX())) * (180.0 / Math.PI);
		double error = navigation.getDifference(minAng, this.odometer.getAng());
		
		double initialAngle = odometer.getAng();
		
		EV3UltrasonicSensor wfSensor;
		EV3LargeRegulatedMotor innerMotor;
		EV3LargeRegulatedMotor outerMotor;
		if (error > 0) {
			// Need to turn right
			wfSensor = leftSensor;
			innerMotor = odometer.getLeftMotor();
			outerMotor = odometer.getRightMotor();
			navigation.turnTo(odometer.getAng() + 90.0, true);
		} else {
			// Need to turn left
			wfSensor = rightSensor;
			innerMotor = odometer.getRightMotor();
			outerMotor = odometer.getLeftMotor();
			navigation.turnTo(odometer.getAng() - 90.0, true);
		}
		
		ObstaclePoller opWall = new ObstaclePoller(wfSensor);
		
		// Start following the wall
		double finalAngle = odometer.getAng() - 180.0;
		
		// Follow the wall until you make a full 180 turn
		int angle = (int) Math.abs(odometer.getAng() - finalAngle);
		while (angle > WF_FINAL_ANGLE_THRESHOLD) {
			int distance = opWall.getDistance();
			
			int bandCenter = WF_BANDCENTER;
			int bandWidth = WF_BANDWIDTH;
			
			// Right turn
			if (distance < (bandCenter - bandWidth)) {
				innerMotor.setSpeed(WF_MOTOR_RIGHT_L);
				outerMotor.setSpeed(WF_MOTOR_RIGHT_R);
			}
			
			// Left turn
			else if (distance > (bandCenter + bandWidth)) {
				innerMotor.setSpeed(WF_MOTOR_LEFT_L);
				outerMotor.setSpeed(WF_MOTOR_LEFT_R);
			}
			
			// Straight
			else {
				innerMotor.setSpeed(WF_MOTOR_STRAIGHT);
				outerMotor.setSpeed(WF_MOTOR_STRAIGHT);
			}
			
			innerMotor.forward();
			outerMotor.forward();
			
			angle = (int) Math.abs(odometer.getAng() - finalAngle);
			if (angle > 360) {
				angle -= 360;
			}
		}
		
		innerMotor.stop(true);
		outerMotor.stop();
		
		navigation.turnTo(initialAngle, true);
		
		opWall.pause();
		opWall.stop();
	}
}
