package finalproject.utilities.obstacleavoidance;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.utility.Timer;
import lejos.utility.TimerListener;

public class ObstaclePoller implements TimerListener {
	private static final int INTERVAL = 30;
	
	private Timer timer;
	private float[] usData;
	private EV3UltrasonicSensor usSensor;
	private SampleProvider us;
	private int distance;
	private boolean paused;
	private Object lock;
	
	public ObstaclePoller(EV3UltrasonicSensor usSensor) {
		this.usSensor = usSensor;
		this.usSensor.enable();
		
		lock = new Object();
		
		if (!this.usSensor.isEnabled()) {
			this.usSensor.enable();
		}
		
		us = this.usSensor.getMode("Distance");
		usData = new float[us.sampleSize()];
		
		this.timer = new Timer(INTERVAL, this);
		paused = false;
		this.timer.start();
	}

	@Override
	public void timedOut() {
		synchronized (lock) {
			if (!paused) {
				us.fetchSample(usData,0);
				distance=(int)(usData[0]*100.0);
			}
		}
	}
	
	public int getDistance() {
		return this.distance;
	}
	
	public void pause() {
		synchronized (lock) {
			this.paused = true;
			usSensor.disable();
		}
	}
	
	public void unpause() {
		synchronized (lock) {
			this.paused = false;
			usSensor.enable();
		}
	}
	
	public void stop() {
		this.timer.stop();
		this.usSensor.disable();
	}
}
