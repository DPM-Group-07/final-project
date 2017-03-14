package finalproject.utilities.localization;
import java.util.Collections;
import java.util.LinkedList;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

public class USLocalizer{
	// Modify these to desired performance
	private final int ROTATION_SPEED = 40;
	private final int SAMPLE_SIZE = 5;
	private final double DETECTION_DISTANCE = 45.0;
		
	private EV3UltrasonicSensor usSensor;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Odometer odometer;
	private Navigation navigation;
	private float[] usData;
	
	public USLocalizer(EV3UltrasonicSensor usSensor, EV3LargeRegulatedMotor leftMotor, 
			EV3LargeRegulatedMotor rightMotor, Odometer odometer, Navigation navigation, float[] usData) {
		this.usSensor = usSensor;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.odometer = odometer;
		this.navigation = navigation;
		this.usData = usData;
	}
	
	/**
	 * Does localization by applying edge detection. 
	 */
	public void doLocalization() {
		double angleA = 0.0, angleB = 0.0;
		
		double currentUSData;
		
		// Falling edge detection		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		leftMotor.backward();
		rightMotor.forward();
	
		// Exterior for loop is to prevent false edge detection from Ultrasonic Sensor
		// Interior while loop rotates the robot until it sees no wall
		for(int i = 0; i < 5; i++){
			while(true){
				currentUSData = getFilteredData();
				if(currentUSData > DETECTION_DISTANCE){
					break;
				}
			}
		}
			
			
		// keep rotating until the robot sees the left wall, then latch the angle
			
		while(true){
			currentUSData = getFilteredData();
			if(currentUSData < DETECTION_DISTANCE){
				angleB = odometer.getAng();
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				break;
			}
		}
			
			
		// switch side
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		leftMotor.forward();
		rightMotor.backward();
		
		for(int i = 0; i < 5; i++){
			// keep rotating until the robot doesn't see a wall
			while(true){
				 currentUSData = getFilteredData();
				 if(currentUSData > DETECTION_DISTANCE){
					 break;
				 }
			}
		}
			
		// keep rotation until the robot sees the back wall, then latch the angle
		while(true){
			currentUSData = getFilteredData();
			if(currentUSData < DETECTION_DISTANCE){
				angleA = odometer.getAng();
				leftMotor.stop();
				rightMotor.stop();
				break;
			}
		}
		
		// keep rotating until the robot sees a wall, then latch the angle
			
		// angleA is clockwise from angleB, so assume the average of the
		// angles to the right of angleB is 45 degrees past 'north'
			
			
		if(angleA < angleB){
			odometer.setAng(odometer.getAng() + 45 - (angleB + angleA) / 2);
		}
		else{
			odometer.setAng(odometer.getAng() + 225 - (angleB + angleA) / 2);
		}
			
		
		// Rising edge detection
		/*
		// rotating counterclockwise
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		leftMotor.backward();
		rightMotor.forward();
			
		//if it detects the wall break
		for(int i=0; i<5; i++){
			while (true){
				double currentUSData = getFilteredData();
				if(currentUSData < DETECTION_DISTANCE){
					break;
				}
			}	
		}
			
		//keep rotating counterclockwise until it detects no wall, store the angle alpha
		while(true){
			double currentUSData = getFilteredData();
			if(currentUSData > DETECTION_DISTANCE){
				leftMotor.setSpeed(0);
				rightMotor.setSpeed(0);
				angleA = odo.getTheta();
				break;
			}
		}
			

		//rotate clockwise
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		leftMotor.forward();
		rightMotor.backward();
		
		//if it detects the wall break
		for(int i=0; i<5; i++){
			while(true){
				double currentUSData = getFilteredData();
				if(currentUSData < DETECTION_DISTANCE){
					break;
				}
			}
		}
			
		//keep rotating forward until the robot sees no wall, than latch the angle
		while(true){
			double currentUSData = getFilteredData();
			if(currentUSData > DETECTION_DISTANCE){
				leftMotor.stop();
				rightMotor.stop();
				angleB = odo.getTheta();
				break;
			}
		}

			
		if(angleA < angleB){
			odo.setTheta(odo.getTheta() + 45 - (angleB + angleA) / 2);
		}
		else{
			odo.setTheta(odo.getTheta() + 225 - (angleB + angleA) / 2);
		}*/
		
		
		// Drive straight for a bit to prepare for light localization
		navigation.turnTo(45, false);
		navigation.goForward(10.0);
	}
	
	/**
	 * Collects ultrasonic sensor data, sorts them, and returns the median.
	 * @return the median distance
	 */
	private double getFilteredData() {
		LinkedList<Double> filterData = new LinkedList<Double>();
	
		for(int i = 0; i < SAMPLE_SIZE; i++){
			usSensor.fetchSample(usData, 0);
			filterData.add(new Double(usData[0] * 100.0));
			Delay.msDelay(1000/(ROTATION_SPEED * SAMPLE_SIZE));
		}
		
		Collections.sort(filterData);
			
		// Obtain median
		return filterData.get(SAMPLE_SIZE/2);		
	}

}
