//package finalproject.utilities.localization;
//
//import lejos.hardware.Sound;
//import lejos.hardware.motor.EV3LargeRegulatedMotor;
//import lejos.hardware.sensor.EV3ColorSensor;
//import lejos.robotics.Color;
//
//public class LightLocalizer {
//	// Change these to desired performance
//	private final double COLOR_THRESHOLD = 0.40;
//	private final double SENSOR_DISTANCE = 15.0;
//	
//	private float[] colorData;	
//	private EV3LargeRegulatedMotor leftMotor, rightMotor;
//	private Navigation navigation;
//	private Odometer odometer;
//	private EV3ColorSensor colorSensor;
//	
//	public LightLocalizer(EV3ColorSensor colorSensor, EV3LargeRegulatedMotor leftMotor, 
//			EV3LargeRegulatedMotor rightMotor, Odometer odometer, Navigation navigation, float[] colorData) {
//		this.colorSensor = colorSensor;
//		this.colorSensor.setFloodlight(Color.RED);
//		this.leftMotor = leftMotor;
//		this.rightMotor = rightMotor;
//		this.odometer = odometer;
//		this.navigation = navigation;
//		this.colorData = colorData;
//		Sound.setVolume(10);
//	}
//	
//	/**
//	 * Does light localization by acquiring four angle samples during rotation.
//	 */
//	public void doLocalization() {
//		int lineCount = 0;
//		
//		double[] theta = new double[4];
//		double thetaX, thetaY;
//		double x, y;
//		
//		// drive to location listed in tutorial
//		// start rotating and clock all 4 gridlines
//		
//		while(lineCount < 4){
//			colorSensor.getRedMode().fetchSample(colorData, 0);
//			 System.out.println(colorData[0]);
//			// Line detected
//			if(colorData[0] < COLOR_THRESHOLD){
//				Sound.playTone(1000, 250);
//				theta[lineCount] = odometer.getAng();
//				lineCount++;
//			}
//		}
//		
//		leftMotor.setSpeed(0);
//		rightMotor.setSpeed(0);
//		
//		// update thetaY and thetaX values
//		thetaY = Math.abs(theta[2] - theta[0]);
//		thetaX = Math.abs(theta[3] - theta[1]);
//		
//		
//		x = -SENSOR_DISTANCE * Math.cos(thetaY * Math.PI / 360);
//		y = -SENSOR_DISTANCE * Math.cos(thetaX * Math.PI / 360);
//		
//		odometer.setX(x);
//		odometer.setY(y);
//		
//		// when done travel to (0,0) and turn to 0 degrees
//		navigation.travelTo(0, 0);
//		navigation.turnTo(0.0, false);
//	}
//}
