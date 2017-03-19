package finalproject.utilities.localization;

import finalproject.utilities.Navigation;
import finalproject.utilities.Odometer;
import finalproject.utilities.localization.USLocalizer.LocalizationType;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

/**
 * MasterLocalizer is the main localization class that performs both ultrasonic and
 * light localization.
 * @author maxsn
 *
 */
public class MasterLocalizer {
	
	private USLocalizer usLocalizer;
	private LightLocalizer lightLocalizer;
	private LocalizationType localizationType;
	
	private EV3UltrasonicSensor usSensor;
	private EV3ColorSensor colorSensor;
	private Odometer odometer;
	private Navigation navigation;
	
	/**
	 * Public constructor for MasterLocalizer must be called with valid references to all
	 * dependencies.
	 * @param odometer Main Odometer object for odometry.
	 * @param navigation Main Navigation class for navigation.
	 * @param usSensor A reference to an ultrasonic sensor used for US localization.
	 * @param colorSensor A reference to a color sensor for for light localization.
	 */
	public MasterLocalizer(Odometer odometer, Navigation navigation, EV3UltrasonicSensor usSensor, EV3ColorSensor colorSensor, LocalizationType localizationType) {
		this.odometer = odometer;
		this.navigation = navigation;
		this.usSensor = usSensor;
		this.colorSensor = colorSensor;
		this.localizationType = localizationType;
	}
	
	/**
	 * Performs localization. First, ultrasonic localization is performed. Then,
	 * light localization is performed.
	 */
	public void localize() {
		
		SampleProvider usValue = usSensor.getMode("Distance");
		float[] usData = new float[usValue.sampleSize()];	

		SampleProvider colorValue = colorSensor.getMode("Red");			// colorValue provides samples from this instance
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		usLocalizer = new USLocalizer(odometer, usValue, usData, localizationType);
		lightLocalizer = new LightLocalizer(odometer, colorValue, colorData);

		usLocalizer.doLocalization();
		
		Sound.twoBeeps();
		int button = Button.waitForAnyPress();
		
		if (button == Button.ID_ESCAPE) {
			System.exit(1);
		}
		lightLocalizer.doLocalization();
	}
}
