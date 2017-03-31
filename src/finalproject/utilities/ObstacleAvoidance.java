package finalproject.utilities;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;

public class ObstacleAvoidance extends Thread {
	
	private final static int OBJECT_DISTANCE = 15;
	
	private float[] usData;
	private EV3UltrasonicSensor usSensor;
	private SampleProvider us;
	private Navigation navigation;
	
	private boolean isRunning;
	private int distance;
	
	public ObstacleAvoidance(EV3UltrasonicSensor usSensor, Navigation navigation) {
		this.usSensor = usSensor;
		this.isRunning = false;
		this.navigation = navigation;
		
		if (!this.usSensor.isEnabled()) {
			this.usSensor.enable();
		}
		
		us = this.usSensor.getMode("Distance");
		usData = new float[us.sampleSize()];
	}
	
	/**
	 * This is the main method of this class.
	 */
	@Override
	public void run() {
		while (true) {
			if (isRunning) {
				us.fetchSample(usData,0);
				distance=(int)(usData[0]*100.0);
				
				if (distance < OBJECT_DISTANCE && !navigation.getObstacleDetected()) {
					Sound.beep();
					navigation.setObstacleDetected(true);
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
}
